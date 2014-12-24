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

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;

public class TesterActivity extends ActionBarActivity implements View.OnClickListener {

    Button mBtn_hello, mBtn_sql_insert_user, mBtn_sql_remove_user;

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
            db.insert_user("tsdnfknasdn", "eric", "2014-11-23 17:37:59", "user", "使用者", null, null, null, null, null, "圓兒～", null,null);
        }
        else if(id == R.id.btn_tester_sqlite_remove_user) {
            DBProvider db = new DBProvider(this);
            db.remove_user();
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
