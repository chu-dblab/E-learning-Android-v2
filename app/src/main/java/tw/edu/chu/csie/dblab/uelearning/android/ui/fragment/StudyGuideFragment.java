package tw.edu.chu.csie.dblab.uelearning.android.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.learning.ActivityManager;
import tw.edu.chu.csie.dblab.uelearning.android.learning.TargetManager;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.ui.LearningActivity;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.FileUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.TimeUtils;

/**
 * 學習引導畫面（顯示推薦學習點的地方）
 */
public class StudyGuideFragment  extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public int currentTId = 0;
    protected static final int REMAINED_TIME = 0x101;
    private String[] itemEnableActivity_default = {"Entry"};
    private int[] itemEnableActivity_tid = {0};
    private Date learningTime;

    private ListView mList_nextPoints;
    int list_select_nextPoint_item = -1; //一開始未選擇任何一個item所以為-1
    private SwipeRefreshLayout mSwipe_nextPoints;
    private TextView mText_remainedTime;
    private LinearLayout mLayout__nextPoint;
    private ImageView mImage_map;
    private Timer updateUITimer;
    private LinearLayout mLayout_finishStudy;
    private Button mBtn_finishStudy;

    public static StudyGuideFragment newInstance(int sectionNumber) {
        StudyGuideFragment fragment = new StudyGuideFragment();
        return fragment;
    }

    public StudyGuideFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_study_guide, container, false);
        learningTime = ActivityManager.getRemainderLearningTime(getActivity());
        initUI(rootView);
        // 若還沒有推薦的學習點
        if(!TargetManager.isHaveRecommand(getActivity())) {
            currentTId = TargetManager.getStartTargetId(getActivity());
            updateNextPoint(currentTId);
        }

        return rootView;
    }

    protected void initUI(View rootView) {

        mList_nextPoints = (ListView) rootView.findViewById(R.id.list_learning_next_points);
        mList_nextPoints.setOnItemClickListener(this);
        mSwipe_nextPoints = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_next_points);
        mSwipe_nextPoints.setOnRefreshListener(this);
        mText_remainedTime = (TextView) rootView.findViewById(R.id.text_learning_remaining_time);
        mLayout__nextPoint = (LinearLayout) rootView.findViewById(R.id.layout_learning_next_point);
        mImage_map = (ImageView) rootView.findViewById(R.id.image_learning_next_points);
        mLayout_finishStudy = (LinearLayout) rootView.findViewById(R.id.layout_finish_study_activity);
        mBtn_finishStudy = (Button) rootView.findViewById(R.id.btn_finish_study_activity);
        mBtn_finishStudy.setOnClickListener(this);

        Message message = new Message();
        message.what = StudyGuideFragment.REMAINED_TIME;
        updateUIHandler.sendMessage(message);

        ArrayAdapter<String> arrayData ;
        arrayData = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice, itemEnableActivity_default);
        mList_nextPoints.setAdapter(arrayData);
        mList_nextPoints.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mList_nextPoints.setOnItemClickListener(this);
        updateUI();

    }

    // ============================================================================================

    /**
     * 取得下一個推薦學習點
     */
    public void updateNextPoint(int currentTId) {
        mSwipe_nextPoints.setRefreshing(true);

        this.currentTId = currentTId;

        if (Config.DEBUG_SHOW_MESSAGE) {
            Toast.makeText(getActivity(), "推薦中...", Toast.LENGTH_SHORT).show();
        }

        final DBProvider db = new DBProvider(getActivity());
        String token = db.get_token();
        db.removeAll_recommand();

        // 取得目前學習活動資料
        Cursor query_activity = db.get_activity();
        query_activity.moveToFirst();
        int saId = query_activity.getInt(query_activity.getColumnIndex("SaID"));
        int enableVirtualInt = query_activity.getInt(query_activity.getColumnIndex("EnableVirtual"));
        boolean enableVirtual;
        if (enableVirtualInt > 0) enableVirtual = true;
        else enableVirtual = false;

        final RequestParams recommand_params = new RequestParams();
        try {
            UElearningRestClient.post("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) +
                    "/activitys/" + saId + "/recommand?current_point=" + currentTId, recommand_params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String content = null;

                    try {
                        content = new String(responseBody, "UTF-8");
                        JSONObject response = new JSONObject(content);
                        JSONArray jsonAry_targets = response.getJSONArray("recommand_target");

                        // 使用資料庫
                        DBProvider db = new DBProvider(getActivity());
                        db.removeAll_recommand();

                        // 抓取是否已結束
                        boolean isEnd = response.getBoolean("is_end");

                        // 還沒結束的話
                        if (!isEnd) {

                            // 抓所有推薦的標的
                            for (int i = 0; i < jsonAry_targets.length(); i++) {
                                JSONObject thisTarget = jsonAry_targets.getJSONObject(i);

                                int tId = thisTarget.getInt("target_id");
                                boolean isEntity = thisTarget.getBoolean("is_entity");

                                // 記錄進資料庫
                                db.insert_recommand(tId, isEntity);
                            }
                        }
                        // 已經結束的話
                        else {

                            // TODO: 改以隨時取得已學習標的數
                            ActivityManager.setLearnedPointTotal(getActivity(),
                                    ActivityManager.getPointTotal(getActivity()) - 1);
                        }

                    } catch (JSONException e) {
                        ErrorUtils.error(getActivity(), e);
                    } catch (UnsupportedEncodingException e) {
                        ErrorUtils.error(getActivity(), e);
                    }

                    // 介面調整
                    mSwipe_nextPoints.setRefreshing(false);
                    list_select_nextPoint_item = 0;
                    updateUI();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    // 介面調整
                    mSwipe_nextPoints.setRefreshing(false);
                    list_select_nextPoint_item = 0;
                    updateUI();
                }
            });
        } catch (UnsupportedEncodingException e) {
            ErrorUtils.error(getActivity(), e);
        }

    }

    /**
     * 重新整理介面
     */
    public void updateUI() {

        DBProvider db = new DBProvider(getActivity());
        Cursor query = db.getAll_recommand();

        int total = query.getCount();
        String[] itemEnableActivity;
        if(total > 0) {

            itemEnableActivity = new String[total];
            itemEnableActivity_tid = new int[total];

            for(int i=0; i<total; i++) {
                query.moveToPosition(i);

                int tId = query.getInt(query.getColumnIndex("TID"));
                int isEntityInt = query.getInt(query.getColumnIndex("IsEntity"));
                boolean isEntity;
                if(isEntityInt>0) isEntity = true;
                else isEntity = false;

                Cursor query_t = db.get_target(tId);
                query_t.moveToFirst();

                //Integer hId = query_t.getInt(query_t.getColumnIndex("HID"));
                //String hName = query_t.getString(query_t.getColumnIndex("HName"));
                //Integer aId = query_t.getInt(query_t.getColumnIndex("AID"));
                //String aName = query_t.getString(query_t.getColumnIndex("AName"));
                //Integer aFloor = query_t.getInt(query_t.getColumnIndex("AFloor"));
                //Integer aNum = query_t.getInt(query_t.getColumnIndex("ANum"));
                //Integer tNum = query_t.getInt(query_t.getColumnIndex("TNum"));
                String tName = query_t.getString(query_t.getColumnIndex("TName"));
                int learnTime = query_t.getInt(query_t.getColumnIndex("LearnTime"));

                itemEnableActivity_tid[i] = tId;
                itemEnableActivity[i] = new String(tId + ". "+tName + " ("+learnTime+" min)");

            }
            mBtn_finishStudy.setVisibility(View.GONE);
        }
        else {
            int startTId = TargetManager.getStartTargetId(getActivity());
            itemEnableActivity = new String[1];
            itemEnableActivity_tid = new int[1];
            itemEnableActivity_tid[0] = startTId;
            itemEnableActivity[0] = new String(getString(R.string.start_target));

            // 若已經學習完成的話
            if(ActivityManager.getRemainingPointTotal(getActivity()) <= 0) {
                mBtn_finishStudy.setVisibility(View.VISIBLE);
                mLayout_finishStudy.setVisibility(View.VISIBLE);
            }
        }

        ArrayAdapter<String> arrayData ;
        arrayData = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, itemEnableActivity);
        mList_nextPoints.setAdapter(arrayData);
        updateSelectNextPointsUI();
    }

    // ============================================================================================

    Handler updateUIHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch(msg.what) {
                case REMAINED_TIME:
                    long milliseconds = learningTime.getTime() - 1000;
                    if(milliseconds > 0) {
                        learningTime.setTime(learningTime.getTime() - 1000);
                    }
                    else {
                        learningTime.setTime(0);
                    }
                    mText_remainedTime.setText(TimeUtils.timerToString(learningTime));
                    break;
            }
        };
    };

    class UpdateUITask extends TimerTask {

        @Override
        public void run() {
            Message message = new Message();
            message.what = StudyGuideFragment.REMAINED_TIME;

            updateUIHandler.sendMessage(message);
        }

    }

    public void stopUpdateUITask() {
        updateUITimer.cancel();
    }

    // ============================================================================================

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 裝置螢幕旋轉時，重新載入界面
        // Get a layout inflater (inflater from getActivity() or getSupportActivity() works as well)
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newView = inflater.inflate(R.layout.fragment_study_guide, null);
        // This just inflates the view but doesn't add it to any thing.
        // You need to add it to the root view of the fragment
        ViewGroup rootView = (ViewGroup) getView();
        // Remove all the existing views from the root view.
        // This is also a good place to recycle any resources you won't need anymore
        rootView.removeAllViews();
        rootView.addView(newView);

        initUI(rootView);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.btn_finish_study_activity) {
            ((LearningActivity)getActivity()).finishStudyActivity();
        }
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        list_select_nextPoint_item = position;//保存目前的View位置
        updateSelectNextPointsUI();
    }

    public void updateSelectNextPointsUI() {

        // 當已經有選取任何一項
        if(list_select_nextPoint_item != -1) {
            mList_nextPoints.setItemChecked(list_select_nextPoint_item, true);

            int tid = itemEnableActivity_tid[ list_select_nextPoint_item ];
            String map_filename = FileUtils.getMapFilePath(getActivity(), tid);
            Bitmap mBitmap_map = BitmapFactory.decodeFile(map_filename);

            mImage_map.setImageBitmap(mBitmap_map);
        }
    }

    @Override
    public void onRefresh() {
        updateNextPoint(currentTId);
    }

    @Override
    public void onPause() {
        updateUITimer.cancel();
        super.onPause();
    }

    @Override
    public void onStop() {
        updateUITimer.cancel();
        super.onStop();
    }

    @Override
    public void onResume() {
        learningTime = ActivityManager.getRemainderLearningTime(getActivity());
        updateUITimer = new Timer();
        updateUITimer.schedule(new UpdateUITask(), 0, 1 * 1000);
        super.onResume();
    }
}