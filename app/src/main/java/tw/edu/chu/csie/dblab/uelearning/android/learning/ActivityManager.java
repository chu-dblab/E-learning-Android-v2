package tw.edu.chu.csie.dblab.uelearning.android.learning;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.exception.NoLoginException;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestHandler;
import tw.edu.chu.csie.dblab.uelearning.android.ui.MainActivity;

/**
 * Created by yuan on 2015/5/19.
 */
public class ActivityManager {

    public static void updateEnableActivityList(final Context context, final UElearningRestHandler handler) {

        try {
            // 取得目前登入的Token
            String token = UserUtils.getToken(context);

            UElearningRestClient.getEnableActivityList(URLEncoder.encode(token, HTTP.UTF_8), new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    handler.onStart();
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String content;
                    try {
                        content = new String(responseBody, HTTP.UTF_8);
                        JSONObject response = new JSONObject(content);

                        JSONArray jsonArr_enableStudy = response.getJSONArray("enable_activity");

                        // 清除目前的活動清單
                        DBProvider db = new DBProvider(context);
                        db.removeAll_enableActivity();

                        // 抓其中一個活動
                        for (int i = 0; i < jsonArr_enableStudy.length(); i++) {
                            JSONObject thisActivity = jsonArr_enableStudy.getJSONObject(i);

                            // 抓取資料
                            String type = thisActivity.getString("type");
                            Integer saId = null;
                            if (!thisActivity.isNull("activity_id")) {
                                saId = thisActivity.getInt("activity_id");
                            }
                            Integer swId = null;
                            if (!thisActivity.isNull("activity_will_id")) {
                                swId = thisActivity.getInt("activity_will_id");
                            }
                            int thId = thisActivity.getInt("theme_id");
                            String thName = thisActivity.getString("theme_name");
                            String thIntroduction = thisActivity.getString("theme_introduction");
                            String startTime = thisActivity.getString("start_time");
                            String expiredTime = thisActivity.getString("expired_time");
                            int learnTime = thisActivity.getInt("remaining_time");
                            Boolean timeForce = thisActivity.getBoolean("time_force");
                            Integer lMode = null;
                            if (!thisActivity.isNull("learnStyle_mode")) {
                                lMode = thisActivity.getInt("learnStyle_mode");
                            }
                            Boolean lForce = thisActivity.getBoolean("learnStyle_force");
                            Boolean enableVirtual = thisActivity.getBoolean("enable_virtual");
                            String mMode = thisActivity.getString("material_mode");
                            Boolean lock = thisActivity.getBoolean("lock");
                            int targetTotal = thisActivity.getInt("target_total");
                            int learnedTotal = thisActivity.getInt("learned_total");

                            int typeId;
                            if (type.equals("theme")) typeId = DBProvider.TYPE_THEME;
                            else if (type.equals("will")) typeId = DBProvider.TYPE_WILL;
                            else if (type.equals("study")) typeId = DBProvider.TYPE_STUDY;
                            else typeId = 0;

                            // 紀錄進資料庫裡
                            db.insert_enableActivity(db.get_user_id(), typeId, saId, swId,
                                    thId, thName, thIntroduction, startTime, expiredTime,
                                    learnTime, timeForce, lMode, lForce, enableVirtual, mMode,
                                    lock, targetTotal, learnedTotal);
                        }
                        handler.onSuccess(statusCode, headers, responseBody);

                    } catch (UnsupportedEncodingException | JSONException e) {
                        handler.onOtherErr(e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 401) {
                        handler.onNoLogin();
                    }
                    else if (statusCode == 0) {
                        handler.onNoResponse();
                    } else {
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
     * 開始進行學習
     *
     * @param thId 主題編號
     * @param _learnTime 學習時間
     * @param _timeForce 時間到強制結束學習
     * @param _lMode 學習導引模式
     * @param _lForce 強制學習導引
     * @param _mMode 教材模式
     */
    public static void startStudyActivity(final Context context, final int thId,
                                          final Integer _learnTime, final Boolean _timeForce,
                                          final Integer _lMode, final Boolean _lForce, final String _mMode,
                                          final UElearningRestHandler handler) {

        try {
            // 抓取目前已登入的Token
            final String token = UserUtils.getToken(context);

            // 對伺服器加入新的學習活動
            UElearningRestClient.startStudyActivity(token, thId, _learnTime, _timeForce, _lMode, _lForce, _mMode, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    handler.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    try {
                        String content = new String(responseBody, "UTF-8");
                        final JSONObject response = new JSONObject(content);
                        JSONObject activityJson = response.getJSONObject("activity");
                        JSONArray jsonAtt_targets = response.getJSONArray("targets");


                        // TODO: 對照輸入的資訊與伺服端接到的資訊是否吻合
                        int saId = activityJson.getInt("activity_id");
                        String thName = activityJson.getString("theme_name");
                        int startTId = activityJson.getInt("start_target_id");
                        String startTime = activityJson.getString("start_time");
                        String expiredTime = activityJson.getString("expired_time");
                        int targetTotal = activityJson.getInt("target_total");
                        int learnedTotal = activityJson.getInt("learned_total");
                        int learnTime = activityJson.getInt("have_time");
                        boolean timeForce;
                        if (activityJson.getString("time_force") == "true")
                            timeForce = true;
                        else timeForce = false;
                        int lMode = activityJson.getInt("learnStyle_mode");
                        boolean lForce;
                        if (activityJson.getString("learnStyle_force") == "true")
                            lForce = true;
                        else lForce = false;
                        boolean enableVirtual;
                        if (activityJson.getString("enable_virtual") == "true")
                            enableVirtual = true;
                        else enableVirtual = false;
                        String mMode = activityJson.getString("material_mode");

                        // 紀錄進資料庫
                        DBProvider db = new DBProvider(context);
                        db.removeAll_activity();
                        db.removeAll_recommand();
                        db.removeAll_target();

                        // 向伺服端取得今次活動所有的標的資訊
                        for (int i = 0; i < jsonAtt_targets.length(); i++) {
                            JSONObject thisTarget = jsonAtt_targets.getJSONObject(i);

                            int thId = thisTarget.getInt("theme_id");
                            int tId = thisTarget.getInt("target_id");
                            Integer hId = null;
                            if (!thisTarget.isNull("hall_id")) {
                                hId = thisTarget.getInt("hall_id");
                            }
                            String hName = thisTarget.getString("hall_name");
                            Integer aId = null;
                            if (!thisTarget.isNull("area_id")) {
                                aId = thisTarget.getInt("area_id");
                            }
                            String aName = thisTarget.getString("area_name");
                            Integer aFloor = null;
                            if (!thisTarget.isNull("floor")) {
                                aFloor = thisTarget.getInt("floor");
                            }
                            Integer aNum = null;
                            if (!thisTarget.isNull("area_number")) {
                                aNum = thisTarget.getInt("area_number");
                            }
                            Integer tNum = null;
                            if (!thisTarget.isNull("target_number")) {
                                tNum = thisTarget.getInt("target_number");
                            }
                            String tName = thisTarget.getString("name");
                            int targetLearnTime = thisTarget.getInt("learn_time");  //這邊
                            String mapUrl = thisTarget.getString("map_url");
                            String materialUrl = thisTarget.getString("material_url");
                            String virtualMaterialUrl = thisTarget.getString("virtual_material_url");

                            // 記錄進資料庫
                            db.insert_target(thId, tId, hId, hName, aId, aName, aFloor, aNum, tNum, tName, targetLearnTime, mapUrl, materialUrl, virtualMaterialUrl);
                        }

                        db.insert_activity(db.get_user_id(), saId,
                                thId, thName, startTId, startTime, learnTime, timeForce,
                                lMode, lForce, enableVirtual, mMode, targetTotal, learnedTotal);
                        db.insert_enableActivity(db.get_user_id(), DBProvider.TYPE_STUDY,
                                saId, null, thId, thName, null,
                                startTime, expiredTime, learnTime, timeForce,
                                lMode, lForce, enableVirtual, mMode, true, targetTotal, learnedTotal);

                        handler.onSuccess(statusCode, headers, responseBody);

                    } catch (UnsupportedEncodingException e) {
                        handler.onOtherErr(e);
                    } catch (JSONException e) {
                        handler.onOtherErr(e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 401) {
                        handler.onNoLogin();
                    }
                    else if (statusCode == 0) {
                        handler.onNoResponse();
                    } else {
                        handler.onOtherErr(statusCode, headers, responseBody, error);
                    }
                }
            });
        }
        catch (NoLoginException e) {
            handler.onNoLogin();
        } catch (UnsupportedEncodingException e) {
            handler.onOtherErr(e);
        }
    }

    public static void resumeStudyActivity(final Context context, final int saId, final UElearningRestHandler handler) {

        try {
            // 取得登入Token
            String token = UserUtils.getToken(context);

            // 向伺服器查詢學習活動資訊
            UElearningRestClient.getActivityInfo(token, saId, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    handler.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String content = new String(responseBody, "UTF-8");
                        JSONObject response = new JSONObject(content);
                        JSONObject activityJson = response.getJSONObject("activity");
                        JSONArray jsonAtt_targets = response.getJSONArray("targets");

                        int saId = activityJson.getInt("activity_id");
                        int thId = activityJson.getInt("theme_id");
                        String thName = activityJson.getString("theme_name");
                        int startTId = activityJson.getInt("start_target_id");
                        String startTime = activityJson.getString("start_time");
                        String expiredTime = activityJson.getString("expired_time");
                        int targetTotal = activityJson.getInt("target_total");
                        int learnedTotal = activityJson.getInt("learned_total");
                        int learnTime = activityJson.getInt("have_time");
                        boolean timeForce;
                        if(activityJson.getString("time_force") == "true")
                            timeForce = true;
                        else timeForce = false;
                        int lMode = activityJson.getInt("learnStyle_mode");
                        boolean lForce;

                        if(activityJson.getString("learnStyle_force") == "true")
                            lForce = true;
                        else lForce = false;
                        boolean enableVirtual;
                        if(activityJson.getString("enable_virtual") == "true")
                            enableVirtual = true;
                        else enableVirtual = false;
                        String mMode = activityJson.getString("material_mode");

                        // 向伺服端取得今次活動所有的標的資訊
                        DBProvider db = new DBProvider(context);
                        db.removeAll_target();
                        for (int i = 0; i < jsonAtt_targets.length(); i++) {
                            JSONObject thisTarget = jsonAtt_targets.getJSONObject(i);

                            thId = thisTarget.getInt("theme_id");
                            int tId = thisTarget.getInt("target_id");
                            Integer hId = null;
                            if(!thisTarget.isNull("hall_id")) {
                                hId = thisTarget.getInt("hall_id");
                            }
                            String hName = thisTarget.getString("hall_name");
                            Integer aId = null;
                            if(!thisTarget.isNull("area_id")) {
                                aId = thisTarget.getInt("area_id");
                            }
                            String aName = thisTarget.getString("area_name");
                            Integer aFloor = null;
                            if(!thisTarget.isNull("floor")) {
                                aFloor = thisTarget.getInt("floor");
                            }
                            Integer aNum = null;
                            if(!thisTarget.isNull("area_number")) {
                                aNum = thisTarget.getInt("area_number");
                            }
                            Integer tNum = null;
                            if(!thisTarget.isNull("target_number")) {
                                tNum = thisTarget.getInt("target_number");
                            }
                            String tName = thisTarget.getString("name");
                            int targetLearnTime = thisTarget.getInt("learn_time");
                            String mapUrl = thisTarget.getString("map_url");
                            String materialUrl = thisTarget.getString("material_url");
                            String virtualMaterialUrl = thisTarget.getString("virtual_material_url");

                            // 記錄進資料庫
                            db.insert_target(thId, tId, hId, hName, aId, aName, aFloor, aNum, tNum, tName, targetLearnTime, mapUrl, materialUrl, virtualMaterialUrl);
                        }

                        // 紀錄進資料庫
                        db.removeAll_activity();
                        db.insert_activity(db.get_user_id(), saId,
                                thId, thName, startTId, startTime, learnTime, timeForce,
                                lMode, lForce, enableVirtual, mMode, targetTotal, learnedTotal);

                        handler.onSuccess(statusCode, headers, responseBody);

                    } catch (JSONException e) {
                        handler.onOtherErr(e);
                    } catch (UnsupportedEncodingException e) {
                        handler.onOtherErr(e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 401) {
                        handler.onNoLogin();
                    }
                    else if (statusCode == 0) {
                        handler.onNoResponse();
                    } else {
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

    public static void finishStudyActivity(final Context context, final int saId, final UElearningRestHandler handler) {

        // 取得登入Token
        try {
            String token = UserUtils.getToken(context);

            UElearningRestClient.finishStudyActivity(token, saId, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    handler.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String content = new String(responseBody, "UTF-8");
                        JSONObject response = new JSONObject(content);
                        JSONObject activityJson = response.getJSONObject("activity");

                        // 紀錄進資料庫
                        DBProvider db = new DBProvider(context);
                        int saId = db.get_activity_id();
                        db.remove_enableActivity_inStudying_bySaId(saId);
                        db.removeAll_target();
                        db.removeAll_recommand();
                        db.removeAll_activity();

                        handler.onSuccess(statusCode, headers, responseBody);
                    } catch (UnsupportedEncodingException e) {
                        handler.onOtherErr(e);
                    } catch (JSONException e) {
                        handler.onOtherErr(e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    // 此學習活動早已結束
                    if(statusCode == 405) {

                        // 紀錄進資料庫
                        DBProvider db = new DBProvider(context);
                        int saId = db.get_activity_id();
                        db.remove_enableActivity_inStudying_bySaId(saId);
                        db.removeAll_target();
                        db.removeAll_recommand();
                        db.removeAll_activity();

                        handler.onSuccess(statusCode, headers, responseBody);
                    }
                    else if (statusCode == 401) {
                        handler.onNoLogin();
                    }
                    else if (statusCode == 0) {
                        handler.onNoResponse();
                    } else {
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

}
