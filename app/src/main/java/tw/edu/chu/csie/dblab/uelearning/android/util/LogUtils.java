package tw.edu.chu.csie.dblab.uelearning.android.util;

import android.content.Context;

import java.util.Date;

import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;

/**
 * Created by afu730917 on 2015/3/15.
 */
public class LogUtils {

    public void insert(Context context, Integer said, Integer tid, String actionGroup, String encode, Integer qid, String answer, String other) {
        DBProvider db = new DBProvider(context);
        Date nowDate = TimeUtils.getNowServerTime(context);
        String nowString = TimeUtils.dateToString(nowDate);
        String uid = db.get_user_id();
        String encypt_before_id = uid+said+tid+actionGroup+encode+qid+answer+other;
        String id = EncryptUtils.sha1(encypt_before_id);


        db.insert_log(id, uid, nowString, said, tid, actionGroup, encode, qid, answer, other);
    }

}
