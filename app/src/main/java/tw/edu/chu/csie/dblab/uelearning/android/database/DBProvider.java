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

    public void removeAllUserData() {
        DBProvider db = this;

        db.remove_user();
        db.removeAll_enableActivity();
        db.removeAll_activity();
        db.removeAll_recommand();
        db.removeAll_materialKind();
        db.removeAll_target();
    }

    public void finishStudy() {
        DBProvider db = this;
        int saId = db.get_activity_id();
        db.remove_enableActivity_inStudying_bySaId(saId);
        db.removeAll_target();
        db.removeAll_recommand();
        db.removeAll_activity();
    }

    public void forceFinishStudy() {
        DBProvider db = this;
        db.removeAll_target();
        db.removeAll_recommand();
        db.removeAll_activity();
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
        long returnData = db.insert("User", null, contentvalues);

        return returnData;
    }

    public Cursor get_user() {

        Cursor returnData = db.query("User", null, null, null, null, null, null);
        return returnData;
    }

    public String get_user_id() {

        Cursor the_query = db.query("User", null, null, null, null, null, null);
        the_query.moveToNext();
        return the_query.getString(the_query.getColumnIndex("UID"));
    }

    public String get_token() {

        Cursor the_query = db.query("User", null, null, null, null, null, null);
        the_query.moveToNext();
        String returnData = the_query.getString(the_query.getColumnIndex("Token"));
        return returnData;
    }

    public long remove_user() {

        long returnData = db.delete("User", null, null);
        return returnData;
    }

    // --------------------------------------------------------------------------------------------

    // ============================================================================================
    public long insert_place_info(Integer IID, String IName,String IContent) {

        ContentValues contentvalues = new ContentValues();
        contentvalues.put("IID", IID);
        contentvalues.put("IName", IName);
        contentvalues.put("IContent", IContent);

        long returnData = db.insert("Placeinfo", null, contentvalues);

        return returnData;
    }
    public long remove_place_info() {

        long returnData = db.delete("Placeinfo", null, null);
        return returnData;
    }
    public long insert_place_map(Integer PID, String PName,String PUrl) {

        ContentValues contentvalues = new ContentValues();
        contentvalues.put("PID", PID);
        contentvalues.put("PName", PName);
        contentvalues.put("PUrl", PUrl);

        long returnData = db.insert("Placemap", null, contentvalues);

        return returnData;
    }
    public long remove_place_map() {

        long returnData = db.delete("Placemap", null, null);
        return returnData;
    }
    // --------------------------------------------------------------------------------------------

    // ============================================================================================
    public long insert_log(String LID, String UID,String Date,Integer SaID,Integer TID,String ActionGroup,String Encode,Integer QID,String Answer,String Other) {

        ContentValues contentvalues = new ContentValues();
        contentvalues.put("LID", LID);
        contentvalues.put("UID", UID);
        contentvalues.put("Date", Date);
        contentvalues.put("SaID", SaID);
        contentvalues.put("TID", TID);
        contentvalues.put("ActionGroup", ActionGroup);
        contentvalues.put("Encode", Encode);
        contentvalues.put("QID", QID);
        contentvalues.put("Aswer", Answer);
        contentvalues.put("Other", Other);

        long returnData = db.insert("Log", null, contentvalues);

        return returnData;
    }
    public long removeAll_log() {

        long returnData = db.delete("Log", null, null);
        return returnData;
    }

    public Cursor getAll_log() {

        Cursor returnData =
                db.query("Log", null, null, null, null, null, null);
        return returnData;
    }
    // --------------------------------------------------------------------------------------------
    public static final int TYPE_STUDY = 1;
    public static final int TYPE_WILL = 2;
    public static final int TYPE_THEME = 3;

    public long insert_enableActivity(String uId, int type, Integer saId, Integer swId,
                                      int thId, String thName, String thIntroduction,
                                      String startTime, String expiredTime,
                                      int learnTime, Boolean timeForce,
                                      Integer lMode, Boolean lForce, Boolean enableVirtual,
                                      String mMode,
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
        contentvalues.put("EnableVirtual", enableVirtual);
        contentvalues.put("MMode", mMode);
        contentvalues.put("Lock", lock);
        contentvalues.put("TargetTotal", targetTotal);
        contentvalues.put("LearnedTotal", learnedTotal);

        long returnData = db.insert("EnableActivity", null, contentvalues);
        return returnData;
    }

    public Cursor get_enableActivity(int serial) {

        Cursor returnData =
                db.query("EnableActivity", null, "Serial="+serial, null, null, null, null);
        return returnData;
    }

    public Cursor getAll_enableActivity() {

        Cursor returnData =
                db.query("EnableActivity", null, null, null, null, null, "Type ASC");
        return returnData;
    }

    public long remove_enableActivity_inStudying_bySaId(int saId) {

        long returnData =
                db.delete("EnableActivity", "SaID="+saId+" AND Type="+this.TYPE_STUDY, null);
        return returnData;
    }

    public long removeAll_enableActivity() {

        long returnData =
                db.delete("EnableActivity", null, null);
        return returnData;
    }

    public Cursor get_activity() {

        Cursor returnData =
                db.query("Activity", null, null, null, null, null, null);
        return returnData;

    }

    public long insert_activity(String uId, int saId,
                                int thId, String thName, int startTId,
                                String startTime, int learnTime, boolean timeForce,
                                int lMode, boolean lForce, boolean enableVirtual, String mMode,
                                int targetTotal, int learnedTotal) {

        ContentValues contentvalues = new ContentValues();
        contentvalues.put("UID", uId);
        contentvalues.put("SaID", saId);
        contentvalues.put("ThID", thId);
        contentvalues.put("ThName", thName);
        contentvalues.put("StartTID", startTId);
        contentvalues.put("StartTime", startTime);
        contentvalues.put("LearnTime", learnTime);
        contentvalues.put("TimeForce", timeForce);
        contentvalues.put("LMode", lMode);
        contentvalues.put("LForce", lForce);
        contentvalues.put("EnableVirtual", enableVirtual);
        contentvalues.put("MMode", mMode);
        contentvalues.put("TargetTotal", targetTotal);
        contentvalues.put("LearnedTotal", learnedTotal);
        long returnData = db.insert("Activity", null, contentvalues);
        return returnData;
    }

    public Integer get_activity_id() {

        Cursor the_query = db.query("Activity", null, null, null, null, null, null);
        the_query.moveToNext();
        Integer returnData =
                Integer.valueOf( the_query.getString(the_query.getColumnIndex("SaID")) );
        return returnData;
    }

    public long removeAll_activity() {

        long returnData = db.delete("Activity", null, null);
        return returnData;
    }

    public long set_activity_learnTime(int time) {
        ContentValues values = new ContentValues();
        values.put("LearnTime", time);
        long returnData = db.update("Activity", values, null, null);

        return returnData;
    }

    public long set_activity_learnedPointTotal(int total) {
        ContentValues values = new ContentValues();
        values.put("LearnedTotal", total);
        long returnData = db.update("Activity", values, null, null);

        return returnData;
    }

    public long insert_materialKind(String mkId, String mkName) {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("MkID", mkId);
        contentvalues.put("MkName", mkName);
        long returnData = db.insert("MaterialKind", null, contentvalues);

        return returnData;
    }

    public Cursor getAll_materialKind() {
        Cursor the_query = db.query("MaterialKind", null, null, null, null, null, null);
        return the_query;
    }

    public long removeAll_materialKind() {
        long returnData = db.delete("MaterialKind", null, null);
        return returnData;
    }

    public long insert_target(int thId, int tId, Integer hId, String hName,
                              Integer aId, String aName, Integer aFloor, Integer aNum,
                              Integer tNum, String tName, int learnTime,
                              String mapUrl, String materialUrl, String virtualMaterialUrl) {

        ContentValues contentvalues = new ContentValues();
        contentvalues.put("ThID", thId);
        contentvalues.put("TID", tId);
        contentvalues.put("HID", hId);
        contentvalues.put("HName", hName);
        contentvalues.put("AID", aId);
        contentvalues.put("AName", aName);
        contentvalues.put("AFloor", aFloor);
        contentvalues.put("ANum", aNum);
        contentvalues.put("TNum", tNum);
        contentvalues.put("TName", tName);
        contentvalues.put("LearnTime", learnTime);
        contentvalues.put("MapUrl", mapUrl);
        contentvalues.put("MaterialUrl", materialUrl);
        contentvalues.put("VirtualMaterialUrl", virtualMaterialUrl);
        long returnData = db.insert("Target", null, contentvalues);

        return returnData;
    }

    public Cursor get_target(int tId) {

        Cursor the_query = db.query("Target", null, "TID="+tId, null, null, null, null);

        return the_query;
    }

    public long removeAll_target() {

        long returnData = db.delete("Target", null, null);
        return returnData;
    }

    public long insert_recommand(int tId, boolean isEntity) {

        ContentValues contentvalues = new ContentValues();
        contentvalues.put("TID", tId);
        contentvalues.put("IsEntity", isEntity);
        long returnData = db.insert("Recommand", null, contentvalues);

        return returnData;
    }

    public Cursor getAll_recommand() {

        Cursor the_query = db.query("Recommand", null, null, null, null, null, null);
        return the_query;
    }

    public Cursor get_recommand_byTargetId(int tId) {

        Cursor the_query = db.query("Recommand", null, "TID="+tId, null, null, null, null);
        return the_query;
    }

    public long removeAll_recommand() {

        long returnData = db.delete("Recommand", null, null);
        return returnData;
    }

    public long insert_answer(Integer tID,String qDate,String aDate,Integer qID,String ans,Boolean correct) {

        ContentValues contentvalues = new ContentValues();
        contentvalues.put("TID", tID);
        contentvalues.put("QDate", qDate);
        contentvalues.put("ADate", aDate);
        contentvalues.put("QID", qID);
        contentvalues.put("Ans", ans);
        contentvalues.put("Correct", correct);

        long returnData = db.insert("Question", null, contentvalues);

        return returnData;
    }

    public long removeAll_answer() {

        long returnData = db.delete("Question", null, null);
        return returnData;
    }

    public Cursor getAll_answer() {

        Cursor returnData =
                db.query("Question", null, null, null, null, null, null);
        return returnData;
    }

    public String get_serverInfo(String name) {

        Cursor the_query = db.query("ServerInfo", new String[]{"Value"}, "Name='"+name+"'", null, null, null, null);
        the_query.moveToFirst();
        String returnData = the_query.getString(0);
        return returnData;
    }

    public long set_serverInfo(String name, String value) {

        ContentValues values = new ContentValues();
        values.put("Value", value);
        long returnData = db.update("ServerInfo", values, "Name = '"+name+"'", null);

        return returnData;
    }



}
