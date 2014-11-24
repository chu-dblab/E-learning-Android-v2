package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.util.HelpUtils;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    ImageButton mBtn_menu_overflow;
    PopupMenu mPopup_menu_overflow;
    EditText mEdit_account,mEdit_password;
    Button mBtn_login_ok;
    ProgressDialog mProgress_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //登入按鈕對應
        mBtn_login_ok = (Button) findViewById(R.id.btn_login_ok);
        mBtn_login_ok.setOnClickListener(this);

        //輸入帳密對應
        mEdit_account = (EditText) findViewById(R.id.edit_login_account);
        mEdit_password = (EditText) findViewById(R.id.edit_login_password);

        // 介面對應
        mBtn_menu_overflow = (ImageButton) findViewById(R.id.btn_login_menu_overflow);
        mBtn_menu_overflow.setOnClickListener(this);
        /** Instantiating PopupMenu class */
        mPopup_menu_overflow = new PopupMenu(this, mBtn_menu_overflow);

        /** Adding menu items to the popumenu */
        mPopup_menu_overflow.getMenuInflater().inflate(R.menu.login, mPopup_menu_overflow.getMenu());

        // 登入中畫面
        mProgress_login = new ProgressDialog(LoginActivity.this);
        mProgress_login.setMessage(getResources().getString(R.string.logining));
        mProgress_login.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress_login.setIndeterminate(true);
        mProgress_login.setCancelable(false);


        // DEBUG 開啟教材內容測試
        if(Config.DEBUG_ACTIVITY) {
            mPopup_menu_overflow.getMenu().findItem(R.id.menu_inside_tester).setVisible(true);
        }

        /** Defining menu item click listener for the popup menu */
        mPopup_menu_overflow.setOnMenuItemClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login_menu_overflow) {
            /** Showing the popup menu */
            mPopup_menu_overflow.show();
        }
        if (id == R.id.btn_login_ok) {
            mlogin();
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        mPopup_menu_overflow.show();
        return super.onMenuOpened(featureId, menu);
    }

    public void mlogin(){
        String mID,mPassword;
        mID = mEdit_account.getText().toString();
        mPassword = mEdit_password.getText().toString();

        // 顯示使用者輸入的登入資訊
        if(Config.DEBUG_SHOW_MESSAGE) {
            Toast.makeText(LoginActivity.this, mID + "\n" + mPassword, Toast.LENGTH_SHORT).show();
        }

        // 帶入登入參數
        RequestParams login_params = new RequestParams();
        login_params.put("user_id", mID);
        login_params.put("password", mPassword);
        login_params.put("browser", "android");

        // 對伺服端進行登入動作
        UElearningRestClient.post("/tokens", login_params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                mProgress_login.show();
                super.onStart();
            }

            /**
             * Fired when a request returns successfully, override to handle in your own code
             *
             * @param statusCode   the status code of the response
             * @param headers      return headers, if any
             * @param responseBody the body of the HTTP response from the server
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgress_login.dismiss();

                try {
                    String content = new String(responseBody, "UTF-8");
                    JSONObject response = new JSONObject(content);

                    // TODO: 登入成功後的動作
                    String token = response.getString("token");
                    Toast.makeText(LoginActivity.this, "S: "+token, Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /**
             * Fired when a request fails to complete, override to handle in your own code
             *
             * @param statusCode   return HTTP status code
             * @param headers      return headers, if any
             * @param responseBody the response body, if any
             * @param error        the underlying cause of the failure
             */
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgress_login.dismiss();

                // TODO: 在界面上顯示詳細錯誤訊息
                Toast.makeText(LoginActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();

            }
        });


    }

    /**
     * This method will be invoked when a menu item is clicked if the item itself did
     * not already handle the event.
     *
     * @param item {@link android.view.MenuItem} that was clicked
     * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_about) {
            HelpUtils.showAboutDialog(LoginActivity.this);
            return true;
        }
        else if(id == R.id.menu_inside_tester) {
            Intent toTester = new Intent(LoginActivity.this, TesterActivity.class);
            startActivity(toTester);
        }

        return super.onOptionsItemSelected(item);
    }
}
