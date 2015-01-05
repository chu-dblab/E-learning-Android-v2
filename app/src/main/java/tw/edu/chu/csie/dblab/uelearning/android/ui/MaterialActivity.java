package tw.edu.chu.csie.dblab.uelearning.android.ui;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.FileUtils;

public class MaterialActivity extends ActionBarActivity {

    /**
     * 此學習點的標的編號
     */
    private int tId;

    // UI上的元件
    private WebView mWebView;
    private WebSettings webSettings;
    private static long back_pressed;
    public String internet_str = new String();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        // 取得目前所在的教材編號
        Intent intent = getIntent();
        this.tId = intent.getIntExtra("tId", 0);

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
        String materialFilePath = FileUtils.getMaterialFilePath(MaterialActivity.this, tId);

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
                Toast.makeText(this, FileUtils.getMaterialFilePath(this, tId), Toast.LENGTH_SHORT).show();
            }
        } else {
            ErrorUtils.error(MaterialActivity.this, "No Material Files");
        }

    }

    public void startLearn() {
    }

    public void finishLearn() {
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
                finish();
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
                finish();
            }
        }
        else if(id == R.id.menu_internet)
        {
            final AlertDialog.Builder Dialog_internet = new AlertDialog.Builder(MaterialActivity.this);
            Dialog_internet.setTitle(R.string.message_internet_search);
            final EditText Edit_internet = new EditText(MaterialActivity.this);
            Edit_internet.setInputType(InputType.TYPE_CLASS_TEXT);
            Dialog_internet.setView(Edit_internet);
            Dialog_internet.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // 隱藏鍵盤（實際上是切換鍵盤是否顯示）
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    String internet_question = Edit_internet.getText().toString();
                    if (!internet_question.equals("")) {
                        Internet_data internet_json = new Internet_data();
                        internet_json.execute();
                        while(internet_str.equals(""))
                        {

                        }
                        //查看是否有回傳成功
                        Toast.makeText(MaterialActivity.this , internet_str , Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Dialog_internet.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 隱藏鍵盤（實際上是切換鍵盤是否顯示）
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            });
            Dialog_internet.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // 隱藏鍵盤（實際上是切換鍵盤是否顯示）
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            });
            Dialog_internet.show();
            /*
            AlertDialog.Builder internet = new  AlertDialog.Builder(this);
            internet.setTitle("網路搜尋:").setView(R.layout.dialog_assistant_internet);
            internet.show();
*/
        }
        else if(id == R.id.menu_detail)
        {
            /*
                        AlertDialog.Builder detail = new  AlertDialog.Builder(this);
            detail.setTitle("網路搜尋:");
            detail.setView(R.layout.dialog_assistant_detail);
            detail.show();
                */
        }
        else if(id == R.id.menu_handwrite)
        {

        }
        else if(id == R.id.menu_question_answer)
        {

        }

        return super.onOptionsItemSelected(item);
    }

    class Internet_data extends AsyncTask
    {
        Bundle bundle = new Bundle();
        @Override
        protected Object doInBackground(Object[] params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet post = new HttpGet("http://140.126.11.158:8080/SupportSystem/api/GoogleSearch");
            try
            {
                HttpResponse response = client.execute(post);
                String content = EntityUtils.toString(response.getEntity());

                // 抓一坨出來
                //bundle.putString("msg", content);

                // 只抓某ID的內容
                JSONArray json_content = new JSONArray(content);
                for(int i=0; i<json_content.length(); i++) {
                    JSONObject json = json_content.getJSONObject(i);
                    String msg = json.getString("Title");
                    bundle.putString("msg", content);
                    internet_str = msg;
                }


               // Uri uri = Uri.parse(msg);
                //Intent intent_internet = new Intent(Intent.ACTION_VIEW, uri);
               //startActivity(intent_internet);
            }catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
