package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

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

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private TextView mText_nickname, mText_realname, mText_classname, mText_groupname;
    private SwipeRefreshLayout mSwipe_activity;
    private ListView mListView_activity;
    private String[] list = {"學習中","預約","主題"};
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // UI對應
        mText_nickname = (TextView) findViewById(R.id.text_nickname);
        mText_realname = (TextView) findViewById(R.id.text_realname);
        mText_classname = (TextView) findViewById(R.id.text_classname);
        mText_groupname = (TextView) findViewById(R.id.text_groups);

        mListView_activity = (ListView) findViewById(R.id.listView_activity);

        // 從資料庫取得個人資訊
        DBProvider db = new DBProvider(MainActivity.this);
        Cursor the_user_query = db.get_user();
        the_user_query.moveToFirst();

        String nickname = the_user_query.getString( the_user_query.getColumnIndex("NickName") );
        String realname = the_user_query.getString( the_user_query.getColumnIndex("RealName") );
        String classname = the_user_query.getString( the_user_query.getColumnIndex("CName") );
        String groupname = the_user_query.getString( the_user_query.getColumnIndex("GName") );

        // 顯示個人資訊在介面上
        mText_nickname.setText(nickname);
        mText_realname.setText(realname);
        mText_classname.setText(classname);
        mText_groupname.setText(groupname);

        // SwipeLayout: 可用的學習活動清單
        mSwipe_activity = (SwipeRefreshLayout) findViewById(R.id.swipe_activity);
        mSwipe_activity.setOnRefreshListener(this);

        // Listview: 可用的學習活動清單
        listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        mListView_activity.setAdapter(listAdapter);
        mListView_activity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"你選擇的是"+list[position], Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 取得學習活動
    public void getStudyActivityList() {
        DBProvider db = new DBProvider(MainActivity.this);

        // 抓取目前已登入的Token
        String token = db.get_token();


        try {
            UElearningRestClient.get("/tokens/"+URLEncoder.encode(token, HTTP.UTF_8)+"/activitys", null, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    mSwipe_activity.setRefreshing(true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    mSwipe_activity.setRefreshing(false);

                    // 清除目前的活動清單
                    DBProvider db = new DBProvider(MainActivity.this);
                    db.removeAll_enableActivity();

                    String content = null;
                    try {
                        content = new String(responseBody, HTTP.UTF_8);
                        JSONObject response = new JSONObject(content);

                        JSONArray jsonArr_enableStudy = response.getJSONArray("enable_study");

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
                            String mMode = thisActivity.getString("material_mode");
                            Boolean lock = thisActivity.getBoolean("lock");
                            int targetTotal = thisActivity.getInt("target_total");
                            int learnedTotal = thisActivity.getInt("learned_total");

                            int typeId;
                            if(type.equals("theme")) typeId = 3;
                            else if(type.equals("will")) typeId = 2;
                            else if(type.equals("study")) typeId = 1;
                            else typeId = 0;

                            // 紀錄進資料庫裡
                            db.insert_enableActivity(db.get_user_id(), typeId, saId, swId,
                                    thId, thName, thIntroduction, startTime, expiredTime,
                                    learnTime, timeForce, lMode, lForce, mMode,
                                    lock, targetTotal, learnedTotal);
                        }

                    } catch (UnsupportedEncodingException e) {
                        ErrorUtils.error(MainActivity.this, e);
                    } catch (JSONException e) {
                        ErrorUtils.error(MainActivity.this, e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    mSwipe_activity.setRefreshing(false);

                    Toast.makeText(MainActivity.this, "s: "+statusCode, Toast.LENGTH_SHORT).show();
                    try {
                        String content = new String(responseBody, HTTP.UTF_8);
                        if(Config.DEBUG_SHOW_MESSAGE) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.get_fail_enableActivity)+"\n"+content, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, R.string.get_fail_enableActivity, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (UnsupportedEncodingException e) {
                        ErrorUtils.error(MainActivity.this, e);
                    }

                }
            });
        } catch (UnsupportedEncodingException e) {
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
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.menu_refresh) {
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

            // 回到登入畫面
            finish();
            Intent to_login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(to_login);
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
