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

    public String get_token() {
        Cursor the_query = db.query("User", null, null, null, null, null, null);
        the_query.moveToNext();
        return the_query.getString(the_query.getColumnIndex("Token"));
    }

    public long remove_user() {
        db = dbHelper.getWritableDatabase();
        return db.delete("User", null, null);
    }
}
