package tw.edu.chu.csie.dblab.uelearning.android.learning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.exception.NoLoginException;
import tw.edu.chu.csie.dblab.uelearning.android.exception.NoStudyActivityException;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestHandler;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.LogUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.TimeUtils;

/**
 * Created by yuan on 2015/1/16.
 */
public class TheActivity {

    public static int getActivityId(final Context context) throws NoStudyActivityException {
        DBProvider db = new DBProvider(context);
        Cursor the_query = db.get_activity();
        if(the_query.getCount() > 0) {
            the_query.moveToNext();
            Integer returnData =
                    Integer.valueOf( the_query.getString(the_query.getColumnIndex("SaID")) );
            return returnData;
        }
        else {
            throw new NoStudyActivityException();
        }
    }

    public static Cursor getActivityQuery(final Context context) throws NoStudyActivityException {
        DBProvider db = new DBProvider(context);
        Cursor the_query = db.get_activity();
        if(the_query.getCount() > 0) {
            return the_query;
        }
        else {
            throw new NoStudyActivityException();
        }
    }

    public static void finishTheActivity(final Context context, final UElearningRestHandler handler) {

        try {
            int saId = getActivityId(context);
            ActivityManager.finishStudyActivity(context, saId, handler);
        }
        catch (NoStudyActivityException e) {
            handler.onNoStudyActivity();
        }

    }

    // ---------------------------------------------------------------------------------------------

    public static void updateNextRecommandPoint(final Context context, final int currentTId, final UElearningRestHandler handler) {

        try {
            String token = UserUtils.getToken(context);
            final DBProvider db = new DBProvider(context);
            db.removeAll_recommand();

            // 取得目前學習活動資料
            Cursor query_activity = getActivityQuery(context);
            query_activity.moveToFirst();
            final int saId = query_activity.getInt(query_activity.getColumnIndex("SaID"));
            int enableVirtualInt = query_activity.getInt(query_activity.getColumnIndex("EnableVirtual"));
            boolean enableVirtual;
            if (enableVirtualInt > 0) enableVirtual = true;
            else enableVirtual = false;


            UElearningRestClient.getNextRecommandPoint(token, saId, currentTId, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    handler.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String content = new String(responseBody, "UTF-8");
                        JSONObject response = new JSONObject(content);
                        JSONArray jsonAry_targets = response.getJSONArray("recommand_target");

                        // 使用資料庫
                        db.removeAll_recommand();

                        // 抓取是否已結束
                        boolean isEnd = response.getBoolean("is_end");

                        // 還沒結束的話
                        if (!isEnd) {

                            if (jsonAry_targets.length() <= 0) {

                            }

                            // 抓所有推薦的標的
                            int recommandTid[] = new int[jsonAry_targets.length()];
                            for (int i = 0; i < jsonAry_targets.length(); i++) {
                                JSONObject thisTarget = jsonAry_targets.getJSONObject(i);

                                int tId = thisTarget.getInt("target_id");
                                recommandTid[i] = tId;
                                boolean isEntity = thisTarget.getBoolean("is_entity");

                                // 記錄進資料庫
                                db.insert_recommand(tId, isEntity);
                            }
                            LogUtils.Insert.recommandResult(context, saId, recommandTid);
                        }
                        // 已經結束的話
                        else {

                            // TODO: 改以隨時取得已學習標的數
                            TheActivity.setLearnedPointTotal(context,
                                    TheActivity.getPointTotal(context) - 1);
                        }
                    } catch (NoStudyActivityException e) {
                        handler.onNoStudyActivity();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        handler.onOtherErr(e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    // 沒有此學習活動
                    if(statusCode == 404) {
                        handler.onNoStudyActivity();
                    }
                    else if (statusCode == 401) {
                        handler.onNoLogin();
                    }
                    else if (statusCode == 0) {
                        handler.onNoResponse();
                    }
                    else {
                        handler.onFailure(statusCode, headers, responseBody, error);
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    handler.onRetry(retryNo);
                }
            });
        } catch (UnsupportedEncodingException e) {
            handler.onOtherErr(e);
        } catch (NoLoginException e) {
            handler.onNoLogin();
        } catch (NoStudyActivityException e) {
            handler.onNoStudyActivity();
        }
    }

    // =============================================================================================

    /**
     * 取得開始學習的時間物件
     * @return 已學習的Date物件
     */
    @SuppressLint("SimpleDateFormat")
    public static Date getStartDate(final Context context) throws NoStudyActivityException {
        // 取得開始學習時間
        Cursor query = getActivityQuery(context);

        if(query.getCount()>0) {

            query.moveToFirst();
            String startDateDB = query.getString(query.getColumnIndex("StartTime"));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = new Date();
            try {
                startDate = format.parse(startDateDB);

                return startDate;

            } catch (ParseException e) {
                ErrorUtils.error(context, e);
                return null;
            }
        }
        else return null;
    }


    /**
     * 取得已經學習的時間物件
     * @return 已學習的Date物件
     */
    @SuppressLint("SimpleDateFormat")
    public static Date getLearningTime(Context context) throws NoStudyActivityException {
        // 取得現在時間
        Date nowDate = TimeUtils.getNowServerTime(context);

        // 取得開始學習時間
        Cursor query = getActivityQuery(context);
        if(query.getCount()>0) {

            query.moveToFirst();
            String startDateDB = query.getString(query.getColumnIndex("StartTime"));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = new Date();
            try {
                startDate = format.parse(startDateDB);

                // 回傳時間差
                Date date= new Date(nowDate.getTime()-startDate.getTime());
                return date;

            } catch (ParseException e) {
                ErrorUtils.error(context, e);
                return null;
            }
        }
        else return null;
    }

    /**
     * 取得已經學了多少分鐘
     * @return 分鐘
     */
    public static Integer getLearningMinTime(Context context) throws NoStudyActivityException {
        Date learningDate = getLearningTime(context);

        if(learningDate != null) {

            Calendar learningCal = Calendar.getInstance();
            learningCal.setTime(learningDate);
            learningCal.setTimeZone(TimeZone.getTimeZone("UTC"));

            return learningCal.get(Calendar.HOUR_OF_DAY)*60 + learningCal.get(Calendar.MINUTE);
        }
        return null;
    }

    /**
     * 取得可學習的時間物件
     * @return 可學習的時間Date物件
     */
    public static Date getlimitTime(Context context) throws NoStudyActivityException {

        Calendar limitCal = Calendar.getInstance();
        limitCal.setTime(new Date(0));
        Integer limitMin = getlimitMin(context);
        if(limitMin != null) {
            limitCal.set(Calendar.MINUTE, limitMin);
            return limitCal.getTime();
        }
        else return null;
    }

    /**
     * 取得可學習的分鐘
     * @return 可學習的分鐘
     */
    public static Integer getlimitMin(Context context) throws NoStudyActivityException {
        // 取得開始學習時間
        Cursor query = getActivityQuery(context);
        if(query.getCount()>0) {
            query.moveToFirst();
            int limitMin = query.getInt(query.getColumnIndex("LearnTime"));
            return limitMin;
        }
        else return null;
    }

    /**
     * 取得剩餘學習時間物件
     * @return 剩餘學習時間Date物件
     */
    public static Date getRemainderLearningTime(Context context) throws NoStudyActivityException {
        Date limitDate = getlimitTime(context);
        Date learningDate = getLearningTime(context);

        long milliseconds = limitDate.getTime() - learningDate.getTime();

        if(milliseconds > 0) return new Date(milliseconds);
        else return new Date(0);
    }

    /**
     * 取得剩餘學習時間分鐘
     * @return 剩餘學習時間分鐘
     */
    public static int getRemainderLearningMinTime(Context context) throws NoStudyActivityException {
        Date remainderLearningDate = getRemainderLearningTime(context);

        Calendar learningCal = Calendar.getInstance();
        learningCal.setTime(remainderLearningDate);
        learningCal.setTimeZone(TimeZone.getTimeZone("UTC"));

        return learningCal.get(Calendar.HOUR_OF_DAY)*60 + learningCal.get(Calendar.MINUTE);
    }

    /**
     * 是否已學習逾時
     * @return 是否已學習逾時
     */
    public static boolean isLearningTimeOver(Context context) throws NoStudyActivityException {
        if(getRemainderLearningTime(context).getTime() <= 0) return true;
        else return false;
    }

    // =============================================================================================

    public static int getPointTotal(Context context) throws NoStudyActivityException {
        Cursor query = getActivityQuery(context);
        int result = 0;
        if(query.getCount()>0) {
            query.moveToFirst();
            result = query.getInt(query.getColumnIndex("TargetTotal"));
        }
        return result;
    }

    public static int getLearnedPointTotal(Context context) throws NoStudyActivityException {
        Cursor query = getActivityQuery(context);
        int result = 0;
        if(query.getCount()>0) {
            query.moveToFirst();
            result = query.getInt(query.getColumnIndex("LearnedTotal"));
        }
        return result;
    }

    public static void setLearnedPointTotal(Context context, int total) throws NoStudyActivityException {
        Cursor query = getActivityQuery(context);
        DBProvider db = new DBProvider(context);
        db.set_activity_learnedPointTotal(total);
    }

    public static int getRemainingPointTotal(Context context) throws NoStudyActivityException {
        return getPointTotal(context)-1 - getLearnedPointTotal(context);
    }
}
