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
            finish();
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

        return super.onOptionsItemSelected(item);
    }
}
