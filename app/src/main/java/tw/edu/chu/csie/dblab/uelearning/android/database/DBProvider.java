package tw.edu.chu.csie.dblab.uelearning.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBProvider {

    protected Context context;
    private DBHelper dbHelper;
    protected SQLiteDatabase db;

    public DBProvider(Context context) {
        this.context = context;
        dbHelper = new DBHelper(this.context, null);
        db = dbHelper.getWritableDatabase();
    }

    // ============================================================================================


    public long insert_user(String token, String uId, String loginDate,
                            String gId, String gName, Integer cId, String cName,
                            Integer lMode, String mMode, Boolean enableNoAppoint,
                            String nickName, String realName, String email) {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("Token", token);
        contentvalues.put("UID", uId);
        contentvalues.put("LoginDate", loginDate);
        contentvalues.put("GID", gId);
        contentvalues.put("GName", gName);
        contentvalues.put("CID", cId);
        contentvalues.put("CName", cName);
        contentvalues.put("LMode", lMode);
        contentvalues.put("MMode", mMode);
        contentvalues.put("Enable_NoAppoint", enableNoAppoint);
        contentvalues.put("NickName", nickName);
        contentvalues.put("RealName", realName);
        contentvalues.put("Email", email);
        return db.insert("User", null, contentvalues);
    }

    public Cursor get_user() {
        return db.query("User", null, null, null, null, null, null);
    }

    public String get_user_id() {
        Cursor the_query = db.query("User", null, null, null, null, null, null);
        the_query.moveToNext();
        return the_query.getString(the_query.getColumnIndex("UID"));
    }

    public String get_token() {
        Cursor the_query = db.query("User", null, null, null, null, null, null);
        the_query.moveToNext();
        return the_query.getString(the_query.getColumnIndex("Token"));
    }

    public long remove_user() {
        db = dbHelper.getWritableDatabase();
        return db.delete("User", null, null);
    }

    // --------------------------------------------------------------------------------------------

    public static final int TYPE_STUDY = 1;
    public static final int TYPE_WILL = 2;
    public static final int TYPE_THEME = 3;

    public long insert_enableActivity(String uId, int type, Integer saId, Integer swId,
                                      int thId, String thName, String thIntroduction,
                                      String startTime, String expiredTime,
                                      int learnTime, Boolean timeForce,
                                      Integer lMode, Boolean lForce, String mMode,
                                      Boolean lock, Integer targetTotal, Integer learnedTotal) {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("UID", uId);
        contentvalues.put("Type", type);
        contentvalues.put("SaID", saId);
        contentvalues.put("SwID", swId);
        contentvalues.put("ThID", thId);
        contentvalues.put("ThName", thName);
        contentvalues.put("ThIntroduction", thIntroduction);
        contentvalues.put("StartTime", startTime);
        contentvalues.put("ExpiredTime", expiredTime);
        contentvalues.put("LearnTime", learnTime);
        contentvalues.put("TimeForce", timeForce);
        contentvalues.put("LMode", lMode);
        contentvalues.put("LForce", lForce);
        contentvalues.put("MMode", mMode);
        contentvalues.put("Lock", lock);
        contentvalues.put("TargetTotal", targetTotal);
        contentvalues.put("LearnedTotal", learnedTotal);
        return db.insert("EnableActivity", null, contentvalues);
    }

    public Cursor get_enableActivity(int serial) {
        return db.query("EnableActivity", null, "Serial="+serial, null, null, null, null);
    }

    public Cursor getAll_enableActivity() {
        return db.query("EnableActivity", null, null, null, null, null, null);
    }

    public long removeAll_enableActivity() {
        db = dbHelper.getWritableDatabase();
        return db.delete("EnableActivity", null, null);
    }

    public long insert_activity(String uId, int type, Integer saId, Integer swId,
                                      int thId, String thName, String thIntroduction,
                                      String startTime, String expiredTime,
                                      int learnTime, Boolean timeForce,
                                      Integer lMode, Boolean lForce, String mMode,
                                      Boolean lock, Integer targetTotal, Integer learnedTotal) {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("UID", uId);
        contentvalues.put("Type", type);
        contentvalues.put("SaID", saId);
        contentvalues.put("SwID", swId);
        contentvalues.put("ThID", thId);
        contentvalues.put("ThName", thName);
        contentvalues.put("ThIntroduction", thIntroduction);
        contentvalues.put("StartTime", startTime);
        contentvalues.put("ExpiredTime", expiredTime);
        contentvalues.put("LearnTime", learnTime);
        contentvalues.put("TimeForce", timeForce);
        contentvalues.put("LMode", lMode);
        contentvalues.put("LForce", lForce);
        contentvalues.put("MMode", mMode);
        contentvalues.put("Lock", lock);
        contentvalues.put("TargetTotal", targetTotal);
        contentvalues.put("LearnedTotal", learnedTotal);
        return db.insert("EnableActivity", null, contentvalues);
    }

}
