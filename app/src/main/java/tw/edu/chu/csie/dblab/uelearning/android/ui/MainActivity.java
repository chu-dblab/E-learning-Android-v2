package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.learning.ActivityManager;
import tw.edu.chu.csie.dblab.uelearning.android.learning.UserUtils;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestHandler;
import tw.edu.chu.csie.dblab.uelearning.android.ui.dialog.StartStudyActivityDialog;
import tw.edu.chu.csie.dblab.uelearning.android.ui.dialog.StartWillStudyActivityLockDialog;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.HelpUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.NetworkUtils;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private TextView mText_nickname, mText_userInfo, mText_realname, mText_classname, mText_groupname;
    private SwipeRefreshLayout mSwipe_activity;
    private ListView mListView_activity;
    ProgressDialog mProgress_start_studyActivity;

    private int[] itemSerial;
    private String[] itemEnableActivity;
    private String[] subitemEnableActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ActionBar對應
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // UI對應
        mText_nickname = (TextView) findViewById(R.id.text_nickname);
        mText_userInfo = (TextView) findViewById(R.id.text_user_info);
//        mText_realname = (TextView) findViewById(R.id.text_realname);
//        mText_classname = (TextView) findViewById(R.id.text_classname);
//        mText_groupname = (TextView) findViewById(R.id.text_groups);

        mListView_activity = (ListView) findViewById(R.id.listView_activity);
        mListView_activity.setOnItemClickListener(this);

        // 從資料庫取得個人資訊
        DBProvider db = new DBProvider(MainActivity.this);
        Cursor the_user_query = db.get_user();
        the_user_query.moveToFirst();

        String nickname = the_user_query.getString( the_user_query.getColumnIndex("NickName") );
        String realname = the_user_query.getString( the_user_query.getColumnIndex("RealName") );
        String classname = the_user_query.getString( the_user_query.getColumnIndex("CName") );
        String groupname = the_user_query.getString( the_user_query.getColumnIndex("GName") );

        String userInfo = "";
        if(realname != null) { userInfo += realname+", "; }
        if(classname != null) { userInfo += classname+", "; }
        if(groupname != null) { userInfo += groupname; }

        // 顯示個人資訊在介面上
        mText_nickname.setText(nickname);
//        mText_realname.setText(realname);
//        mText_classname.setText(classname);
//        mText_groupname.setText(groupname);
        mText_userInfo.setText(userInfo);

        // SwipeLayout: 可用的學習活動清單
        mSwipe_activity = (SwipeRefreshLayout) findViewById(R.id.swipe_activity);
        mSwipe_activity.setOnRefreshListener(this);

        // 登入中畫面
        mProgress_start_studyActivity = new ProgressDialog(MainActivity.this);
        mProgress_start_studyActivity.setMessage(getResources().getString(R.string.starting_study_activity));
        mProgress_start_studyActivity.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress_start_studyActivity.setIndeterminate(true);
        // TODO: 設計成可中途取消的功能
        mProgress_start_studyActivity.setCancelable(false);

        // 抓取可用的學習活動
        // TODO: 若正在抓取中旋轉螢幕的話，有可能會造成重新抓取，需修正
        if(!isExistEnableStudyActivity()) {
            getStudyActivityList();
        }
        else {
            updateStudyActivityUI();
        }
    }

    public boolean isExistEnableStudyActivity() {
        DBProvider db = new DBProvider(MainActivity.this);
        Cursor query = db.getAll_enableActivity();
        return query.getCount() > 0;
    }

    /**
     * 取得學習活動
     */
    public void getStudyActivityList() {

        ActivityManager.updateEnableActivityList(MainActivity.this, new UElearningRestHandler() {

            @Override
            public void onStart() {
                super.onStart();
                mSwipe_activity.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mSwipe_activity.setRefreshing(false);

                // 更新目前可用的學習活動清單到介面上
                updateStudyActivityUI();
            }

            @Override
            public void onRetry(int retryNo) {
                Toast.makeText(MainActivity.this, "Retry:"+retryNo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoLogin() {
                super.onNoLogin();
                mSwipe_activity.setRefreshing(false);
            }

            @Override
            public void onNoResponse() {
                mSwipe_activity.setRefreshing(false);
            }

            @Override
            public void onOtherErr(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ErrorUtils.error(MainActivity.this, error);
            }

            @Override
            public void onOtherErr(Throwable e) {
                mSwipe_activity.setRefreshing(false);

                ErrorUtils.error(MainActivity.this, e);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(NetworkUtils.isNetworkConnected(MainActivity.this)){
            updateStudyActivityUI();
        }
        else {
            NetworkUtils.showNoNetworkDialog(MainActivity.this, false);
        }
    }


    /**
     * 更新可用的學習活動清單到介面
     */
    public void updateStudyActivityUI() {

        // 從資料庫取得學習活動資料
        DBProvider db = new DBProvider(MainActivity.this);
        Cursor queryEA = db.getAll_enableActivity();

        int total = queryEA.getCount();
        int[] serial = new int[total];
        int[] type = new int[total];
        String[] typeDisplay = new String[total];
        int[] id = new int[total];
        int[] themeId = new int[total];
        String[] themeName = new String[total];

        // 取得資料
        for(int i=0; i<total; i++) {
            queryEA.moveToPosition(i);

            serial[i]    = queryEA.getInt( queryEA.getColumnIndex("Serial") );
            type[i]      = queryEA.getInt( queryEA.getColumnIndex("Type") );
            themeId[i]   = queryEA.getInt( queryEA.getColumnIndex("ThID") );
            themeName[i] = queryEA.getString( queryEA.getColumnIndex("ThName") );

            // TODO: 將字串拉出來成String.xml
            if(type[i] == DBProvider.TYPE_STUDY) { typeDisplay[i] = "學習中"; }
            else if(type[i] == DBProvider.TYPE_WILL) { typeDisplay[i] = "預約"; }
            else if(type[i] == DBProvider.TYPE_THEME) { typeDisplay[i] = "主題"; }

        }
        // Listview: 可用的學習活動清單
        itemEnableActivity = new String[total];
        subitemEnableActivity = new String[total];
        itemSerial = new int[total];

        for(int i=0; i<total; i++) {
            itemEnableActivity[i] = typeDisplay[i]+": "+themeName[i];
            subitemEnableActivity[i] = themeName[i];
            itemSerial[i] = serial[i];
        }

        ArrayAdapter<String> listAdapter;
        listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, itemEnableActivity);
        mListView_activity.setAdapter(listAdapter);
    }

    /**
     * 點選指定的學習活動
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

        // 取得其中的可用學習活動資訊
        DBProvider db = new DBProvider(MainActivity.this);
        Cursor query = db.get_enableActivity(itemSerial[position]);
        query.moveToFirst();

        int type = query.getInt(query.getColumnIndex("Type"));
        int thId = query.getInt( query.getColumnIndex("ThID") );
        String thName = query.getString( query.getColumnIndex("ThName") );
        int learnTime;
        int timeForce_int;
        int lMode;
        int lForce_int;
        String mMode;



        if(Config.DEBUG_SHOW_MESSAGE) {
            Toast.makeText(MainActivity.this, "你選擇的是"+ query.getInt(0), Toast.LENGTH_SHORT).show();
        }

        if(type == DBProvider.TYPE_STUDY) {

            int saId = query.getInt( query.getColumnIndex("SaID") );
            resumeStudyActivity(saId);
        }
        else if(type == DBProvider.TYPE_WILL) {

            AlertDialog startDialog =
                    new StartWillStudyActivityLockDialog(MainActivity.this, itemSerial[position]).create();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(startDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            startDialog.getWindow().setAttributes(lp);

            startDialog.show();
        }
        else {

            //Dialog
            AlertDialog startDialog =
                    new StartStudyActivityDialog(MainActivity.this, itemSerial[position]).create();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(startDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            startDialog.getWindow().setAttributes(lp);

            startDialog.show();
            //startStudyActivity(thId, thName, null, null, null, null, null);
        }

    }

    /**
     * 開始進行學習
     *
     * @param thId 主題編號
     * @param thName 主題名稱
     * @param _learnTime 學習時間
     * @param _timeForce 時間到強制結束學習
     * @param _lMode 學習導引模式
     * @param _lForce 強制學習導引
     * @param _mMode 教材模式
     */
    public void startStudyActivity(final int thId, final String thName,
                                   final Integer _learnTime, final Boolean _timeForce,
                                   final Integer _lMode, final Boolean _lForce, final String _mMode) {


        ActivityManager.startStudyActivity(MainActivity.this, thId, _learnTime, _timeForce, _lMode, _lForce, _mMode, new UElearningRestHandler() {

            @Override
            public void onStart() {
                mProgress_start_studyActivity.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgress_start_studyActivity.dismiss();

                // 進入學習畫面
                Intent toLearning = new Intent(MainActivity.this, LearningActivity.class);
                startActivity(toLearning);
            }

            @Override
            public void onNoResponse() {
                mProgress_start_studyActivity.dismiss();
            }

            @Override
            public void onOtherErr(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgress_start_studyActivity.dismiss();
                ErrorUtils.error(MainActivity.this, error);
            }

            @Override
            public void onOtherErr(Throwable e) {
                mProgress_start_studyActivity.dismiss();
                ErrorUtils.error(MainActivity.this, e);
            }
        });

    }

    public void resumeStudyActivity(final int saId) {

        ActivityManager.resumeStudyActivity(MainActivity.this, saId, new UElearningRestHandler() {
            @Override
            public void onStart() {
                mProgress_start_studyActivity.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgress_start_studyActivity.dismiss();

                // 進入學習畫面
                Intent toLearning = new Intent(MainActivity.this, LearningActivity.class);
                startActivity(toLearning);
            }

            @Override
            public void onNoResponse() {

            }

            @Override
            public void onOtherErr(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ErrorUtils.error(MainActivity.this, error);
            }

            @Override
            public void onOtherErr(Throwable e) {
                ErrorUtils.error(MainActivity.this, e);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(Config.DEBUG_ACTIVITY) {
            menu.findItem(R.id.menu_inside_tester).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.menu_refresh) {
            getStudyActivityList();
            return true;
        }
        else if(id == R.id.menu_logout) {

            DBProvider db = new DBProvider(MainActivity.this);

            // 抓取目前已登入的Token
            String token = db.get_token();

            // 告訴伺服端說已登出
            UserUtils.userLogout(MainActivity.this, new UElearningRestHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (Config.DEBUG_SHOW_MESSAGE) {
                        Toast.makeText(MainActivity.this, "伺服器已接受登出", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNoResponse() {

                }

                @Override
                public void onOtherErr(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    ErrorUtils.error(MainActivity.this, error);
                }

                @Override
                public void onOtherErr(Throwable e) {
                    ErrorUtils.error(MainActivity.this, e);
                }
            });

            // 回到登入畫面
            finish();
            Intent to_login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(to_login);
            return true;
        }
        else if(id == R.id.menu_log_manager) {
            Intent toLogMgr = new Intent(MainActivity.this, LogActivity.class);
            startActivity(toLogMgr);
        }
        else if (id == R.id.menu_about) {
            HelpUtils.showAboutDialog(MainActivity.this);
            return true;
        }
        else if(id == R.id.menu_inside_tester) {
            Intent toTester = new Intent(MainActivity.this, TesterActivity.class);
            startActivity(toTester);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        getStudyActivityList();
    }

}
