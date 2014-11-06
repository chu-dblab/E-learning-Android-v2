package tw.edu.chu.csie.dblab.uelearning.android.util;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by yuan on 2014/11/6.
 */
public class HelpUtils {

    /**
     * 顯示"關於"資訊的對話框
     * @param context 帶入Android基底Context
     */
    @SuppressWarnings("deprecation")
    public static void showAboutDialog(Context context){
        //建立對話方塊AlertDialog
        AlertDialog about_AlertDialog = new AlertDialog.Builder(context).create();
        about_AlertDialog.setTitle(R.string.about);	//設定AlertDialog標題

        //"關於"視窗內容裡建立Layout面板
        ScrollView about_AlertDialog_scrollView = new ScrollView(context);
        LinearLayout about_AlertDialog_content = new LinearLayout(context);
        about_AlertDialog_content.setOrientation(LinearLayout.VERTICAL);	//設定為直向的layout
        about_AlertDialog_content.setPadding(16,16,16,16);	//設定layout的邊界大小（左、上、右、下）

        //"關於"視窗內容裡面內容字串
        TextView content_textView = new TextView(context);
        content_textView.setTextAppearance(context, android.R.style.TextAppearance_Medium);	//指定文字樣式為中等大小
        //content_textView.setAutoLinkMask(Linkify.ALL);	//設定成會自動加上連結

        try{
            //宣告"取得套件資訊"的物件
            PackageInfo package_info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            content_textView.setText(
                    context.getString(R.string.app_name) + "\n\n"
                            + context.getString(R.string.package_name) +"\n"+ package_info.packageName + "\n"
                            + context.getString(R.string.version) + package_info.versionName + "\n"
                            + "\n");
        } catch (PackageManager.NameNotFoundException ex) {
            content_textView.setText(context.getString(R.string.getPackageInfo_error)+ "\n");
            //e.printStackTrace();
        } catch(Exception ex){
            Toast.makeText(context, context.getString(R.string.inside_error), Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
        about_AlertDialog_content.addView(content_textView);

        //指定這個面板到這個對話框
        about_AlertDialog_scrollView.addView(about_AlertDialog_content);
        about_AlertDialog.setView(about_AlertDialog_scrollView);

        about_AlertDialog.setButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        about_AlertDialog.show();
    }
}
