package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.HelpUtils;

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
        DBProvider db = new DBProvider(MainActivity.this);

        // 抓取目前已登入的Token
        String token = db.get_token();


        try {
            UElearningRestClient.get("/tokens/"+URLEncoder.encode(token, HTTP.UTF_8)+"/activitys",
                    null, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    mSwipe_activity.setRefreshing(true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    mSwipe_activity.setRefreshing(false);

                    String content;
                    try {
                        content = new String(responseBody, HTTP.UTF_8);
                        JSONObject response = new JSONObject(content);

                        JSONArray jsonArr_enableStudy = response.getJSONArray("enable_activity");

                        // 清除目前的活動清單
                        DBProvider db = new DBProvider(MainActivity.this);
                        db.removeAll_enableActivity();

                        // 抓其中一個活動
                        for(int i=0; i<jsonArr_enableStudy.length(); i++) {
                            JSONObject thisActivity = jsonArr_enableStudy.getJSONObject(i);

                            // 抓取資料
                            String type = thisActivity.getString("type");
                            Integer saId = null;
                            if(!thisActivity.isNull("activity_id")) {
                                saId = thisActivity.getInt("activity_id");
                            }
                            Integer swId = null;
                            if(!thisActivity.isNull("activity_will_id")) {
                                swId = thisActivity.getInt("activity_will_id");
                            }
                            int thId = thisActivity.getInt("theme_id");
                            String thName = thisActivity.getString("theme_name");
                            String thIntroduction = thisActivity.getString("theme_introduction");
                            String startTime = thisActivity.getString("start_time");
                            String expiredTime = thisActivity.getString("expired_time");
                            int learnTime = thisActivity.getInt("remaining_time");
                            Boolean timeForce = thisActivity.getBoolean("time_force");
                            Integer lMode = null;
                            if(!thisActivity.isNull("learnStyle_mode")) {
                                lMode = thisActivity.getInt("learnStyle_mode");
                            }
                            Boolean lForce = thisActivity.getBoolean("learnStyle_force");
                            Boolean enableVirtual = thisActivity.getBoolean("enable_virtual");
                            String mMode = thisActivity.getString("material_mode");
                            Boolean lock = thisActivity.getBoolean("lock");
                            int targetTotal = thisActivity.getInt("target_total");
                            int learnedTotal = thisActivity.getInt("learned_total");

                            int typeId;
                            if(type.equals("theme")) typeId = DBProvider.TYPE_THEME;
                            else if(type.equals("will")) typeId = DBProvider.TYPE_WILL;
                            else if(type.equals("study")) typeId = DBProvider.TYPE_STUDY;
                            else typeId = 0;

                            // 紀錄進資料庫裡
                            db.insert_enableActivity(db.get_user_id(), typeId, saId, swId,
                                    thId, thName, thIntroduction, startTime, expiredTime,
                                    learnTime, timeForce, lMode, lForce, enableVirtual, mMode,
                                    lock, targetTotal, learnedTotal);
                        }

                    } catch (UnsupportedEncodingException | JSONException e) {
                        ErrorUtils.error(MainActivity.this, e);
                    }

                    // 更新目前可用的學習活動清單到介面上
                    updateStudyActivityUI();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    mSwipe_activity.setRefreshing(false);

                    if(responseBody != null) {

                        try {
                            // TODO: 取得可用的學習活動失敗的錯誤處理
                            String content = new String(responseBody, HTTP.UTF_8);
                            if(Config.DEBUG_SHOW_MESSAGE) {
                                Toast.makeText(MainActivity.this,
                                        "s: "+statusCode+"\n"
                                                + getResources().getString(R.string.get_fail_enableActivity)+"\n"+content,
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, R.string.get_fail_enableActivity, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (UnsupportedEncodingException e) {
                            ErrorUtils.error(MainActivity.this, e);
                        }
                    } else {
                        ErrorUtils.error(MainActivity.this, error);
                    }

                }
            });
        } catch (UnsupportedEncodingException e) {
            ErrorUtils.error(MainActivity.this, e);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStudyActivityUI();
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
        //ArrayAdapter listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_2,list){
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

            learnTime     = query.getInt   ( query.getColumnIndex("LearnTime") );
            timeForce_int = query.getInt   ( query.getColumnIndex("TimeForce") );
            lMode         = query.getInt   ( query.getColumnIndex("LMode")     );
            lForce_int    = query.getInt   ( query.getColumnIndex("LForce")    );
            mMode         = query.getString( query.getColumnIndex("") );

            boolean timeForce;
            if(timeForce_int>=1) timeForce = true;
            else timeForce = false;
            boolean lForce;
            if(lForce_int>=1) lForce = true;
            else lForce = false;

            startStudyActivity(thId, thName, learnTime, timeForce, lMode, lForce, mMode);
        }
        else/* if(type == DBProvider.TYPE_THEME) */{
            startStudyActivity(thId, thName, null, null, null, null, null);
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

        final DBProvider db = new DBProvider(MainActivity.this);

        // 抓取目前已登入的Token
        final String token = db.get_token();

        // 帶入登入參數
        final RequestParams startActivity_params = new RequestParams();
        startActivity_params.put("theme_id", thId);
        if(_learnTime != null) startActivity_params.put("learn_time", _learnTime);
        if(_timeForce != null) startActivity_params.put("time_force", _timeForce);
        if(_lMode != null)     startActivity_params.put("learnStyle_mode", _lMode);
        if(_lForce != null)    startActivity_params.put("learnStyle_force", _lForce);
        if(_mMode != null)     startActivity_params.put("material_mode", _mMode);

        // 對伺服器加入新的學習活動
        try {
            UElearningRestClient.post("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) + "/activitys",
                    startActivity_params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    mProgress_start_studyActivity.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String content = null;
                    try {
                        content = new String(responseBody, "UTF-8");
                        final JSONObject response = new JSONObject(content);
                        JSONObject activityJson = response.getJSONObject("activity");

                        // TODO: 對照輸入的資訊與伺服端接到的資訊是否吻合
                        int saId = activityJson.getInt("activity_id");
                        String startTime = activityJson.getString("start_time");
                        String expiredTime = activityJson.getString("expired_time");
                        int targetTotal = activityJson.getInt("target_total");
                        int learnedTotal = activityJson.getInt("learned_total");
                        int learnTime = activityJson.getInt("have_time");
                        boolean timeForce;
                        if(activityJson.getString("time_force") == "true")
                            timeForce = true;
                        else timeForce = false;
                        int lMode = activityJson.getInt("learnStyle_mode");
                        boolean lForce;
                        if(activityJson.getString("learnStyle_force") == "true")
                            lForce = true;
                        else lForce = false;
                        boolean enableVirtual;
                        if(activityJson.getString("enable_virtual") == "true")
                            enableVirtual = true;
                        else enableVirtual = false;
                        String mMode = activityJson.getString("material_mode");

                        // 紀錄進資料庫
                        DBProvider db = new DBProvider(MainActivity.this);
                        db.removeAll_activity();
                        db.insert_activity(db.get_user_id(), saId,
                                thId, thName, startTime, learnTime, timeForce,
                                lMode, lForce, enableVirtual, mMode, targetTotal, learnedTotal);
                        db.insert_enableActivity(db.get_user_id(), DBProvider.TYPE_STUDY,
                                saId, null, thId, thName, null,
                                startTime, expiredTime, learnTime, timeForce,
                                lMode, lForce, enableVirtual, mMode, true, targetTotal, learnedTotal);

                        // 向伺服端取得今次活動所有的標的資訊
                        UElearningRestClient.get("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) + "/activitys/" + saId + "/points",
                                null, new AsyncHttpResponseHandler() {

                                /**
                                 * Fired when a request returns successfully, override to handle in your own code
                                 *
                                 * @param statusCode   the status code of the response
                                 * @param headers      return headers, if any
                                 * @param responseBody the body of the HTTP response from the server
                                 */
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                                    String content = null;
                                    try {
                                        content = new String(responseBody, "UTF-8");
                                        JSONObject response = new JSONObject(content);
                                        JSONArray jsonAtt_targets = response.getJSONArray("targets");

                                        // 使用資料庫
                                        DBProvider db = new DBProvider(MainActivity.this);
                                        db.removeAll_target();

                                        // 抓其中一個活動
                                        for (int i = 0; i < jsonAtt_targets.length(); i++) {
                                            JSONObject thisTarget = jsonAtt_targets.getJSONObject(i);

                                            int thId = thisTarget.getInt("theme_id");
                                            int tId = thisTarget.getInt("target_id");
                                            Integer hId = null;
                                            if(!thisTarget.isNull("hall_id")) {
                                                hId = thisTarget.getInt("hall_id");
                                            }
                                            String hName = thisTarget.getString("hall_name");
                                            Integer aId = null;
                                            if(!thisTarget.isNull("area_id")) {
                                                aId = thisTarget.getInt("area_id");
                                            }
                                            String aName = thisTarget.getString("area_name");
                                            Integer aFloor = null;
                                            if(!thisTarget.isNull("floor")) {
                                                aFloor = thisTarget.getInt("floor");
                                            }
                                            Integer aNum = null;
                                            if(!thisTarget.isNull("area_number")) {
                                                aNum = thisTarget.getInt("area_number");
                                            }
                                            Integer tNum = null;
                                            if(!thisTarget.isNull("target_number")) {
                                                tNum = thisTarget.getInt("target_number");
                                            }
                                            String tName = thisTarget.getString("name");
                                            int learnTime = thisTarget.getInt("learn_time");
                                            String mapUrl = thisTarget.getString("map_url");
                                            String materialUrl = thisTarget.getString("material_url");
                                            String virtualMaterialUrl = thisTarget.getString("virtual_material_url");

                                            // 記錄進資料庫
                                            db.insert_target(thId, tId, hId, hName, aId, aName, aFloor, aNum, tNum, tName, learnTime, mapUrl, materialUrl, virtualMaterialUrl);
                                        }

                                        mProgress_start_studyActivity.dismiss();

                                        // 進入學習畫面
                                        Intent toLearning = new Intent(MainActivity.this, LearningActivity.class);
                                        startActivity(toLearning);
                                    } catch (UnsupportedEncodingException e) {
                                        mProgress_start_studyActivity.dismiss();
                                        ErrorUtils.error(MainActivity.this, e);
                                    } catch (JSONException e) {
                                        mProgress_start_studyActivity.dismiss();
                                        ErrorUtils.error(MainActivity.this, e);
                                    }
                                }


                                /**
                                 * Fired when a request fails to complete, override to handle in your own code
                                 *
                                 * @param statusCode   return HTTP status code
                                 * @param headers      return headers, if any
                                 * @param responseBody the response body, if any
                                 * @param error        the underlying cause of the failure
                                 */
                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                    mProgress_start_studyActivity.dismiss();
                                    if(responseBody != null) {

                                        try {
                                            // TODO: 取得可用的學習活動失敗的錯誤處理
                                            String content = new String(responseBody, HTTP.UTF_8);
                                            if(Config.DEBUG_SHOW_MESSAGE) {
                                                Toast.makeText(MainActivity.this,
                                                        "s: " + statusCode + "\n" + content,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(MainActivity.this, R.string.inside_error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        catch (UnsupportedEncodingException e) {
                                            ErrorUtils.error(MainActivity.this, e);
                                        }
                                    }
                                    else {
                                        ErrorUtils.error(MainActivity.this, error);
                                    }
                                }
                        });

                    }
                    catch (UnsupportedEncodingException e) {
                        ErrorUtils.error(MainActivity.this, e);
                    }
                    catch (JSONException e) {
                        ErrorUtils.error(MainActivity.this, e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    mProgress_start_studyActivity.dismiss();

                    if(responseBody != null) {

                        try {
                            // TODO: 取得可用的學習活動失敗的錯誤處理
                            String content = new String(responseBody, HTTP.UTF_8);
                            if(Config.DEBUG_SHOW_MESSAGE) {
                                Toast.makeText(MainActivity.this,
                                        "s: " + statusCode + "\n" + content,
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, R.string.inside_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (UnsupportedEncodingException e) {
                            ErrorUtils.error(MainActivity.this, e);
                        }
                    }
                    else {
                        ErrorUtils.error(MainActivity.this, error);
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            ErrorUtils.error(MainActivity.this, e);
        }
    }

    public void resumeStudyActivity(final int saId) {

        final DBProvider db = new DBProvider(MainActivity.this);

        // 抓取目前已登入的Token
        String token = db.get_token();

        // 查訊伺服器那邊的學習狀況資料
        try {
            UElearningRestClient.get("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) +
                            "/activitys/" + saId, null, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    mProgress_start_studyActivity.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    mProgress_start_studyActivity.dismiss();

                    String content = null;
                    try {
                        content = new String(responseBody, "UTF-8");
                        JSONObject response = new JSONObject(content);
                        JSONObject activityJson = response.getJSONObject("activity");

                        int saId = activityJson.getInt("activity_id");
                        int thId = activityJson.getInt("theme_id");
                        String thName = activityJson.getString("theme_name");
                        String startTime = activityJson.getString("start_time");
                        String expiredTime = activityJson.getString("expired_time");
                        int targetTotal = activityJson.getInt("target_total");
                        int learnedTotal = activityJson.getInt("learned_total");
                        int learnTime = activityJson.getInt("have_time");
                        boolean timeForce;
                        if(activityJson.getString("time_force") == "true")
                            timeForce = true;
                        else timeForce = false;
                        int lMode = activityJson.getInt("learnStyle_mode");
                        boolean lForce;

                        if(activityJson.getString("learnStyle_force") == "true")
                            lForce = true;
                        else lForce = false;
                        boolean enableVirtual;
                        if(activityJson.getString("enable_virtual") == "true")
                            enableVirtual = true;
                        else enableVirtual = false;
                        String mMode = activityJson.getString("material_mode");

                        // 紀錄進資料庫
                        DBProvider db = new DBProvider(MainActivity.this);
                        db.removeAll_activity();
                        db.insert_activity(db.get_user_id(), saId,
                                thId, thName, startTime, learnTime, timeForce,
                                lMode, lForce, enableVirtual, mMode, targetTotal, learnedTotal);

                        // 進入學習畫面
                        Intent toLearning = new Intent(MainActivity.this, LearningActivity.class);
                        startActivity(toLearning);

                    } catch (JSONException e) {
                        ErrorUtils.error(MainActivity.this, e);
                    } catch (UnsupportedEncodingException e) {
                        ErrorUtils.error(MainActivity.this, e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    mProgress_start_studyActivity.dismiss();

                    if(responseBody != null) {
                        try {
                            // TODO: 取得可用的學習活動失敗的錯誤處理
                            String content = new String(responseBody, HTTP.UTF_8);
                            if(Config.DEBUG_SHOW_MESSAGE) {
                                Toast.makeText(MainActivity.this,
                                        "s: " + statusCode + "\n" + content,
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, R.string.inside_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (UnsupportedEncodingException e) {
                            ErrorUtils.error(MainActivity.this, e);
                        }
                    }
                    else {
                        ErrorUtils.error(MainActivity.this, error);
                    }
                }
            });
        }
        catch (UnsupportedEncodingException e) {
            ErrorUtils.error(MainActivity.this, e);
        }
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
            try {
                UElearningRestClient.delete("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8), new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if(Config.DEBUG_SHOW_MESSAGE) {
                                    Toast.makeText(MainActivity.this, "伺服器已接受登出" ,Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(MainActivity.this, "伺服器登出失敗" ,Toast.LENGTH_SHORT).show();
                                ErrorUtils.error(MainActivity.this, error);
                            }
                });
            } catch (UnsupportedEncodingException e) {
                ErrorUtils.error(MainActivity.this, e);
            }

            // 清除登入資料
            db.remove_user();
            db.removeAll_enableActivity();
            db.removeAll_activity();

            // 回到登入畫面
            finish();
            Intent to_login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(to_login);
            return true;
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
