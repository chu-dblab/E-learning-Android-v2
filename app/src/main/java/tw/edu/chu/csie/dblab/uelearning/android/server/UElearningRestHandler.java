package tw.edu.chu.csie.dblab.uelearning.android.server;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.net.UnknownHostException;

/**
 * Created by yuan on 2015/5/19.
 */
public abstract class UElearningRestHandler extends AsyncHttpResponseHandler {
    private static final String LOG_TAG = "Uelearning-RestClient";

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if(statusCode == 0) {
            if(error.getClass().isInstance(UnknownHostException.class)) {
                this.onFailure(statusCode, headers, responseBody, error);
            }
            else {
                this.onNoResponse();
            }
        }
        else if(statusCode == 401) {

            try {
                String content = new String(responseBody, "UTF-8");
                JSONObject response = new JSONObject(content);

                // 密碼錯誤
                if(response.getInt("substatus") == 204) {
                    this.onNoLogin();
                }
                // 其他錯誤
                else {
                    this.onOtherErr(statusCode, headers, responseBody, error);
                }
            } catch (Exception e) {
                this.onOtherErr(statusCode, headers, responseBody, error);
            }
        }
        else {
            this.onOtherErr(statusCode, headers, responseBody, error);
        }
    }

    /**
     * 當已經被登出
     */
    public void onNoLogin() {

    };

    /**
     * 當沒有此學習活動
     */
    public void onNoStudyActivity() {

    }

    /**
     * 當沒有回應時
     */
    public abstract void onNoResponse();

    public abstract void onOtherErr(int statusCode, Header[] headers, byte[] responseBody, Throwable error);

    public abstract void onOtherErr(Throwable e);
}
