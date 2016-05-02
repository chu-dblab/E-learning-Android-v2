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

    /**
     * 取得目前的活動編號
     * @deprecated 請以生成物建方式為主，不再建議使用
     * @param context
     * @return 活動編號
     * @throws NoStudyActivityException
     */
    @Deprecated
    public static int getActivityId(final Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getActivityId();
    }

    /**
     * 取的目前活動的資料庫查詢結果
     * @deprecated 請以生成物建方式為主，不再建議使用
     * @param context
     * @return
     * @throws NoStudyActivityException
     */
    @Deprecated
    public static Cursor getActivityQuery(final Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getActivityQuery();
    }

    /**
     * 結束本次活動
     * @param context
     * @param handler
     */
    public static void finishTheActivity(final Context context, final UElearningRestHandler handler) {

        try {
            int saId = getActivityId(context);
            ActivityManager.finishStudyActivity(context, saId, handler);
        }
        catch (NoStudyActivityException e) {
            handler.onNoStudyActivity();
        }

    }

    /**
     * 強制結束學習活動（不通知伺服端）
     * @param context
     */
    public static void forceFinishTheActivity(final Context context) {
        int saId = 0;
        try {
            saId = getActivityId(context);
            ActivityManager.forceFinishStudyActivity(context, saId);
        } catch (NoStudyActivityException e) {
            DBProvider db = new DBProvider(context);
            db.finishStudy();
        }
    }

    // =============================================================================================

    /**
     * 取得開始學習的時間物件
     * @deprecated 請以生成物建方式為主，不再建議使用
     * @return 已學習的Date物件
     */
    @Deprecated
    @SuppressLint("SimpleDateFormat")
    public static Date getStartDate(final Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getStartDate();
    }


    /**
     * 取得已經學習的時間物件
     * @deprecated 請以生成物建方式為主，不再建議使用
     * @return 已學習的Date物件
     */
    @Deprecated
    @SuppressLint("SimpleDateFormat")
    public static Date getLearningTime(Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getLearningTime();
    }

    /**
     * 取得已經學了多少分鐘
     * @deprecated 請以生成物建方式為主，不再建議使用
     * @return 分鐘
     */
    @Deprecated
    public static Integer getLearningMinTime(Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getLearningMinTime();
    }

    /**
     * 取得可學習的時間物件
     * @deprecated 請以生成物建方式為主，不再建議使用
     * @return 可學習的時間Date物件
     */
    @Deprecated
    public static Date getlimitTime(Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getlimitTime();
    }

    /**
     * 取得可學習的分鐘
     * @deprecated 請以生成物建方式為主，不再建議使用
     * @return 可學習的分鐘
     */
    @Deprecated
    public static Integer getlimitMin(Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getlimitMin();
    }

    /**
     * 取得剩餘學習時間物件
     * @deprecated 請以生成物建方式為主，不再建議使用
     * @return 剩餘學習時間Date物件
     */
    @Deprecated
    public static Date getRemainderLearningTime(Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getRemainderLearningTime();
    }

    /**
     * 取得剩餘學習時間分鐘
     * @deprecated 請以生成物建方式為主，不再建議使用
     * @return 剩餘學習時間分鐘
     */
    @Deprecated
    public static int getRemainderLearningMinTime(Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getRemainderLearningMinTime();
    }

    /**
     * 是否已學習逾時
     * @deprecated 請以生成物建方式為主，不再建議使用
     * @return 是否已學習逾時
     */
    @Deprecated
    public static boolean isLearningTimeOver(Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.isLearningTimeOver();
    }

    // =============================================================================================

    @Deprecated
    public static int getPointTotal(Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getPointTotal();
    }

    @Deprecated
    public static int getLearnedPointTotal(Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getLearnedPointTotal();
    }

    @Deprecated
    public static void setLearnedPointTotal(Context context, int total) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        theActivity.setLearnedPointTotal(total);
    }

    @Deprecated
    public static int getRemainingPointTotal(Context context) throws NoStudyActivityException {
        TheActivity theActivity = new TheActivity(context);
        return theActivity.getRemainingPointTotal();
    }

    // =============================================================================================
    private Context context;
    private Cursor query;
    private int saId;
    private int lMode;
    private String startDateDB;
    private int limitMin;
    private boolean enableVirtual;

    public TheActivity(final Context context) throws NoStudyActivityException {
        this.context = context;

        DBProvider db = new DBProvider(context);
        query = db.get_activity();

        if(query.getCount() > 0) {
            query.moveToNext();
            saId = Integer.valueOf(query.getString(query.getColumnIndex("SaID")));
            lMode = Integer.valueOf(query.getString(query.getColumnIndex("LMode")));
            startDateDB = query.getString(query.getColumnIndex("StartTime"));
            limitMin = query.getInt(query.getColumnIndex("LearnTime"));

            int enableVirtualInt = query.getInt(query.getColumnIndex("EnableVirtual"));
            if (enableVirtualInt > 0) enableVirtual = true;
            else enableVirtual = false;

        }
        else {
            throw new NoStudyActivityException();
        }
    }

    /**
     * 取的活動編號
     * @return 活動編號
     */
    public int getActivityId() {
        return saId;
    }

    /**
     * 取得此活動查詢結果物件
     * @deprecated 請使用其他function來取得資料，不建議直接使用此function
     * @return 此活動查詢結果物件
     */
    @Deprecated
    public Cursor getActivityQuery() {
        return query;
    }

    /**
     * 取得此活動採用何種學習導引模式
     * @return 將會推薦幾個學習點
     */
    public int getLearnMode() {
        return lMode;
    }
    /**
     * 取得此活動是否有啟用虛擬教材功能
     * @return 是否有啟用虛擬教材功能
     */
    public boolean isEnableVirtual() {
        return enableVirtual;
    }

    /**
     * 取得開始學習的時間物件
     * @return 已學習的Date物件
     */
    @SuppressLint("SimpleDateFormat")
    public Date getStartDate() {

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


    /**
     * 取得已經學習的時間物件
     * @return 已學習的Date物件
     */
    @SuppressLint("SimpleDateFormat")
    public Date getLearningTime() {
        // 取得現在時間
        Date nowDate = TimeUtils.getNowServerTime(context);

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

    /**
     * 取得已經學了多少分鐘
     * @return 分鐘
     */
    public Integer getLearningMinTime() {
        Date learningDate = getLearningTime();

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
    public Date getlimitTime() {

        Calendar limitCal = Calendar.getInstance();
        limitCal.setTime(new Date(0));
        Integer limitMin = getlimitMin();
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
    public Integer getlimitMin() {
        return limitMin;
    }

    /**
     * 取得剩餘學習時間物件
     * @return 剩餘學習時間Date物件
     */
    public Date getRemainderLearningTime() {
        Date limitDate = getlimitTime();
        Date learningDate = getLearningTime();

        long milliseconds = limitDate.getTime() - learningDate.getTime();

        if(milliseconds > 0) return new Date(milliseconds);
        else return new Date(0);
    }

    /**
     * 取得剩餘學習時間分鐘
     * @return 剩餘學習時間分鐘
     */
    public int getRemainderLearningMinTime() {
        Date remainderLearningDate = getRemainderLearningTime();

        Calendar learningCal = Calendar.getInstance();
        learningCal.setTime(remainderLearningDate);
        learningCal.setTimeZone(TimeZone.getTimeZone("UTC"));

        return learningCal.get(Calendar.HOUR_OF_DAY)*60 + learningCal.get(Calendar.MINUTE);
    }

    /**
     * 是否已學習逾時
     * @return 是否已學習逾時
     */
    public boolean isLearningTimeOver() {
        if(getRemainderLearningTime().getTime() <= 0) return true;
        else return false;
    }

    // =============================================================================================
    public void updateNextRecommandPoint(final int currentTId, final UElearningRestHandler handler) {

        try {
            String token = UserUtils.getToken(context);
            final DBProvider db = new DBProvider(context);
            db.removeAll_recommand();

            // 取得目前學習活動資料
            final int saId = getActivityId();
            boolean enableVirtual = isEnableVirtual();;

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
                            handler.onSuccess(statusCode, headers, responseBody);
                        }
                        // 已經結束的話
                        else {

                            // TODO: 改以隨時取得已學習標的數
                            setLearnedPointTotal(getPointTotal() - 1);
                            handler.onSuccess(statusCode, headers, responseBody);
                        }
                    } catch (UnsupportedEncodingException | JSONException e) {
                        handler.onOtherErr(e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    // 沒有此學習活動
                    if (statusCode == 404) {
                        handler.onNoStudyActivity();
                    } else if (statusCode == 401) {
                        handler.onNoLogin();
                    } else if (statusCode == 0) {
                        handler.onNoResponse();
                    } else {
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
        }
    }

    // =============================================================================================

    public int getPointTotal() {
        Cursor query = getActivityQuery();
        int result = 0;
        if(query.getCount()>0) {
            query.moveToFirst();
            result = query.getInt(query.getColumnIndex("TargetTotal"));
        }
        return result;
    }

    public int getLearnedPointTotal() {
        Cursor query = getActivityQuery();
        int result = 0;
        if(query.getCount()>0) {
            query.moveToFirst();
            result = query.getInt(query.getColumnIndex("LearnedTotal"));
        }
        return result;
    }

    public void setLearnedPointTotal(int total) {
        Cursor query = getActivityQuery();
        DBProvider db = new DBProvider(context);
        db.set_activity_learnedPointTotal(total);
    }

    public int getRemainingPointTotal() {
        return getPointTotal()-1 - getLearnedPointTotal();
    }
}
