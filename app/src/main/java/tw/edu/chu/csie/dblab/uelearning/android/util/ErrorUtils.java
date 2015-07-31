package tw.edu.chu.csie.dblab.uelearning.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;

/**
 * Created by yuan on 2014/11/25.
 */
public class ErrorUtils {

    public static void error(Context context, String msg) {
        Log.e("UElearning", msg);
        if(Config.DEBUG_SHOW_MESSAGE) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inside_error).toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static void error(Context context, String msg, Throwable e) {
        Log.e("UElearning", Log.getStackTraceString(e));
        if(Config.DEBUG_SHOW_MESSAGE) {
            Toast.makeText(context, msg+"\n"+e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    public static void error(Context context, Throwable e) {
        error(context, context.getResources().getString(R.string.inside_error).toString(), e);
    }

    // =============================================================================================

    public static AlertDialog.Builder noStudyActivityDialog(final Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.error_dialog_title)
                .setMessage(R.string.no_study_activity_dialog_message);
    }
}
