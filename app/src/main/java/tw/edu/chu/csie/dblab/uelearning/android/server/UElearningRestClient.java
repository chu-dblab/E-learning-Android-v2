package tw.edu.chu.csie.dblab.uelearning.android.server;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;

import tw.edu.chu.csie.dblab.uelearning.android.config.Config;

/**
 * Created by yuan on 2014/11/18.
 */
public class UElearningRestClient {
    private static final String BASE_URL = Config.REMOTE_API_URL;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static RequestHandle get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient.allowRetryExceptionClass(IOException.class);
        AsyncHttpClient.allowRetryExceptionClass(SocketTimeoutException.class);
        AsyncHttpClient.allowRetryExceptionClass(ConnectTimeoutException.class);
        AsyncHttpClient.blockRetryExceptionClass(UnknownHostException.class);
        AsyncHttpClient.blockRetryExceptionClass(ConnectionPoolTimeoutException.class);
        return client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static RequestHandle post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient.allowRetryExceptionClass(IOException.class);
        AsyncHttpClient.allowRetryExceptionClass(SocketTimeoutException.class);
        AsyncHttpClient.allowRetryExceptionClass(ConnectTimeoutException.class);
        AsyncHttpClient.blockRetryExceptionClass(UnknownHostException.class);
        AsyncHttpClient.blockRetryExceptionClass(ConnectionPoolTimeoutException.class);
        return client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static RequestHandle post(Context context, String url, StringEntity params, String type, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient.allowRetryExceptionClass(IOException.class);
        AsyncHttpClient.allowRetryExceptionClass(SocketTimeoutException.class);
        AsyncHttpClient.allowRetryExceptionClass(ConnectTimeoutException.class);
        AsyncHttpClient.blockRetryExceptionClass(UnknownHostException.class);
        AsyncHttpClient.blockRetryExceptionClass(ConnectionPoolTimeoutException.class);
        return client.post(context, getAbsoluteUrl(url), params, type, responseHandler);
    }

    public static RequestHandle put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient.allowRetryExceptionClass(IOException.class);
        AsyncHttpClient.allowRetryExceptionClass(SocketTimeoutException.class);
        AsyncHttpClient.allowRetryExceptionClass(ConnectTimeoutException.class);
        AsyncHttpClient.blockRetryExceptionClass(UnknownHostException.class);
        AsyncHttpClient.blockRetryExceptionClass(ConnectionPoolTimeoutException.class);
        return client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public static RequestHandle delete(String url, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient.allowRetryExceptionClass(IOException.class);
        AsyncHttpClient.allowRetryExceptionClass(SocketTimeoutException.class);
        AsyncHttpClient.allowRetryExceptionClass(ConnectTimeoutException.class);
        AsyncHttpClient.blockRetryExceptionClass(UnknownHostException.class);
        AsyncHttpClient.blockRetryExceptionClass(ConnectionPoolTimeoutException.class);
        return client.delete(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(final String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    /**
     * 使用者登入
     * @param userId
     * @param userPasswd
     * @param responseHandler
     */
    public static RequestHandle userLogin(final String userId, final String userPasswd, final AsyncHttpResponseHandler responseHandler) {

        // 帶入登入參數
        RequestParams login_params = new RequestParams();
        login_params.put("user_id", userId);
        login_params.put("password", userPasswd);
        login_params.put("browser", "android");

        // 對伺服端進行登入動作
        return UElearningRestClient.post("/tokens", login_params, responseHandler);
    }

    /**
     * 使用者登出
     * @param token
     * @param responseHandler
     * @throws UnsupportedEncodingException
     */
    public static RequestHandle userLogout(final String token, final AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {

        // 對伺服端進行登入動作
        return UElearningRestClient.delete("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8), responseHandler);
    }

    /**
     * 取得可用的學習活動清單
     * @param token
     * @param responseHandler
     */
    public static RequestHandle getEnableActivityList(final String token, final AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {

        return UElearningRestClient.get("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) + "/activitys", null, responseHandler);
    }

    /**
     * 開始進行新的學習活動
     * @param token
     * @param thId
     * @param _learnTime
     * @param _timeForce
     * @param _lMode
     * @param _lForce
     * @param _mMode
     * @param responseHandler
     */
    public static RequestHandle startStudyActivity(final String token, final int thId,
                                          final Integer _learnTime, final Boolean _timeForce,
                                          final Integer _lMode, final Boolean _lForce, final String _mMode,
                                          final AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {

        RequestParams startActivity_params = new RequestParams();
        // 帶入登入參數
        startActivity_params.put("theme_id", thId);
        if(_learnTime != null) startActivity_params.put("learn_time", _learnTime);
        if(_timeForce != null) {
            if(_timeForce == true) startActivity_params.put("time_force", "1");
            else startActivity_params.put("time_force", "0");
        }
        if(_lMode != null)     startActivity_params.put("learnStyle_mode", _lMode);
        if(_lForce != null) {
            if(_lForce == true) startActivity_params.put("learnStyle_force", "1");
            else startActivity_params.put("learnStyle_force", "0");
        }
        if(_mMode != null)     startActivity_params.put("material_mode", _mMode);

        return UElearningRestClient.post("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) + "/activitys", startActivity_params, responseHandler);
    }

    /**
     * 取得活動資訊
     * @param token
     * @param saId
     * @param responseHandler
     * @throws UnsupportedEncodingException
     */
    public static RequestHandle getActivityInfo(final String token, final int saId, final AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {

        return UElearningRestClient.get("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) + "/activitys/" + saId, null, responseHandler);
    }

    /**
     * 結束本次的學習活動
     * @param token
     * @param saId
     * @param responseHandler
     * @throws UnsupportedEncodingException
     */
    public static RequestHandle finishStudyActivity(final String token, final int saId, final AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {

        RequestParams finish_params = new RequestParams();
        return UElearningRestClient.post("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) + "/activitys/" + saId + "/finish",
                finish_params, responseHandler);
    }

    public static RequestHandle getNextRecommandPoint (final String token, final int saId, final int currentTId, final AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {

        final RequestParams recommand_params = new RequestParams();
        return UElearningRestClient.post("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) +
                "/activitys/" + saId + "/recommand?current_point=" + currentTId, recommand_params, responseHandler);
    }

}
