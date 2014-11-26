package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.content.Intent;
import android.database.Cursor;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;

public class MainActivity extends ActionBarActivity {

    private TextView mText_nickname, mText_realname, mText_classname, mText_groupname;
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

        //Listview
        listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        mListView_activity.setAdapter(listAdapter);
        mListView_activity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"你選擇的是"+list[position], Toast.LENGTH_SHORT).show();
                if(position == 0)
                {
                    Intent to_mainActivity = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(to_mainActivity);
                }

            }
        });
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

            // 清除登入資料
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
