package tw.edu.chu.csie.dblab.uelearning.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
    public static void showNoNetworkDialog(Context context) {

    }
}
