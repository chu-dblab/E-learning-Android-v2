package tw.edu.chu.csie.dblab.uelearning.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import tw.edu.chu.csie.dblab.uelearning.android.config.Config;

public class DBHelper extends SQLiteOpenHelper {

    protected static String fileName = Config.CDB_NAME;
    protected static int    version  = 10;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param factory to use for creating cursor objects, or null for the default
     */
    public DBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, fileName, factory, version);
    }

    /**
     * 當建立資料庫的時候會做的事，大概就是創立裡面的表格
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Table
        String sql_create_user = "CREATE TABLE User (\n" +
                "Token Varchar(50)  NOT NULL,\n" +
                "UID Varchar(30)  NOT NULL  PRIMARY KEY,\n" +
                "LoginDate Timestamp DEFAULT NULL,\n" +
                "GID Varchar(30)  NOT NULL,\n" +
                "GName Varchar(100) DEFAULT NULL,\n" +
                "CID integer DEFAULT NULL,\n" +
                "CName Varchar(100) DEFAULT NULL,\n" +
                "LMode integer DEFAULT NULL,\n" +
                "MMode Varchar(25) DEFAULT NULL,\n" +
                "Enable_NoAppoint Boolean DEFAULT NULL,\n" +
                "NickName Varchar(50) DEFAULT NULL,\n" +
                "RealName Varchar(50) DEFAULT NULL,\n" +
                "Email Varchar(100) DEFAULT NULL)";

        String sql_create_auth = "CREATE TABLE Auth (" +
                "UID Varchar(30)  PRIMARY KEY DEFAULT NULL," +
                "ClientAdmin Boolean DEFAULT NULL)";

        String sql_create_activity = "CREATE TABLE Activity (" +
                "SaID integer  PRIMARY KEY DEFAULT NULL," +
                "UID Varchar(30)," +
                "ThID integer  NOT NULL DEFAULT 0," +
                "ThName Varchar(100)," +
                "StartTID integer," +
                "StartTime Timestamp DEFAULT NULL," +
                "LearnTime integer  NOT NULL DEFAULT 0," +
                "TimeForce Boolean  NOT NULL DEFAULT 0," +
                "LMode integer," +
                "LForce Boolean DEFAULT NULL," +
                "EnableVirtual Boolean DEFAULT 0," +
                "MMode Varchar(25) DEFAULT NULL," +
                "TargetTotal integer," +
                "LearnedTotal integer DEFAULT 0)";

        String sql_create_enableActivity = "CREATE TABLE EnableActivity (" +
                "Serial integer PRIMARY KEY AUTOINCREMENT," +
                "UID Varchar(30)," +
                "Type Smallint DEFAULT 0," +
                "SaID integer," +
                "SwID integer," +
                "ThID integer  NOT NULL DEFAULT 0," +
                "ThName Varchar(100)," +
                "ThIntroduction TEXT," +
                "StartTime Timestamp DEFAULT NULL," +
                "ExpiredTime Timestamp  DEFAULT NULL," +
                "LearnTime integer  NOT NULL DEFAULT 0," +
                "TimeForce Boolean  NOT NULL DEFAULT 0," +
                "LMode integer," +
                "LForce Boolean DEFAULT NULL," +
                "EnableVirtual Boolean DEFAULT 0," +
                "MMode Varchar(25) DEFAULT NULL," +
                "Lock Boolean  DEFAULT 0," +
                "TargetTotal integer," +
                "LearnedTotal integer DEFAULT 0)";

        String sql_create_material_kind = "CREATE TABLE MaterialKind (" +
                "MkID Varchar(25) PRIMARY KEY," +
                "MkName Varchar(100)" +
                ")";

        String sql_create_target = "CREATE TABLE Target (" +
                "ThID integer," +
                "TID integer PRIMARY KEY," +
                "HID integer," +
                "HName Varchar(100)," +
                "AID integer," +
                "AName Varchar(100)," +
                "AFloor integer," +
                "ANum integer," +
                "TNum integer," +
                "TName Varchar(100)," +
                "LearnTime integer," +
                "MapUrl Varchar(1000)," +
                "MaterialUrl Varchar(1000)," +
                "VirtualMaterialUrl Varchar(1000)" +
                ")";

        String sql_create_recommand = "CREATE TABLE Recommand (" +
                "Serial integer PRIMARY KEY AUTOINCREMENT," +
                "TID integer," +
                "IsEntity Boolean NOT NULL DEFAULT 0" +
                ")";

        String sql_create_log = "CREATE TABLE \"Log\" (" +
                "LID integer  PRIMARY KEY AUTOINCREMENT DEFAULT NULL," +
                "UID Varchar(30)," +
                "Date Timestamp  NOT NULL  DEFAULT CURRENT_TIMESTAMP," +
                "Encode Varchar(3)," +
                "Data Varchar(100))";

        // Create Table
        String sql_create_server_info = "CREATE TABLE ServerInfo (\n" +
                "Name Varchar(200) NOT NULL PRIMARY KEY,\n" +
                "Value Varchar(200))";

        String sql_insert_server_info_1 =
                        "INSERT INTO `ServerInfo`(`Name`,`Value`) VALUES ('SiteName',NULL)";
        String sql_insert_server_info_2 =
                        "INSERT INTO `ServerInfo`(`Name`,`Value`) VALUES ('SiteSubname',NULL)";
        String sql_insert_server_info_3 =
                        "INSERT INTO `ServerInfo`(`Name`,`Value`) VALUES ('SiteReferred',NULL)";
        String sql_insert_server_info_4 =
                        "INSERT INTO `ServerInfo`(`Name`,`Value`) VALUES ('TimeAdjust',0);";

        //Create Table
        String sql_create_place_info = "CREATE TABLE Placeinfo (" +
                "IID integer PRIMARY KEY," +
                "IName Varchar(1000)," +
                "IContent Varchar(1000)" +
                ")";

        String sql_create_place_map = "CREATE TABLE Placemap (" +
                "PID integer PRIMARY KEY," +
                "PName Varchar(1000)," +
                "PUrl Varchar(1000)" +
                ")";

        db.execSQL(sql_create_user);
        db.execSQL(sql_create_auth);
        db.execSQL(sql_create_activity);
        db.execSQL(sql_create_enableActivity);
        db.execSQL(sql_create_material_kind);
        db.execSQL(sql_create_target);
        db.execSQL(sql_create_recommand);
        db.execSQL(sql_create_log);
        db.execSQL(sql_create_server_info);
        db.execSQL(sql_insert_server_info_1);
        db.execSQL(sql_insert_server_info_2);
        db.execSQL(sql_insert_server_info_3);
        db.execSQL(sql_insert_server_info_4);
        db.execSQL(sql_create_place_info);
        db.execSQL(sql_create_place_map);

        Log.d("success", "SQLite: User, Auth, Activity, EnableActivity, Log 建表成功!!");
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 第二代第一版不需撰寫
    }
}
