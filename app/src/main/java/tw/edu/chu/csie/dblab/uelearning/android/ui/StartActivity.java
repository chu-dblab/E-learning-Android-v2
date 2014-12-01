package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;


public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 取得資料庫中的登入資訊
        DBProvider db = new DBProvider(StartActivity.this);
        Cursor mDb_user = db.get_user();

        // 如果尚未登入
        if(mDb_user.getCount() == 0) {
            Intent to_login = new Intent(StartActivity.this, LoginActivity.class);
            to_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(to_login);
        }
        // 如果是已登入
        else {
            Intent to_main = new Intent(StartActivity.this, MainActivity.class);
            to_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(to_main);
        }
    }
}
