package tw.edu.chu.csie.dblab.uelearning.android.learning;

import android.content.Context;
import android.database.Cursor;

import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;

/**
 * Created by yuan on 2015/1/16.
 */
public class TargetManager {

    /**
     * 是否已有推薦的學習點
     * @param context Android基底
     * @return 是否已有推薦的學習點
     */
    public static boolean isHaveRecommand(Context context) {
        DBProvider db = new DBProvider(context);
        Cursor query = db.getAll_recommand();

        if(query.getCount() > 1) return true;
        else return false;
    }

    /**
     * 此學習點是否為推薦的
     * @param context Android基底
     * @param tId 標的編號
     * @return 是否為推薦的學習點
     */
    public static boolean isInRecommand(Context context, int tId) {

        DBProvider db = new DBProvider(context);
        Cursor query = db.get_recommand_byTargetId(tId);

        if(query.getCount() > 1) return true;
        else return false;
    }

    public void enterPointByDialog() {

    }

}
