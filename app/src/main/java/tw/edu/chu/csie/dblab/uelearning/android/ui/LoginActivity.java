package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
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
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.learning.UserUtils;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.ui.fragment.BrowseMaterialFragment;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.HelpUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.NetworkUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.TimeUtils;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    ImageButton mBtn_menu_overflow;
    PopupMenu mPopup_menu_overflow;
    EditText mEdit_account,mEdit_password;
    Button mBtn_login_ok, mBtn_login_clear;
    ProgressDialog mProgress_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 介面對應
        mBtn_menu_overflow = (ImageButton) findViewById(R.id.btn_login_menu_overflow);
        mBtn_menu_overflow.setOnClickListener(this);
        /** Instantiating PopupMenu class */
        mPopup_menu_overflow = new PopupMenu(this, mBtn_menu_overflow);
        /** Adding menu items to the popumenu */
        mPopup_menu_overflow.getMenuInflater().inflate(R.menu.menu_login, mPopup_menu_overflow.getMenu());
        // DEBUG 開啟教材內容測試
        if(Config.DEBUG_ACTIVITY) {
            mPopup_menu_overflow.getMenu().findItem(R.id.menu_inside_tester).setVisible(true);
        }
        /** Defining menu item click listener for the popup menu */
        mPopup_menu_overflow.setOnMenuItemClickListener(this);

        //輸入帳密對應
        mEdit_account = (EditText) findViewById(R.id.edit_login_account);
        mEdit_password = (EditText) findViewById(R.id.edit_login_password);
        //登入按鈕對應
        mBtn_login_ok = (Button) findViewById(R.id.btn_login_ok);
        mBtn_login_ok.setOnClickListener(this);
        mBtn_login_clear = (Button) findViewById(R.id.btn_login_clear);
        mBtn_login_clear.setOnClickListener(this);


        // 登入中畫面
        mProgress_login = new ProgressDialog(LoginActivity.this);
        mProgress_login.setMessage(getResources().getString(R.string.logining));
        mProgress_login.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress_login.setIndeterminate(true);
        // TODO: 設計成可中途取消的功能
        mProgress_login.setCancelable(false);

        // 填入預設帳號密碼
        if(Config.AUTO_FILL_LOGIN) {
            mEdit_account.setText(Config.DEFAULT_LOGIN_ID);
            mEdit_password.setText(Config.DEFAULT_LOGIN_PASSWORD);
        }
        //自動登入
        if(Config.AUTO_NO_ID_LOGIN) mlogin();

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
        else if (id == R.id.btn_login_ok) {
            // 開始像伺服端送出登入要求
            getPlaceInfo();
            mlogin();
        }
        else if (id == R.id.btn_login_clear) {
            mEdit_account.setText("");
            mEdit_password.setText("");
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        mPopup_menu_overflow.show();
        return super.onMenuOpened(featureId, menu);
    }

    public void mlogin(){
        // 是否初步驗證過
        boolean isOK = true;
        View focusView = null;

        // Reset errors.
        mEdit_account.setError(null);
        mEdit_password.setError(null);

        // Store values at the time of the menu_login attempt.
        String mID,mPassword;
        mID = mEdit_account.getText().toString();
        mPassword = mEdit_password.getText().toString();

        // 顯示使用者輸入的登入資訊
        if(Config.DEBUG_SHOW_MESSAGE) {
            Toast.makeText(LoginActivity.this, mID + "\n" + mPassword, Toast.LENGTH_SHORT).show();
        }

        // 判斷登入資料有無填寫正確
        if (TextUtils.isEmpty(mID)) {
            mEdit_account.setError(getString(R.string.error_field_required));
            mEdit_account.requestFocus();
            isOK = false;
        }
        if (TextUtils.isEmpty(mPassword)) {
            mEdit_password.setError(getString(R.string.error_field_required));
            if (!TextUtils.isEmpty(mID)) mEdit_password.requestFocus();
            isOK = false;
        }

        // 初步驗證正常，開始對伺服端進行登入
        if(isOK) {

            // 有沒有網路
            if(NetworkUtils.isNetworkConnected(LoginActivity.this)) {
                new UserUtils().userLogin(LoginActivity.this, mID, mPassword, new UserUtils.UserLoginHandler() {

                    // 開始登入
                    @Override
                    public void onStart() {
                        mProgress_login.show();
                    }

                    // 登入成功
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        mProgress_login.dismiss();
                        // 前往MainActivity
                        finish();
                        Intent to_mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(to_mainActivity);
                    }

                    // 找不到此帳號
                    @Override
                    public void onNoUser() {
                        mProgress_login.dismiss();
                        mEdit_account.setError(getString(R.string.error_no_account));
                        mEdit_account.requestFocus();
                    }

                    // 密碼錯誤
                    @Override
                    public void onPasswordErr() {
                        mProgress_login.dismiss();
                        mEdit_password.setError(getString(R.string.error_password));
                        mEdit_password.requestFocus();
                    }

                    // 此帳號被停用
                    @Override
                    public void onNoEnable() {
                        mProgress_login.dismiss();
                        mEdit_account.setError(getString(R.string.error_account_no_enable));
                        mEdit_account.requestFocus();
                    }

                    @Override
                    public void onNoResponse() {
                        Context context = LoginActivity.this;
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.no_network_response_dialog_title))
                                .setMessage(context.getString(R.string.no_network_response_dialog_message))
                                .setCancelable(true)
                                .setPositiveButton(context.getString(R.string.retry), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mlogin();
                                    }
                                })
                                .setNeutralButton(context.getString(R.string.cancel), null);
                        builder.create().show();
                    }

                    @Override
                    public void onOtherErr(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        mProgress_login.dismiss();
                        Toast.makeText(LoginActivity.this, "Status:"+statusCode, Toast.LENGTH_SHORT).show();
                        ErrorUtils.error(LoginActivity.this, error);
                    }

                    @Override
                    public void onOtherErr(Throwable e) {
                        mProgress_login.dismiss();
                        ErrorUtils.error(LoginActivity.this, e);
                    }
                });
            }
            // 若沒有網路
            else {
                Context context = LoginActivity.this;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.no_network_dialog_title))
                        .setMessage(context.getString(R.string.no_network_dialog_message))
                        .setCancelable(true)
                        .setPositiveButton(context.getString(R.string.retry), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mlogin();
                            }
                        })
                        .setNeutralButton(context.getString(R.string.cancel), null);
                builder.create().show();
            }
        }


    }
    public void getPlaceInfo() {
        UElearningRestClient.get("/info" , null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String content = new String(responseBody, "UTF-8");
                    JSONObject response = new JSONObject(content);
                    JSONArray placeInfoJson = response.getJSONArray("place_info");
                    JSONArray placeMapJson = response.getJSONArray("place_map");

                    // 紀錄進資料庫
                    DBProvider db = new DBProvider(LoginActivity.this);
                    db.remove_place_info();
                    db.remove_place_map();


                    // 抓取伺服端資料
                    for(int i=0; i<placeInfoJson.length(); i++) {
                        JSONObject thisData = placeInfoJson.getJSONObject(i);
                        int idData = thisData.getInt("id");
                        String nameData = thisData.getString("name");
                        String contentData = thisData.getString("content");

                        db.insert_place_info(idData,nameData,contentData);
                    }

                    for(int i=0; i<placeMapJson.length(); i++) {
                        JSONObject thisData = placeMapJson.getJSONObject(i);
                        int idData = thisData.getInt("id");
                        String nameData = thisData.getString("name");
                        String mapUrlData = thisData.getString("url");

                        db.insert_place_map(idData,nameData,mapUrlData);
                    }

                }
                catch (UnsupportedEncodingException e) {
                    ErrorUtils.error(LoginActivity.this, e);
                } catch (JSONException e) {
                    ErrorUtils.error(LoginActivity.this, e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                ErrorUtils.error(LoginActivity.this, error);
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
        if (id == R.id.menu_place_info) {
            Intent toBrowser = new Intent(LoginActivity.this, BrowserActivity.class);
            toBrowser.putExtra("to", BrowserActivity.TO_INFO);
            startActivity(toBrowser);
        }
        else if (id == R.id.menu_place_map) {
            Intent toBrowser = new Intent(LoginActivity.this, BrowserActivity.class);
            toBrowser.putExtra("to", BrowserActivity.TO_MAP);
            startActivity(toBrowser);
        }
        else if(id == R.id.menu_log_manager) {
            Intent toLogMgr = new Intent(LoginActivity.this, LogActivity.class);
            startActivity(toLogMgr);
        }
        else if (id == R.id.menu_browse_material) {
            Intent toBrowser = new Intent(LoginActivity.this, BrowserActivity.class);
            toBrowser.putExtra("to", BrowserActivity.TO_MATERIAL);
            startActivity(toBrowser);
        }
        else if (id == R.id.menu_about) {
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
