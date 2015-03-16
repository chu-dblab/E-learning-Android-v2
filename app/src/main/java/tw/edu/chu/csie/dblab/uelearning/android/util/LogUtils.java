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
         * 開始向伺服端取得推薦學習點
         *
         * @param context Android基底
         * @param said 活動編號
         */
        public static void toRecommand(Context context, int said) {
            insert(context, said, null, "Learning", "ToRecommand", null, null, null);
        }

        /**
         * 取得到的推薦學習點
         *
         * @param context Android基底
         * @param said 活動編號
         * @param tid 標的
         */
        public static void recommandResult(Context context, int said, int[] tid) {

            String tids_string = "";
            for(int i=0; i<tid.length; i++) {
                tids_string = tids_string+tid[i]+", ";
            }
            insert(context, said, null, "Learning", "Recommand", null, null, tids_string);
        }

        /**
         * 以QRCode掃描方式進入標的
         *
         * @param context Android基底
         * @param said 活動編號
         * @param tid 標的
         */
        public static void scan_withQr(Context context, int said, int tid) {

            insert(context, said, tid, "Scan", "WithQR", null, null, null);
        }

        /**
         * 以輸入方式進入標的
         *
         * @param context Android基底
         * @param said 活動編號
         * @param tid 標的
         */
        public static void scan_withKeyIn(Context context, int said, int tid) {

            insert(context, said, tid, "Scan", "WithKeyIn", null, null, null);
        }


        public static void toInTarget(Context context, int said, int tid) {
            insert(context, said, tid, "Learning", "ToInTarget", null, null, null);
        }

        public static void toOutTarget(Context context, int said, int tid) {
            insert(context, said, tid, "Learning", "ToOutTarget", null, null, null);
        }

        public static void material_pressFinishButton(Context context, int said, int tid) {
            insert(context, said, tid, "Material", "PressFinishButton", null, null, null);
        }

        /**
         * 新增回答紀錄
         *
         * @param context Android基底
         * @param said 活動編號
         * @param tid 標的編號
         * @param qid 問題編號
         * @param answer 答案編號
         * @param isCorrect 是否回答正確
         */
        public static void material_answer(Context context, int said, int tid, int qid, String answer, boolean isCorrect) {
            String correctString;
            if(isCorrect) { correctString = "Correct"; }
            else { correctString = "Error"; }
            insert(context, said, tid, "Material", "Answer", qid, answer, correctString);
        }

        public static void material_goBack(Context context, int said, int tid) {
            insert(context, said, tid, "Material", "GoBack", null, null, null);
        }

    }

}
