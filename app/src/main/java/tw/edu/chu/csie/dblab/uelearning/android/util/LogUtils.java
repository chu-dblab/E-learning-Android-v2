package tw.edu.chu.csie.dblab.uelearning.android.util;

import android.content.Context;

import java.util.Date;

import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;

/**
 * Created by afu730917 on 2015/3/15.
 */
public class LogUtils {

    /**
     * 插入一筆Log資料
     *
     * @param context Android基底
     * @param said 活動編號
     * @param tid 標的編號
     * @param actionGroup 動作類型
     * @param encode 動作名稱
     * @param qid 問題編號
     * @param answer 答案編號
     * @param other 其他
     */
    public static void insert(Context context, Integer said, Integer tid, String actionGroup, String encode, Integer qid, String answer, String other) {
        DBProvider db = new DBProvider(context);
        Date nowDate = TimeUtils.getNowServerTime(context);
        String nowString = TimeUtils.dateToString(nowDate);
        String uid = db.get_user_id();
        String encypt_before_id = uid+said+tid+actionGroup+encode+qid+answer+other;
        String id = EncryptUtils.sha1(encypt_before_id);


        db.insert_log(id, uid, nowString, said, tid, actionGroup, encode, qid, answer, other);
    }

    /**
     * 清除所有的Log資料
     * @param context Android基底
     */
    public static void removeAll(Context context) {
        DBProvider db = new DBProvider(context);
        db.removeAll_log();
    }

    public static class Insert {
        /**
         * 新增回答紀錄
         *
         * @param context Android基底
         * @param said 活動編號
         * @param tid 標的編號
         * @param qid 問題編號
         * @param answer 答案編號
         */
        public static void answer(Context context, int said, int tid, int qid, String answer) {
            insert(context, said, tid, "Material", "Answer", qid, answer, null);
        }


    }

}
