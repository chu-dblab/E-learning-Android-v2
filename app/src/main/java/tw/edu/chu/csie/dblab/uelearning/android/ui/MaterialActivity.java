package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.FileUtils;

public class MaterialActivity extends ActionBarActivity {

    /**
     * 此學習點的標的編號
     */
    private int tId;

    /**
     * 是否為實際抵達學習點
     */
    private boolean isEntity;

    // UI上的元件
    private WebView mWebView;
    private WebSettings webSettings;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        // 取得目前所在的教材編號
        Intent intent = getIntent();
        this.tId = intent.getIntExtra("tId", 0);
        this.isEntity = intent.getBooleanExtra("is_entity", true);

        // ActionBar對應
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_material);
        setSupportActionBar(toolbar);

        // Add ActionBar back button
        final ActionBar actionbar = getSupportActionBar();
        // 判斷目前的設定檔是否允許中途離開學習點
        if (Config.LEARNING_BACK_ENABLE) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        // 在ActionBar顯示所在標的編號
        actionbar.setTitle(getString(R.string.now_in_target).toString() + this.tId);
        actionbar.setSubtitle(getString(R.string.in_target_time).toString() + "00:00");

        // 界面元件對應
        mWebView = (WebView) findViewById(R.id.webview_material);

        // 取得教材路徑
        String materialFilePath = FileUtils.getMaterialFilePath(MaterialActivity.this, tId, true);

        // 有查到此標的的教材路徑
        if (!materialFilePath.equals(null)) {
            // 開始學習
            startLearn();

            // 將網頁內容顯示出來
            webSettings = mWebView.getSettings();
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setJavaScriptEnabled(true);
            //mWebView.addJavascriptInterface(new MaterialJSCall(this), "Android");
            mWebView.loadUrl("file://" + materialFilePath);
            if (Config.DEBUG_SHOW_MESSAGE) {
                Toast.makeText(this, FileUtils.getMaterialFilePath(this, tId, isEntity), Toast.LENGTH_SHORT).show();
            }
        } else {
            ErrorUtils.error(MaterialActivity.this, "No Material Files");
        }

    }

    public void startLearn() {

        final DBProvider db = new DBProvider(MaterialActivity.this);

        // 抓取目前狀態所需資料
        String token = db.get_token();
        int saId = db.get_activity_id();

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
    }

    public void finishLearn() {

        final DBProvider db = new DBProvider(MaterialActivity.this);

        // 抓取目前狀態所需資料
        String token = db.get_token();
        int saId = db.get_activity_id();

        // 帶入參數
        final RequestParams out_params = new RequestParams();

        // 告訴伺服端我已離開學習點
        try {
            final String url = "/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) +
                    "/activitys/" + saId + "/points/" + tId + "/toout";
            UElearningRestClient.post(url, out_params, new AsyncHttpResponseHandler() {

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

        finish();
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
}
