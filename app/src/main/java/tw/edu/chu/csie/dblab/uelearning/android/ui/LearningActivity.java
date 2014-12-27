package tw.edu.chu.csie.dblab.uelearning.android.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.server.UElearningRestClient;
import tw.edu.chu.csie.dblab.uelearning.android.ui.fragment.BrowseMaterialFragment;
import tw.edu.chu.csie.dblab.uelearning.android.ui.fragment.PlaceInfoFragment;
import tw.edu.chu.csie.dblab.uelearning.android.ui.fragment.PlaceMapFragment;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.HelpUtils;

public class LearningActivity extends ActionBarActivity implements ActionBar.TabListener {

    ProgressDialog mProgress_activity_finish;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        // 結束中畫面
        mProgress_activity_finish = new ProgressDialog(LearningActivity.this);
        mProgress_activity_finish.setMessage(getResources().getString(R.string.finishing_study_activity));
        mProgress_activity_finish.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress_activity_finish.setIndeterminate(true);
        // TODO: 設計成可中途取消的功能
        mProgress_activity_finish.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_learning, menu);
        if(Config.DEBUG_ACTIVITY) {
            menu.findItem(R.id.menu_inside_tester).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // QR Code 掃描
        if (id == R.id.menu_qr_scan) {

        }
        // 輸入標的編號
        else if (id == R.id.menu_keyin_tid) {

            final AlertDialog.Builder mDialog_inputTId = new AlertDialog.Builder(LearningActivity.this);
            mDialog_inputTId.setTitle(R.string.keyin_tid_message);

            final EditText mEdit_inputTId = new EditText(LearningActivity.this);
            mEdit_inputTId.setInputType(InputType.TYPE_CLASS_NUMBER);
            // 設定最大長度
            mEdit_inputTId.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});

            mDialog_inputTId.setView(mEdit_inputTId);
            mDialog_inputTId.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // 隱藏鍵盤（實際上是切換鍵盤是否顯示）
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    // 取得輸入的標的編號
                    String tId_string = mEdit_inputTId.getText().toString();

                    // 判斷是否有輸入數字
                    if(!tId_string.equals("")) {
                        // 取得剛剛輸入的編號
                        int tId = Integer.valueOf(tId_string);

                        // 進入教材頁面
                        Intent toMaterial = new Intent(LearningActivity.this, MaterialActivity.class);
                        toMaterial.putExtra("tId", tId);
                        startActivityForResult(toMaterial, 1);
                    }

                }
            });
            mDialog_inputTId.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 隱藏鍵盤（實際上是切換鍵盤是否顯示）
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            });
            mDialog_inputTId.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // 隱藏鍵盤（實際上是切換鍵盤是否顯示）
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            });
            mDialog_inputTId.show();

            // 馬上設定輸入點與顯示鍵盤
            mEdit_inputTId.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        }
        // 結束學習活動
        else if (id == R.id.menu_finish_study_activity) {
            finishStudyActivity();
        }
        // 暫停學習活動
        else if (id == R.id.menu_pause_study_activity) {
            // 返回學習活動選擇頁面
            finish();
        }
        else if (id == R.id.menu_about) {
            HelpUtils.showAboutDialog(LearningActivity.this);
            return true;
        }
        else if(id == R.id.menu_inside_tester) {
            Intent toTester = new Intent(LearningActivity.this, TesterActivity.class);
            startActivity(toTester);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * 結束學習活動
     */
    public void finishStudyActivity() {

        mProgress_activity_finish.show();

        DBProvider db = new DBProvider(LearningActivity.this);
        // 取得目前使用者的登入階段Token
        String token = db.get_token();

        // 取得目前正在學習的活動編號
        int saId = db.get_activity_id();

        // 對伺服器通知學習活動已結束
        RequestParams finish_params = new RequestParams();
        try {
            UElearningRestClient.post("/tokens/" + URLEncoder.encode(token, HTTP.UTF_8) +
                    "/activitys/" + saId + "/finish",
                    finish_params, new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            mProgress_activity_finish.show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            mProgress_activity_finish.dismiss();

                            String content = null;
                            try {
                                content = new String(responseBody, "UTF-8");
                                JSONObject response = new JSONObject(content);
                                JSONObject activityJson = response.getJSONObject("activity");

                                // 紀錄進資料庫
                                DBProvider db = new DBProvider(LearningActivity.this);
                                int saId = db.get_activity_id();
                                db.removeAll_activity();
                                db.remove_enableActivity_inStudying_bySaId(saId);

                                // 離開學習畫面
                                LearningActivity.this.finish();

                            } catch (UnsupportedEncodingException e) {
                                ErrorUtils.error(LearningActivity.this, e);
                            } catch (JSONException e) {
                                ErrorUtils.error(LearningActivity.this, e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            mProgress_activity_finish.dismiss();

                            // 此學習活動早已結束
                            if(statusCode == 405) {

                                // 紀錄進資料庫
                                DBProvider db = new DBProvider(LearningActivity.this);
                                int saId = db.get_activity_id();
                                db.removeAll_activity();
                                db.remove_enableActivity_inStudying_bySaId(saId);

                                // 離開學習畫面
                                LearningActivity.this.finish();
                            }
                            // 其他錯誤
                            else {
                                try {
                                    // TODO: 取得可用的學習活動失敗的錯誤處理
                                    String content = new String(responseBody, HTTP.UTF_8);
                                    if (Config.DEBUG_SHOW_MESSAGE) {
                                        Toast.makeText(LearningActivity.this,
                                                "s: " + statusCode + "\n" + content,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(LearningActivity.this, R.string.inside_error, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    ErrorUtils.error(LearningActivity.this, e);
                                }
                            }

                        }
                    });
        } catch (UnsupportedEncodingException e) {
            ErrorUtils.error(LearningActivity.this, e);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment f;
            switch (position) {
                case 0:
                    f = StudyGuideFragment.newInstance(position);
                    break;
                case 1:
                    f = PlaceMapFragment.newInstance(position);
                    break;
                case 2:
                    f = PlaceInfoFragment.newInstance(position);
                    break;
                case 3:
                    f = BrowseMaterialFragment.newInstance(position);
                    break;
                default:
                    f = null;
            }
            return f;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.study_guide).toUpperCase(l);
                case 1:
                    return getString(R.string.place_map).toUpperCase(l);
                case 2:
                    return getString(R.string.place_info).toUpperCase(l);
                case 3:
                    return getString(R.string.browse_material).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * 學習引導畫面（顯示推薦學習點的地方）
     */
    public static class StudyGuideFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

        private ListView mList_nextPoints;
        private SwipeRefreshLayout mSwipe_nextPoints;
        private TextView mText_remainedTime;
        private ImageView mImage_map;

        public static StudyGuideFragment newInstance(int sectionNumber) {
            StudyGuideFragment fragment = new StudyGuideFragment();
            return fragment;
        }

        public StudyGuideFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_study_guide, container, false);
            initUI(rootView);

            return rootView;
        }

        protected void initUI(View rootView) {
            mList_nextPoints = (ListView) rootView.findViewById(R.id.list_learning_next_points);
            mList_nextPoints.setOnItemClickListener(this);
            mSwipe_nextPoints = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_next_points);
            mSwipe_nextPoints.setOnRefreshListener(this);
            mText_remainedTime = (TextView) rootView.findViewById(R.id.text_learning_remaining_time);
            mImage_map = (ImageView) rootView.findViewById(R.id.image_learning_next_points);
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);

            // 裝置螢幕旋轉時，重新載入界面
            // Get a layout inflater (inflater from getActivity() or getSupportActivity() works as well)
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View newView = inflater.inflate(R.layout.fragment_study_guide, null);
            // This just inflates the view but doesn't add it to any thing.
            // You need to add it to the root view of the fragment
            ViewGroup rootView = (ViewGroup) getView();
            // Remove all the existing views from the root view.
            // This is also a good place to recycle any resources you won't need anymore
            rootView.removeAllViews();
            rootView.addView(newView);

            initUI(rootView);
        }

        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p/>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onRefresh() {
            mSwipe_nextPoints.setRefreshing(false);
        }
    }
}
