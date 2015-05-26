package tw.edu.chu.csie.dblab.uelearning.android.util;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.database.DBProvider;
import tw.edu.chu.csie.dblab.uelearning.android.learning.UserUtils;
import tw.edu.chu.csie.dblab.uelearning.android.ui.StartActivity;

public class NetworkUtils {
    /**
     * 目前是否有網路連線
     * @param context 帶入Android基底Context
     * @return <code>true</code>目前有網路可使用
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni=cm.getActiveNetworkInfo();
        if(ni!=null && ni.isConnected()){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 跳出沒有網路的Dialog
     * @param context
     */
    public static void showNoNetworkDialog(final Context context, final boolean isAllowDismiss) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.no_network_dialog_title))
               .setMessage(context.getString(R.string.no_network_dialog_message))
               .setCancelable(isAllowDismiss)
               // 重試按鈕
               .setPositiveButton(context.getString(R.string.retry), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       if (!isNetworkConnected(context)) {
                           showNoNetworkDialog(context, isAllowDismiss);
                       }
                   }
               })
               // 登出按鈕
               .setNegativeButton(context.getString(R.string.logout), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // 清除App資料
                       new DBProvider(context).removeAllUserData();

                       // 重新執行App
                       Intent mStartActivity = new Intent(context, StartActivity.class);
                       int mPendingIntentId = 123456;
                       PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                       AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                       mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                       System.exit(0);
                   }
               });
        // 取消/離開按鈕
        if(isAllowDismiss) {
            builder.setNeutralButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        else {
            builder.setNeutralButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
        }

        // 顯示Dialog
        builder.create().show();
    }
}
