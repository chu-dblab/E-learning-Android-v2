package tw.edu.chu.csie.dblab.uelearning.android.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.ui.MainActivity;

/**
 * Created by yuan on 2015/2/2.
 */
public class StartStudyActivityDialog extends AlertDialog.Builder
        implements DialogInterface.OnClickListener {

    protected int thId;
    protected String thName;
    protected int thTime;
    protected int thPointTotal;
    protected String thIntroduction;

    View rootView;
    Context context;
    private TextView mText_themeId, mText_estimated_time, mText_have_point_total;
    private ScrollView mScroll_theme_introduction;
    private TextView mText_theme_introduction;
    private RadioGroup mRadioG_learnMode;
    private CheckBox mCheck_learnMode_force, mCheck_timeLimit;
    private EditText mEdit_learnMode, mEdit_timeLimit;
    private Spinner mSpinner_materialMode;

    /**
     * Create a Dialog window that uses the default dialog frame style.
     *
     * @param context The Context the Dialog is to run it.  In particular, it
     *                uses the window manager and theme in this context to
     *                present its UI.
     */
    public StartStudyActivityDialog(final Context context, int position) {
        super(context);
        this.context = context;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_start_study_activity, null);
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

        thId = query.getInt( query.getColumnIndex("ThID") );
        thName = query.getString( query.getColumnIndex("ThName") );
        thIntroduction = query.getString( query.getColumnIndex("ThIntroduction") );
        thTime = query.getInt( query.getColumnIndex("LearnTime") );
        thPointTotal = query.getInt( query.getColumnIndex("TargetTotal") );
    }

    public void initUi() {
        mText_themeId = (TextView) rootView.findViewById(R.id.text_theme_id);
        mText_estimated_time = (TextView) rootView.findViewById(R.id.text_estimated_time);
        mText_have_point_total = (TextView) rootView.findViewById(R.id.text_have_point_total);
        mScroll_theme_introduction = (ScrollView) rootView.findViewById(R.id.scroll_theme_introduction);
        mText_theme_introduction = (TextView) rootView.findViewById(R.id.text_theme_introduction);
        mRadioG_learnMode = (RadioGroup) rootView.findViewById(R.id.radioG_learn_mode);
        mEdit_learnMode = (EditText) rootView.findViewById(R.id.edit_learn_mode);
        mCheck_learnMode_force = (CheckBox) rootView.findViewById(R.id.check_learn_mode_force);
        mEdit_timeLimit = (EditText) rootView.findViewById(R.id.edit_learning_time_limit);
        mCheck_timeLimit = (CheckBox) rootView.findViewById(R.id.check_learning_time_limit);
    }

    public void displayUi() {
        this.setTitle(thName);
        mText_themeId.setText(""+thId);
        mText_estimated_time.setText(""+thTime);
        Resources res = context.getResources();
        String have_point_total_string = String.format(
                res.getString(R.string.have_point_total),
                thPointTotal);
        mText_have_point_total.setText(have_point_total_string);
        if(thIntroduction.equals("null")) {
            mScroll_theme_introduction.setVisibility(View.GONE);
        }
        else {
            mScroll_theme_introduction.setVisibility(View.VISIBLE);
            mText_theme_introduction.setText(thIntroduction);
        }

        mEdit_learnMode.setText("3");
        mEdit_timeLimit.setText(""+thTime);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {

            int learnTime = Integer.valueOf( mEdit_timeLimit.getText().toString() );
            boolean timeForce = mCheck_timeLimit.isChecked();
            int lMode = Integer.valueOf( mEdit_learnMode.getText().toString() );
            boolean lForce = mCheck_learnMode_force.isChecked();
            String mMode = null;

            ((MainActivity) context).startStudyActivity(thId, thName, learnTime, timeForce, lMode, lForce, mMode);

        }
        else if(which == DialogInterface.BUTTON_NEGATIVE) {
            dialog.cancel();
        }
    }
}
