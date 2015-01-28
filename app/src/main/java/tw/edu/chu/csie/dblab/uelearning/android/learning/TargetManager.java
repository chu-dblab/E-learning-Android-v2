package tw.edu.chu.csie.dblab.uelearning.android.learning;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.InputFilter;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.scanner.QRDecodeActivity;
import tw.edu.chu.csie.dblab.uelearning.android.ui.LearningActivity;
import tw.edu.chu.csie.dblab.uelearning.android.ui.MaterialActivity;

/**
 * Created by yuan on 2015/1/16.
 */
public class TargetManager {

    /**
     * 取得此主題起始標的
     * @param context ANDROID基底
     * @return int 起始標的編號
     */
    public static int getStartTargetId(Context context) {
        DBProvider db = new DBProvider(context);
        Cursor activity = db.get_activity();
        activity.moveToFirst();
        int startTId = activity.getInt(activity.getColumnIndex("StartTID"));
        return startTId;
    }

    /**
     * 是否強制必須要在推薦的學習點內
     * @param context ANDROID基底
     * @return bool 是否強制
     */
    public static boolean isForceStudyInRecommand(Context context) {
        DBProvider db = new DBProvider(context);
        Cursor query = db.get_activity();
        query.moveToFirst();

        int lForceInt = query.getInt(query.getColumnIndex("LForce"));
        if(lForceInt != 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 是否已有推薦的學習點
     * @param context Android基底
     * @return 是否已有推薦的學習點
     */
    public static boolean isHaveRecommand(Context context) {
        DBProvider db = new DBProvider(context);
        Cursor query = db.getAll_recommand();

        if(query.getCount() >= 1) return true;
        else return false;
    }

    /**
     * 此學習點是否為推薦的
     * @param context Android基底
     * @param tId 標的編號
     * @return 是否為推薦的學習點
     */
    public static boolean isInRecommand(Context context, int tId) {

        DBProvider db = new DBProvider(context);
        Cursor query = db.get_recommand_byTargetId(tId);

        if(query.getCount() >= 1) return true;
        else return false;
    }

    public static void enterPointByQRCode(final Context context) {
        Intent toQRScan = new Intent(context, QRDecodeActivity.class);
        Activity activity = (Activity) context;
        activity.startActivityForResult(toQRScan, LearningActivity.RESULT_MATERIAL);
    }

    public static void enterPointByDialog(final Context context) {
        final AlertDialog.Builder mDialog_inputTId = new AlertDialog.Builder(context);
        mDialog_inputTId.setTitle(R.string.keyin_tid_message);

        final EditText mEdit_inputTId = new EditText(context);
        mEdit_inputTId.setInputType(InputType.TYPE_CLASS_NUMBER);
        // 設定最大長度
        mEdit_inputTId.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});

        mDialog_inputTId.setView(mEdit_inputTId);
        mDialog_inputTId.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 隱藏鍵盤（實際上是切換鍵盤是否顯示）
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                // 取得輸入的標的編號
                String tId_string = mEdit_inputTId.getText().toString();

                // 判斷是否有輸入數字
                if(!tId_string.equals("")) {
                    // 取得剛剛輸入的編號
                    int tId = Integer.valueOf(tId_string);

                    // 進入教材頁面
                    if(isForceStudyInRecommand(context) && !isInRecommand(context, tId)) {
                        Toast.makeText(context, R.string.is_not_in_recommand, Toast.LENGTH_LONG).show();
                    }
                    else {
                        Activity activity = (Activity) context;
                        Intent toMaterial = new Intent(activity, MaterialActivity.class);
                        toMaterial.putExtra("tId", tId);
                        activity.startActivityForResult(toMaterial, LearningActivity.RESULT_MATERIAL);
                    }
                }

            }
        });
        mDialog_inputTId.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 隱藏鍵盤（實際上是切換鍵盤是否顯示）
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        mDialog_inputTId.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // 隱藏鍵盤（實際上是切換鍵盤是否顯示）
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        mDialog_inputTId.show();

        // 馬上設定輸入點與顯示鍵盤
        mEdit_inputTId.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

}
