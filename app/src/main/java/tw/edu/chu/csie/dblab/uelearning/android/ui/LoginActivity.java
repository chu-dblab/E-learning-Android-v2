package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.util.HelpUtils;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    ImageButton mBtn_menu_overflow;
    PopupMenu mPopup_menu_overflow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 介面對應
        mBtn_menu_overflow = (ImageButton) findViewById(R.id.btn_login_menu_overflow);
        mBtn_menu_overflow.setOnClickListener(this);
        /** Instantiating PopupMenu class */
        mPopup_menu_overflow = new PopupMenu(this, mBtn_menu_overflow);

        /** Adding menu items to the popumenu */
        mPopup_menu_overflow.getMenuInflater().inflate(R.menu.login, mPopup_menu_overflow.getMenu());
        // DEBUG 開啟教材內容測試
        if(Config.DEBUG_ACTIVITY) {
            mPopup_menu_overflow.getMenu().findItem(R.id.menu_inside_tester).setVisible(true);
        }

        /** Defining menu item click listener for the popup menu */
        mPopup_menu_overflow.setOnMenuItemClickListener(this);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login_menu_overflow) {
            /** Showing the popup menu */
            mPopup_menu_overflow.show();
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        mPopup_menu_overflow.show();
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * This method will be invoked when a menu item is clicked if the item itself did
     * not already handle the event.
     *
     * @param item {@link android.view.MenuItem} that was clicked
     * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_about) {
            HelpUtils.showAboutDialog(LoginActivity.this);
            return true;
        }
        else if(id == R.id.menu_inside_tester) {
            Intent toTester = new Intent(LoginActivity.this, TesterActivity.class);
            startActivity(toTester);
        }

        return super.onOptionsItemSelected(item);
    }
}
