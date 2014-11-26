package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

            // 回到登入畫面
            finish();
            Intent to_login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(to_login);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
