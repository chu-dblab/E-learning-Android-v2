package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;

public class MaterialActivity extends ActionBarActivity {

    /**
     * 此學習點的標的編號
     */
    private int tId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        // 取得目前所在的教材編號
        Intent intent = getIntent();
        this.tId = intent.getIntExtra("tId",0);

        // ActionBar對應
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_material);
        setSupportActionBar(toolbar);

        // Add ActionBar back button
        final ActionBar actionbar = getSupportActionBar();
        // 判斷目前的設定檔是否允許中途離開學習點
        if(Config.LEARNING_BACK_ENABLE) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        // 在ActionBar顯示所在標的編號
        actionbar.setTitle(getString(R.string.now_in_target).toString() + this.tId);
        actionbar.setSubtitle(getString(R.string.in_target_time).toString() + "00:00");

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
