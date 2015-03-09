package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
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
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.ui.fragment.BrowseMaterialFragment;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.HelpUtils;
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
                        JSONObject userJson = response.getJSONObject("user");

                        // 抓取伺服端資料
                        String token = response.getString("token");
                        String uid = userJson.getString("user_id");
                        String loginDate = response.getString("login_time");
                        String gId = userJson.getString("group_id");
                        String gName = userJson.getString("group_name");
                        Integer cId = null;
                        if(!userJson.isNull("class_id")) {
                            cId = userJson.getInt("class_id");
                        }
                        String cName = null;
                        if(!userJson.isNull("class_name")) {
                            cName = userJson.getString("class_name");
                        }
                        Integer lMode = null;
                        if(!userJson.isNull("learnStyle_mode")) {
                            lMode = userJson.getInt("learnStyle_mode");
                        }
                        String mMode = userJson.getString("material_mode");
                        Boolean enableNoAppoint = userJson.getBoolean("enable_noAppoint");
                        String nickName = userJson.getString("nickname");
                        String realName = userJson.getString("realname");
                        String email = userJson.getString("email");

                        // 顯示拿到的Token
                        if(Config.DEBUG_SHOW_MESSAGE) {
                            Toast.makeText(LoginActivity.this, "S: "+token, Toast.LENGTH_SHORT).show();
                        }

                        // 紀錄進資料庫
                        DBProvider db = new DBProvider(LoginActivity.this);
                        db.remove_user();
                        db.insert_user(token, uid, loginDate,
                                gId, gName, cId, cName,
                                lMode, mMode, enableNoAppoint,
                                nickName, realName, email);

                        // 處理伺服端與本機端的時間差
                        String nowDateString = response.getString("login_time");
                        Date serverTime = TimeUtils.stringToDate(nowDateString);
                        TimeUtils.setTimeAdjustByNowServerTime(LoginActivity.this, serverTime);

                        // 抓取可用的教材類型
                        db.removeAll_materialKind();
                        JSONArray materialKindArr = response.getJSONArray("material_kind");
                        for(int i=0; i<materialKindArr.length(); i++) {
                            JSONObject the_mkJSON = materialKindArr.getJSONObject(i);
                            String mkId = the_mkJSON.getString("material_kind_id");
                            String mkName = the_mkJSON.getString("material_kind_name");

                            db.insert_materialKind(mkId, mkName);
                        }


                        // 前往MainActivity
                        finish();
                        Intent to_mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(to_mainActivity);

                    }
                    catch (UnsupportedEncodingException e) {
                        ErrorUtils.error(LoginActivity.this, e);
                    } catch (JSONException e) {
                        ErrorUtils.error(LoginActivity.this, e);
                    } catch (ParseException e) {
                        ErrorUtils.error(LoginActivity.this, e);
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

                    // 找不到此帳號
                    if(statusCode == 404) {
                        mEdit_account.setError(getString(R.string.error_no_account));
                        mEdit_account.requestFocus();
                    }
                    else if(statusCode == 401) {

                        try {
                            String content = new String(responseBody, "UTF-8");
                            JSONObject response = new JSONObject(content);

                            // 密碼錯誤
                            if(response.getInt("substatus") == 201) {
                                mEdit_password.setError(getString(R.string.error_password));
                                mEdit_password.requestFocus();
                            }
                            // 此帳號被停用
                            else if(response.getInt("substatus") == 202) {
                                mEdit_account.setError(getString(R.string.error_account_no_enable));
                                mEdit_account.requestFocus();
                            }
                            // 其他錯誤
                            else {
                                ErrorUtils.error(LoginActivity.this, content);
                            }
                        } catch (UnsupportedEncodingException e) {
                            ErrorUtils.error(LoginActivity.this, e);
                        } catch (JSONException e) {
                            ErrorUtils.error(LoginActivity.this, e);
                        }
                    }
                    // 其他錯誤
                    else {
                        String content = ""+statusCode;
                        if(responseBody != null) {

                            try {
                                content = new String(responseBody, "UTF-8");
                                ErrorUtils.error(LoginActivity.this, content);
                            } catch (UnsupportedEncodingException e) {
                                ErrorUtils.error(LoginActivity.this, e);
                            }
                        }
                        else {
                            ErrorUtils.error(LoginActivity.this, content);
                        }
                    }
                }
            });
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
                ErrorUtils.error(LoginActivity.this, error);
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
