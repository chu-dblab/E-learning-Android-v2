package tw.edu.chu.csie.dblab.uelearning.android.ui;

import java.util.Locale;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.util.HelpUtils;

public class LearningActivity extends ActionBarActivity implements ActionBar.TabListener {

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_qr_scan) {

        }
        else if (id == R.id.menu_keyin_tid) {

        }
        else if (id == R.id.menu_end_study_activity) {

        }
        else if (id == R.id.menu_pause_study_activity) {
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
    public void onBackPressed() {

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
    public void endStudyActivity() {

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
    public static class StudyGuideFragment extends Fragment {


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
            return rootView;
        }
    }

    /**
     * 館區地圖
     */
    public static class PlaceMapFragment extends Fragment {


        public static PlaceMapFragment newInstance(int sectionNumber) {
            PlaceMapFragment fragment = new PlaceMapFragment();
            return fragment;
        }

        public PlaceMapFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_place_map, container, false);
            View rootView = new View(getActivity());
            return rootView;
        }
    }

    /**
     * 館區地圖
     */
    public static class PlaceInfoFragment extends Fragment {


        public static PlaceInfoFragment newInstance(int sectionNumber) {
            PlaceInfoFragment fragment = new PlaceInfoFragment();
            return fragment;
        }

        public PlaceInfoFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_place_map, container, false);
            View rootView = new View(getActivity());
            return rootView;
        }
    }

    /**
     * 館區地圖
     */
    public static class BrowseMaterialFragment extends Fragment {


        public static BrowseMaterialFragment newInstance(int sectionNumber) {
            BrowseMaterialFragment fragment = new BrowseMaterialFragment();
            return fragment;
        }

        public BrowseMaterialFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_place_map, container, false);
            View rootView = new View(getActivity());
            return rootView;
        }
    }
}
