package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;

public class LogActivity extends ActionBarActivity implements View.OnClickListener {

    private TextView mText_log_total;
    private Button mBtn_uploadAllLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        initUi();
        updateUi();
    }

    public void initUi() {
        mText_log_total = (TextView) findViewById(R.id.text_log_total);
        mBtn_uploadAllLog = (Button) findViewById(R.id.btn_log_upload_all);
        mBtn_uploadAllLog.setOnClickListener(this);
    }

    public void updateUi() {
        DBProvider db = new DBProvider(LogActivity.this);
        Cursor query_allLog = db.getAll_log();
        int log_total = query_allLog.getCount();

        mText_log_total.setText("Total" + log_total);
    }

    public void uploadLogs() {
        DBProvider db = new DBProvider(LogActivity.this);
        Cursor query_allLog = db.getAll_log();
        int log_total = query_allLog.getCount();

        JSONArray logs = new JSONArray();
        try {
            for(int i=0; i<log_total; i++) {
                query_allLog.moveToPosition(i);

                String lId = query_allLog.getString(query_allLog.getColumnIndex("LID"));
                String uId = query_allLog.getString(query_allLog.getColumnIndex("UID"));
                String date = query_allLog.getString(query_allLog.getColumnIndex("Date"));
                Integer saId = query_allLog.getInt(query_allLog.getColumnIndex("SaID"));
                Integer tId = query_allLog.getInt(query_allLog.getColumnIndex("TID"));
                String actionG = query_allLog.getString(query_allLog.getColumnIndex("ActionGroup"));
                String action = query_allLog.getString(query_allLog.getColumnIndex("Encode"));
                Integer qId = query_allLog.getInt(query_allLog.getColumnIndex("QID"));
                String answer = query_allLog.getString(query_allLog.getColumnIndex("Aswer"));
                String other = query_allLog.getString(query_allLog.getColumnIndex("Other"));

                JSONObject thisLog = new JSONObject();
                thisLog.put("LID", lId);
                thisLog.put("UID", lId);
                thisLog.put("Date", lId);
                thisLog.put("SaID", lId);
                thisLog.put("ActionGroup", lId);
                thisLog.put("Encode", lId);
                thisLog.put("TID", lId);
                thisLog.put("QID", lId);
                thisLog.put("Aswer", lId);
                thisLog.put("Other", lId);

                logs.put(thisLog);
            }
        } catch (JSONException e) {
            ErrorUtils.error(LogActivity.this, e);
        }

        // 顯示挖出來的資料
        Toast.makeText(this, logs.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_log_upload_all) {
            uploadLogs();
        }
    }
}
