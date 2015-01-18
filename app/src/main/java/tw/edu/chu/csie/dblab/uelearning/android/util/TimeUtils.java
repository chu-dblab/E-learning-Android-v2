package tw.edu.chu.csie.dblab.uelearning.android.util;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;

/**
 * Created by yuan on 2015/1/17.
 */
public class TimeUtils {

    public static Date stringToDate(String dateString) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultDate = new Date();
        resultDate = format.parse(dateString);
        return resultDate;
    }

    public static String timerToString(Date date) {
        Calendar inTimeCal = Calendar.getInstance();
        inTimeCal.setTime(date);
        inTimeCal.setTimeZone(TimeZone.getTimeZone("UTC"));

        String hourString = String.format( "%02d", inTimeCal.get(Calendar.HOUR_OF_DAY));
        String minuteString = String.format( "%02d", inTimeCal.get(Calendar.MINUTE));
        String secondString = String.format( "%02d", inTimeCal.get(Calendar.SECOND));

        String inTimeString = hourString + ":" + minuteString + ":" + secondString;
        return inTimeString;
    }

    public static String timeeToStringNoHour(Date date) {
        Calendar inTimeCal = Calendar.getInstance();
        inTimeCal.setTime(date);
        inTimeCal.setTimeZone(TimeZone.getTimeZone("UTC"));

        int hourToMinute = inTimeCal.get(Calendar.HOUR_OF_DAY) * 60;

        String minuteString = String.format( "%02d", hourToMinute + inTimeCal.get(Calendar.MINUTE));
        String secondString = String.format( "%02d", inTimeCal.get(Calendar.SECOND));

        String inTimeString = minuteString + ":" + secondString;
        return inTimeString;
    }

    /**
     * 設定與伺服端的時間差
     *
     * 請填入: 時間差 = 伺服端時間 - 客戶端(本機)時間
     * @param context Android基底
     * @param adjust 時間差
     */
    public static void setTimeAdjust(Context context, long adjust) {
        DBProvider db = new DBProvider(context);
        db.set_serverInfo("TimeAdjust", String.valueOf(adjust));
    }

    /**
     * 設定與伺服端的時間差
     * @param context Android基底
     * @param serverTime Server端時間物件
     */
    public static void setTimeAdjustByNowServerTime(Context context, Date serverTime) {
        Date clientTime = getNowClientTime();
        long adjust = serverTime.getTime() - clientTime.getTime();
        setTimeAdjust(context, adjust);
    }

    /**
     * 取得與伺服端的時間差
     * @param context Android基底
     * @return 時間差，為 時間差 = 伺服端時間 - 客戶端(本機)時間
     */
    public static long getTimeAdjust(Context context) {
        DBProvider db = new DBProvider(context);
        String timeAdjustString = db.get_serverInfo("TimeAdjust");
        long timeAdjust = Long.valueOf(timeAdjustString);
        return timeAdjust;
    }

    /**
     * 取得伺服端現在時間
     * @param context Android基底
     * @return 伺服端時間物件
     */
    public static Date getNowServerTime(Context context) {
        Date clientTime = getNowClientTime();
        long timeAdjust = getTimeAdjust(context);

        // 回傳時間差
        Date date= new Date(clientTime.getTime()+timeAdjust);
        return date;
    }

    /**
     * 取得客戶端現在時間
     * @return 客戶端現在時間
     */
    public static Date getNowClientTime() {
        // 取得現在時間
        Date nowDate = new Date(System.currentTimeMillis());
        return nowDate;
    }
}
