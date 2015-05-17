package tw.edu.chu.csie.dblab.uelearning.android.server;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;

import tw.edu.chu.csie.dblab.uelearning.android.config.Config;

/**
 * Created by yuan on 2014/11/18.
 */
public class UElearningRestClient {
    private static final String BASE_URL = Config.REMOTE_API_URL;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context, String url, StringEntity params, String type, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), params, type, responseHandler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void userLogin(String userId, String userPasswd, AsyncHttpResponseHandler responseHandler) {
        // 帶入登入參數
        RequestParams login_params = new RequestParams();
        login_params.put("user_id", userId);
        login_params.put("password", userPasswd);
        login_params.put("browser", "android");

        // 對伺服端進行登入動作
        UElearningRestClient.post("/tokens", login_params, responseHandler);
    }



}
