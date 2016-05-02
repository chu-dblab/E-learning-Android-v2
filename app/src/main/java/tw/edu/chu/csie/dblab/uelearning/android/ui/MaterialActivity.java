package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
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
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.ui.js_handler.MaterialJSHandler;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.FileUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.LogUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.TimeUtils;

public class MaterialActivity extends ActionBarActivity {

    protected static final int REMAINED_TIME = 0x101;

    /**
     * 此學習點的標的編號
     */
    private int tId;

    /**
     * 此標的預計學習時間 (分鐘)
     */
    private int tLearnTime;

    /**
     * 是否為實際抵達學習點
     */
    private boolean isEntity;

    /**
     * 開始進入學習點時間
     */
    protected Date startTime;

    protected String materialFilePath;

    // UI上的元件
    private ActionBar actionbar;
    private WebView mWebView;
    private WebSettings webSettings;
    private static long back_pressed;
    private Timer updateUITimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        // 取得目前所在的教材編號
        Intent intent = getIntent();
        this.tId = intent.getIntExtra("tId", 0);
        this.isEntity = intent.getBooleanExtra("is_entity", true);

        // 取得此標的資訊
        DBProvider db = new DBProvider(MaterialActivity.this);
        Cursor targetQuery = db.get_target(tId);
        targetQuery.moveToFirst();
        String tName = targetQuery.getString(targetQuery.getColumnIndex("TName"));
        tLearnTime = targetQuery.getInt(targetQuery.getColumnIndex("LearnTime"));

        // ActionBar對應
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_material);
        setSupportActionBar(toolbar);

        // Add ActionBar back button
        actionbar = getSupportActionBar();
        // 判斷目前的設定檔是否允許中途離開學習點
        if (Config.LEARNING_BACK_ENABLE) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        // 在ActionBar顯示所在標的編號
        actionbar.setTitle(this.tId +". "+ tName);
        actionbar.setSubtitle(getString(R.string.in_target_time).toString() + "00:00");

        // 界面元件對應
        mWebView = (WebView) findViewById(R.id.webview_material);

        // 取得教材路徑
        materialFilePath = FileUtils.getMaterialFilePath(MaterialActivity.this, tId, true);

        // 有查到此標的的教材路徑
        if (!materialFilePath.equals(null)) {
            // 開始學習
            startLearn();

            // 將網頁內容顯示出來
            webSettings = mWebView.getSettings();
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setJavaScriptEnabled(true);
            mWebView.addJavascriptInterface(new MaterialJSHandler(this, tId), "Android");
            mWebView.loadUrl("file://" + materialFilePath);
            if (Config.DEBUG_SHOW_MESSAGE) {
                Toast.makeText(this, FileUtils.getMaterialFilePath(this, tId, isEntity), Toast.LENGTH_SHORT).show();
            }
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mWebView.loadUrl("javascript:uelearning.setAndroid()");
                }
            });
        } else {
            ErrorUtils.error(MaterialActivity.this, "No Material Files");
        }

        updateUI();
    }

    public void updateUI() {

        Date inTime = getInTime();
        String inTimeString = TimeUtils.timeeToStringNoHour(inTime);
        actionbar.setSubtitle(getString(R.string.in_target_time).toString() +" "+ inTimeString + ", "
                + getString(R.string.target_learn_time) +" "+ tLearnTime + getString(R.string.minute));
    }

    public void webViewBackToView() {
        mWebView.loadUrl("file://" + materialFilePath);
    }

    /**
     * 開始學習
     */
    public void startLearn() {
        // 取得現在時間
        startTime = TimeUtils.getNowClientTime();

        // 以下是通知伺服器開始學習
        final DBProvider db = new DBProvider(MaterialActivity.this);

        // 抓取目前狀態所需資料
        String token = db.get_token();
        int saId = db.get_activity_id();

        // 紀錄
        LogUtils.Insert.toInTarget(MaterialActivity.this, saId, tId);

        // 帶入參數
        final RequestParams sId_params = new RequestParams();
        if(isEntity) {
            sId_params.put("is_entity", 1);
        }
        else {
            sId_params.put("is_entity", 0);
        }

        // 告訴伺服端我已進入學習點
        try {
            final String url = "/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) +
                    "/activitys/" + saId + "/points/" + tId + "/toin";
            UElearningRestClient.post(url, sId_params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {

                    if(Config.DEBUG_SHOW_MESSAGE) {
                        Toast.makeText(MaterialActivity.this, "正在通知伺服端: "+url, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String content = null;

                    try {
                        content = new String(responseBody, "UTF-8");
                        JSONObject response = new JSONObject(content);
                        int sId = response.getInt("study_id");

                        if(Config.DEBUG_SHOW_MESSAGE) {
                            Toast.makeText(MaterialActivity.this, "已成功通知: "+sId, Toast.LENGTH_SHORT).show();
                        }

                    } catch (UnsupportedEncodingException e) {
                        ErrorUtils.error(MaterialActivity.this, e);
                    } catch (JSONException e) {
                        ErrorUtils.error(MaterialActivity.this, e);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    if(statusCode != 0) {

                        try {
                            // TODO: 錯誤處理
                            String content = new String(responseBody, HTTP.UTF_8);
                            if(Config.DEBUG_SHOW_MESSAGE) {
                                Toast.makeText(MaterialActivity.this,
                                        "s: " + statusCode + "\n" + content,
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(MaterialActivity.this, R.string.inside_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (UnsupportedEncodingException e) {
                            ErrorUtils.error(MaterialActivity.this, e);
                        }
                    }
                    else {
                        ErrorUtils.error(MaterialActivity.this, error);
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            ErrorUtils.error(MaterialActivity.this, e);
        }
    }

    public void finishLearn() {
        updateUITimer.cancel();

        final DBProvider db = new DBProvider(MaterialActivity.this);

        // 抓取目前狀態所需資料
        String token = db.get_token();
        int saId = db.get_activity_id();

        // 紀錄
        LogUtils.Insert.toOutTarget(MaterialActivity.this, saId, tId);
        Cursor query_allAns = db.getAll_answer();
        int ans_total = query_allAns.getCount();

        JSONObject postData = new JSONObject();
        JSONArray ans_list = new JSONArray();
        try {
            for(int i=0; i<ans_total; i++) {
                query_allAns.moveToPosition(i);

                int tId = query_allAns.getInt(query_allAns.getColumnIndex("TID"));
                String qDate = query_allAns.getString(query_allAns.getColumnIndex("QDate"));
                String aDate = query_allAns.getString(query_allAns.getColumnIndex("ADate"));
                Integer qId = query_allAns.getInt(query_allAns.getColumnIndex("QID"));
                String ans = query_allAns.getString(query_allAns.getColumnIndex("Ans"));
                int corrInt = query_allAns.getInt(query_allAns.getColumnIndex("Correct"));

                JSONObject thisAns = new JSONObject();
                thisAns.put("target_id", tId);
                thisAns.put("question_time", qDate);
                thisAns.put("answer_time", aDate);
                thisAns.put("quest_id", qId);
                thisAns.put("answer", ans);
                thisAns.put("correct", corrInt);

                ans_list.put(thisAns);
            }
            postData.put("answers", ans_list);
        } catch (JSONException e) {
            ErrorUtils.error(MaterialActivity.this, e);
        }

        // 帶入參數
        final RequestParams out_params = new RequestParams();
        StringEntity entity = null;
        try {
            entity = new StringEntity(postData.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            // 告訴伺服端我已離開學習點
            try {
                final String url = "/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) +
                        "/activitys/" + saId + "/points/" + tId + "/toout";
                UElearningRestClient.post(MaterialActivity.this, url, entity, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {

                        if(Config.DEBUG_SHOW_MESSAGE) {
                            Toast.makeText(MaterialActivity.this, "正在通知伺服端: "+url, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String content = null;

                        try {
                            content = new String(responseBody, "UTF-8");
                            JSONObject response = new JSONObject(content);

                            if(Config.DEBUG_SHOW_MESSAGE) {
                                Toast.makeText(MaterialActivity.this, "已成功通知: ", Toast.LENGTH_SHORT).show();
                            }

                            db.removeAll_answer();

                        } catch (UnsupportedEncodingException e) {
                            ErrorUtils.error(MaterialActivity.this, e);
                        } catch (JSONException e) {
                            ErrorUtils.error(MaterialActivity.this, e);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        try {
                            // TODO: 錯誤處理
                            String content = new String(responseBody, HTTP.UTF_8);
                            if(Config.DEBUG_SHOW_MESSAGE) {
                                Toast.makeText(MaterialActivity.this,
                                        "s: " + statusCode + "\n" + content,
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(MaterialActivity.this, R.string.inside_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (UnsupportedEncodingException e) {
                            ErrorUtils.error(MaterialActivity.this, e);
                        }
                    }
                });
            } catch (UnsupportedEncodingException e) {
                ErrorUtils.error(MaterialActivity.this, e);
            }

        } catch (UnsupportedEncodingException e) {
            ErrorUtils.error(MaterialActivity.this, e);
        }

        // 告知上一個活動說剛剛學習到的是哪個標地
        Intent returnIntent = new Intent();
        returnIntent.putExtra("LearnedPointId", tId);
        setResult(RESULT_OK, returnIntent);

        finish();
    }

    /**
     * 取得已停留時間
     * @return 在此學習點停留時間
     */
    public Date getInTime() {
        Date nowTime = TimeUtils.getNowClientTime();
        long timerLong = nowTime.getTime() - startTime.getTime();
        return new Date(timerLong);
    }

    Handler updateUIHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch(msg.what) {
                case REMAINED_TIME:

                    updateUI();
                    break;
            }
        };
    };

    class UpdateUITask extends TimerTask {

        @Override
        public void run() {
            Message message = new Message();
            message.what = MaterialActivity.REMAINED_TIME;

            updateUIHandler.sendMessage(message);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        // 判斷目前的設定檔是否允許中途離開學習點
        if(Config.LEARNING_BACK_ENABLE) {
            // 按兩下即離開學習點
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                finishLearn();
            }
            else {
                Toast.makeText(getBaseContext(), R.string.double_back_press_to_exit_point, Toast.LENGTH_SHORT).show();
            }
            back_pressed = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_material, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            // 判斷目前的設定檔是否允許中途離開學習點
            if(Config.LEARNING_BACK_ENABLE) {
                finishLearn();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        updateUITimer.cancel();
        super.onPause();
    }

    @Override
    public void onResume() {
        updateUITimer = new Timer();
        updateUITimer.schedule(new UpdateUITask(), 0, 1 * 1000);
        super.onResume();
    }
}
