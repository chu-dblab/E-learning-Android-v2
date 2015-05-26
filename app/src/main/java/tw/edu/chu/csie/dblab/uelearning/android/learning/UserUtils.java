package tw.edu.chu.csie.dblab.uelearning.android.learning;


import android.content.Context;
import android.database.Cursor;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;

import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.exception.NoLoginException;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestHandler;
import tw.edu.chu.csie.dblab.uelearning.android.util.TimeUtils;

/**
 * Created by yuan on 2015/5/17.
 */
public class UserUtils {

    public static abstract class UserLoginHandler extends UElearningRestHandler {
        private static final String LOG_TAG = "Uelearning-Login";
        public abstract void onStart();
        public abstract void onNoUser();
        public abstract void onPasswordErr();
        public abstract void onNoEnable();
    }

    /**
     * 進行使用者登入
     * @param context
     * @param userId
     * @param userPasswd
     * @param loginHandler
     */
    public static void userLogin(final Context context, final String userId, final String userPasswd, final UserLoginHandler loginHandler) {

        UElearningRestClient.userLogin(userId, userPasswd, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                loginHandler.onStart();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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

                    // 紀錄進資料庫
                    DBProvider db = new DBProvider(context);
                    db.removeAllUserData();
                    db.remove_user();
                    db.insert_user(token, uid, loginDate,
                            gId, gName, cId, cName,
                            lMode, mMode, enableNoAppoint,
                            nickName, realName, email);

                    // 處理伺服端與本機端的時間差
                    String nowDateString = response.getString("login_time");
                    Date serverTime = TimeUtils.stringToDate(nowDateString);
                    TimeUtils.setTimeAdjustByNowServerTime(context, serverTime);

                    // 抓取可用的教材類型
                    db.removeAll_materialKind();
                    JSONArray materialKindArr = response.getJSONArray("material_kind");
                    for(int i=0; i<materialKindArr.length(); i++) {
                        JSONObject the_mkJSON = materialKindArr.getJSONObject(i);
                        String mkId = the_mkJSON.getString("material_kind_id");
                        String mkName = the_mkJSON.getString("material_kind_name");

                        db.insert_materialKind(mkId, mkName);
                    }


                    // 送出成功訊息
                    loginHandler.onSuccess(statusCode, headers, responseBody);
                }
                catch (UnsupportedEncodingException e) {
                    loginHandler.onOtherErr(e);
                } catch (JSONException e) {
                    loginHandler.onOtherErr(e);
                } catch (ParseException e) {
                    loginHandler.onOtherErr(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 找不到此帳號
                if(statusCode == 404) {
                    loginHandler.onNoUser();
                }
                else if(statusCode == 401) {

                    try {
                        String content = new String(responseBody, "UTF-8");
                        JSONObject response = new JSONObject(content);

                        // 密碼錯誤
                        if(response.getInt("substatus") == 201) {
                            loginHandler.onPasswordErr();
                        }
                        // 此帳號被停用
                        else if(response.getInt("substatus") == 202) {
                            loginHandler.onNoEnable();
                        }
                        // 其他錯誤
                        else {
                            loginHandler.onOtherErr(error);
                        }
                    } catch (UnsupportedEncodingException e) {
                        loginHandler.onOtherErr(error);
                    } catch (JSONException e) {
                        loginHandler.onOtherErr(error);
                    }
                }
                else if(statusCode == 0) {
                    loginHandler.onNoResponse();
                }
                else {
                    loginHandler.onOtherErr(statusCode, headers, responseBody, error);
                }
            }
        });
    }

    /**
     * 是否已經登入了
     * @param context
     * @return
     */
    public static boolean isLogin(final Context context) {
        // 取得資料庫中的登入資訊
        DBProvider db = new DBProvider(context);
        Cursor mDb_user = db.get_user();

        // 如果尚未登入
        if(mDb_user.getCount() == 0) {
            return false;
        }
        // 如果是已登入
        else {
            return true;
        }
    }

    /**
     * 使用者登出
     * @param context
     * @param handler
     * @return
     */
    public static void userLogout(final Context context, final UElearningRestHandler handler) {
        try {
            String token = getToken(context);

            UElearningRestClient.userLogout(token, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    handler.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    new DBProvider(context).removeAllUserData();
                    handler.onSuccess(statusCode, headers, responseBody);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(statusCode == 404) {
                        handler.onNoLogin();
                    }
                    else if(statusCode == 0) {
                        handler.onNoResponse();
                    }
                    else {
                        handler.onOtherErr(statusCode, headers, responseBody, error);
                    }
                }
            });
        } catch (NoLoginException e) {
            handler.onNoLogin();
        } catch (UnsupportedEncodingException e) {
            handler.onOtherErr(e);
        }
    }

    /**
     * 抓取目前已登入的帳號ID
     * @param context
     * @return
     * @throws NoLoginException
     */
    public static String getUserId(final Context context) throws NoLoginException {

        if(isLogin(context)) {
            DBProvider db = new DBProvider(context);
            String user_id = db.get_user_id();
            return user_id;
        }
        else {
            throw new NoLoginException();
        }
    }

    /**
     * 抓取目前已登入的Token
     * @param context
     * @return
     * @throws NoLoginException
     */
    public static String getToken(final Context context) throws NoLoginException {

        if(isLogin(context)) {
            DBProvider db = new DBProvider(context);
            String token = db.get_token();
            return token;
        }
        else {
            throw new NoLoginException();
        }
    }
}
