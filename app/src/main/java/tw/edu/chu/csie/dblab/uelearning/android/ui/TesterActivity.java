package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.learning.ActivityManager;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.util.EncryptUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.LogUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.TimeUtils;

public class TesterActivity extends ActionBarActivity implements View.OnClickListener {

    Button mBtn_hello;
    Button mBtn_sql_insert_user, mBtn_sql_remove_user, mBtn_sql_get_siteInfo, mBtn_sql_set_siteInfo,mBtn_sql_insert_log, mBtn_sql_log;
    Button mBtn_time_now, mBtn_time_start, mBtn_time_learning, mBtn_time_remainder;
    Button mBtn_sha1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);

        // Add ActionBar back button
        final ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        mBtn_hello = (Button) findViewById(R.id.btn_tester_hello);
        mBtn_hello.setOnClickListener(this);

        mBtn_sql_insert_user = (Button) findViewById(R.id.btn_tester_sqlite_insert_user);
        mBtn_sql_insert_user.setOnClickListener(this);

        mBtn_sql_remove_user = (Button) findViewById(R.id.btn_tester_sqlite_remove_user);

        mBtn_sql_get_siteInfo = (Button) findViewById(R.id.btn_tester_sqlite_get_site_info);
        mBtn_sql_get_siteInfo.setOnClickListener(this);

        mBtn_sql_insert_log = (Button) findViewById(R.id.btn_tester_sqlite_get_site_info);
        mBtn_sql_insert_log.setOnClickListener(this);

        mBtn_sql_set_siteInfo = (Button) findViewById(R.id.btn_tester_sqlite_set_site_info);
        mBtn_sql_set_siteInfo.setOnClickListener(this);

        mBtn_sql_log = (Button) findViewById(R.id.btn_tester_log);
        mBtn_sql_log.setOnClickListener(this);

        mBtn_time_now = (Button) findViewById(R.id.btn_tester_time_now);
        mBtn_time_now.setOnClickListener(this);

        mBtn_time_start = (Button) findViewById(R.id.btn_tester_time_start);
        mBtn_time_start.setOnClickListener(this);

        mBtn_time_learning = (Button) findViewById(R.id.btn_tester_time_learning);
        mBtn_time_learning.setOnClickListener(this);

        mBtn_time_remainder = (Button) findViewById(R.id.btn_tester_time_remainder);
        mBtn_time_remainder.setOnClickListener(this);

        mBtn_sha1 = (Button) findViewById(R.id.btn_tester_sha1);
        mBtn_sha1.setOnClickListener(this);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.btn_tester_hello) {
            getHelloString();
        }
        else if(id == R.id.btn_tester_sqlite_insert_user) {
            DBProvider db = new DBProvider(this);
            db.insert_user("tsdnfknasdn", "eric", "2014-11-23 17:37:59", "user", "使用者", null, null, null, null, null, "圓兒～", null, null);
        }
        else if(id == R.id.btn_tester_sqlite_remove_user) {
            DBProvider db = new DBProvider(this);
            db.remove_user();
        }
        else if(id == R.id.btn_tester_sqlite_get_site_info) {
            DBProvider db = new DBProvider(this);
            String timeAdjust = db.get_serverInfo("TimeAdjust");
            Toast.makeText(TesterActivity.this, "ServerInfo: "+timeAdjust, Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.btn_tester_sqlite_set_site_info) {
            DBProvider db = new DBProvider(this);
            db.set_serverInfo("TimeAdjust", String.valueOf(5));
        }
        else if(id == R.id.btn_tester_log) {
            LogUtils.Insert.material_answer(TesterActivity.this, 3,3,2,"4", true);
        }
        else if(id == R.id.btn_tester_time_now) {
            // 取得現在時間
            Date nowDate = TimeUtils.getNowServerTime(TesterActivity.this);
//            Date nowDate = TimeUtils.getNowClientTime();

            Toast.makeText(TesterActivity.this, "Now: "+nowDate.getTime(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(TesterActivity.this, "Now: "+nowDate.getHours()+":"+nowDate.getMinutes()+":"+nowDate.getSeconds(), 0).show();

            // 顯示時間
            Toast.makeText(TesterActivity.this, "Now: " + TimeUtils.dateToString(nowDate), Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.btn_tester_time_start) {

            Date startDate = ActivityManager.getStartDate(TesterActivity.this);
            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTime(startDate);
            String timeString = nowCalendar.get(Calendar.HOUR_OF_DAY)+":"+nowCalendar.get(Calendar.MINUTE)+":"+nowCalendar.get(Calendar.SECOND);
            Toast.makeText(TesterActivity.this, timeString, Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.btn_tester_time_learning) {
            Date remainderDate = ActivityManager.getLearningTime(TesterActivity.this);

            Calendar learningCal = Calendar.getInstance();
            learningCal.setTime(remainderDate);
            learningCal.setTimeZone(TimeZone.getTimeZone("UTC"));

            Toast.makeText(TesterActivity.this, "Learning: "+learningCal.get(Calendar.HOUR_OF_DAY)+":"+learningCal.get(Calendar.MINUTE)+":"+learningCal.get(Calendar.SECOND), 0).show();

            int LearningMin = ActivityManager.getLearningMinTime(TesterActivity.this);
            Toast.makeText(TesterActivity.this, "Learning: "+ LearningMin, Toast.LENGTH_SHORT).show();
            //Toast.makeText(TesterActivity.this, "Limit: "+new LearningUtils(TesterActivity.this).getRemainderLearningMinTime(), 0).show();
        }
        else if(id == R.id.btn_tester_time_remainder) {
            Date remainderDate = ActivityManager.getRemainderLearningTime(TesterActivity.this);

            Calendar learningCal = Calendar.getInstance();
            learningCal.setTime(remainderDate);
            learningCal.setTimeZone(TimeZone.getTimeZone("UTC"));

            Toast.makeText(TesterActivity.this, "Remainder: "+learningCal.get(Calendar.HOUR_OF_DAY)+":"+learningCal.get(Calendar.MINUTE)+":"+learningCal.get(Calendar.SECOND), 0).show();

            int LearningMin = ActivityManager.getRemainderLearningMinTime(TesterActivity.this);
            Toast.makeText(TesterActivity.this, "Remainder: "+ LearningMin, Toast.LENGTH_SHORT).show();
            //Toast.makeText(TesterActivity.this, "Limit: "+new LearningUtils(TesterActivity.this).getRemainderLearningMinTime(), 0).show();
        }
        else if(id == R.id.btn_tester_sha1) {
            String origin = "abcde";
            String encypted = EncryptUtils.sha1(origin);

            Toast.makeText(TesterActivity.this, "原本: "+origin+"\n加密後"+encypted, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tester, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getHelloString() {

        UElearningRestClient.get("/hello/tester", null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Toast.makeText(TesterActivity.this, "開始爬", Toast.LENGTH_SHORT).show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    String msg = response.getString("msg");
                    Toast.makeText(TesterActivity.this, "msg: "+msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(TesterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                super.onSuccess(statusCode, headers, response);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(TesterActivity.this, "失敗", Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
