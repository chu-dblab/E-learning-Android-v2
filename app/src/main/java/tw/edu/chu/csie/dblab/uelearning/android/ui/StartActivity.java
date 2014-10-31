package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent to_login = new Intent(StartActivity.this, LoginActivity.class);
        to_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(to_login);
    }
}
