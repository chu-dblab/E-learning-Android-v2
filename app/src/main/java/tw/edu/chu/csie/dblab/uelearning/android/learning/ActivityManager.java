package tw.edu.chu.csie.dblab.uelearning.android.learning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.TimeUtils;

/**
 * Created by yuan on 2015/1/16.
 */
public class ActivityManager {

    /**
     * 取得開始學習的時間物件
     * @return 已學習的Date物件
     */
    @SuppressLint("SimpleDateFormat")
    public static Date getStartDate(Context context) {
        // 取得開始學習時間
        DBProvider db = new DBProvider(context);
        Cursor query = db.get_activity();

        if(query.getColumnCount()>0) {

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
    public static Date getLearningTime(Context context) {
        // 取得現在時間
        Date nowDate = TimeUtils.getNowServerTime(context);

        // 取得開始學習時間
        DBProvider db = new DBProvider(context);
        Cursor query = db.get_activity();
        if(query.getColumnCount()>0) {

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
    public static Integer getLearningMinTime(Context context) {
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
    public static Date getlimitTime(Context context) {

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
    public static Integer getlimitMin(Context context) {
        // 取得開始學習時間
        DBProvider db = new DBProvider(context);
        Cursor query = db.get_activity();
        if(query.getColumnCount()>0) {
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
    public static Date getRemainderLearningTime(Context context) {
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
    public static int getRemainderLearningMinTime(Context context) {
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
    public static boolean isLearningOver(Context context) {
        if(getRemainderLearningTime(context).getTime() <= 0) return true;
        else return false;
    }
}
