package tw.edu.chu.csie.dblab.uelearning.android.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.ui.MainActivity;

/**
 * Created by yuan on 2015/3/11.
 */
public class StartWillStudyActivityLockDialog extends AlertDialog.Builder
        implements DialogInterface.OnClickListener{

    protected int swId;
    protected int thId;
    protected String thName;
    protected int thTime;
    protected int thPointTotal;
    protected String thIntroduction;
    protected String startTime;
    protected String expiredTime;
    protected int learnTime;
    protected boolean timeForce;
    protected int lMode;
    protected boolean lForce;
    protected boolean enableVirtual;
    protected String mMode;

    View rootView;
    Context context;
    private TextView mText_swId, mText_themeId, mText_estimated_time, mText_have_point_total,
                     mText_expiredTime;
    private LinearLayout mLayout_theme_introduction;
    private TextView mText_theme_introduction;
    private TextView mText_learnMode, mText_timeLimit, mText_materialMode;

    public StartWillStudyActivityLockDialog(final Context context, int position) {
        super(context);
        this.context = context;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_start_will_study_activity_lock, null);
        this.setTitle(R.string.title_start_study_activity_dialog);
        this.setView(rootView);
        this.setCancelable(true);
        this.setPositiveButton(R.string.start_study_activity, this);
        this.setNegativeButton(R.string.cancel, this);

        checkData(position);
        initUi();
        displayUi();
    }

    public void checkData(int position) {
        DBProvider db = new DBProvider(context);
        Cursor query = db.get_enableActivity(position);
        query.moveToFirst();

        swId = query.getInt( query.getColumnIndex("SwID") );
        thId = query.getInt( query.getColumnIndex("ThID") );
        thName = query.getString( query.getColumnIndex("ThName") );
        thIntroduction = query.getString( query.getColumnIndex("ThIntroduction") );
        thTime = query.getInt( query.getColumnIndex("LearnTime") );
        thPointTotal = query.getInt( query.getColumnIndex("TargetTotal") );
        startTime = query.getString( query.getColumnIndex("StartTime") );
        expiredTime = query.getString( query.getColumnIndex("ExpiredTime") );
        learnTime = query.getInt( query.getColumnIndex("LearnTime") );
        if(query.getInt( query.getColumnIndex("TimeForce") ) > 0) {
            timeForce = true;
        }
        else { timeForce = false; }
        lMode = query.getInt( query.getColumnIndex("LMode") );
        if(query.getInt( query.getColumnIndex("LForce") ) > 0) {
            lForce = true;
        }
        else { lForce = false; }
        if(query.getInt( query.getColumnIndex("EnableVirtual") ) > 0) {
            enableVirtual = true;
        }
        else { enableVirtual = false; }
        mMode = query.getString( query.getColumnIndex("MMode") );
    }

    public void initUi() {
        mText_swId = (TextView) rootView.findViewById(R.id.text_will_id);
        mText_themeId = (TextView) rootView.findViewById(R.id.text_theme_id);
        mText_estimated_time = (TextView) rootView.findViewById(R.id.text_estimated_time);
        mText_have_point_total = (TextView) rootView.findViewById(R.id.text_have_point_total);
        mText_expiredTime = (TextView) rootView.findViewById(R.id.text_validity_date);
        mLayout_theme_introduction = (LinearLayout) rootView.findViewById(R.id.layout_theme_introduction);
        mText_theme_introduction = (TextView) rootView.findViewById(R.id.text_theme_introduction);
        mText_learnMode = (TextView) rootView.findViewById(R.id.text_learn_mode);
        mText_timeLimit = (TextView) rootView.findViewById(R.id.text_learning_time_limit);
        mText_materialMode = (TextView) rootView.findViewById(R.id.text_material_mode);

    }

    public void displayUi() {
        this.setTitle(thName);

        mText_swId.setText(""+swId);
        mText_themeId.setText(""+thId);
        mText_estimated_time.setText(""+thTime);
        Resources res = context.getResources();
        String have_point_total_string = String.format(
                res.getString(R.string.have_point_total),
                thPointTotal);
        mText_have_point_total.setText(have_point_total_string);
        mText_expiredTime.setText(expiredTime);

        if(thIntroduction.equals("null")) {
            mLayout_theme_introduction.setVisibility(View.GONE);
        }
        else {
            mLayout_theme_introduction.setVisibility(View.VISIBLE);
            mText_theme_introduction.setText(thIntroduction);
        }

        // TODO: 抓取登記在使用者的習慣數據來作為預設選項
        String recommand_number_point_string = String.format(
                res.getString(R.string.recommand_number_point),
                lMode);
        mText_learnMode.setText(recommand_number_point_string);
        mText_timeLimit.setText(learnTime + " " + res.getString(R.string.minute));
        mText_materialMode.setText(mMode);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {

            int learnTime = this.learnTime;
            boolean timeForce = this.timeForce;
            int lMode = this.lMode;
            boolean lForce = this.lForce;
            String mMode = this.mMode;

            ((MainActivity) context).startStudyActivity(thId, thName, learnTime, timeForce, lMode, lForce, mMode);

        }
        else if(which == DialogInterface.BUTTON_NEGATIVE) {
            dialog.cancel();
        }
    }
}
