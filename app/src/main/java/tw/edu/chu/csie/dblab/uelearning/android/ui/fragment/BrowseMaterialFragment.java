package tw.edu.chu.csie.dblab.uelearning.android.ui.fragment;

/**
 * Created by yuan on 2014/12/25.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.config.Config;
import tw.edu.chu.csie.dblab.uelearning.android.util.ErrorUtils;
import tw.edu.chu.csie.dblab.uelearning.android.util.FileUtils;

/**
 * 瀏覽教材
 */
public class BrowseMaterialFragment extends Fragment{

    // UI上的元件
    protected WebView mWebView;
    protected WebSettings webSettings;

    public static BrowseMaterialFragment newInstance(int sectionNumber) {
        BrowseMaterialFragment fragment = new BrowseMaterialFragment();
        return fragment;
    }

    public BrowseMaterialFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_material, container, false);

        // 界面元件對應
        mWebView = (WebView) rootView.findViewById(R.id.webview＿material_browser);

        // 取得教材路徑
        String materialFilePath = FileUtils.getMaterialIndexPath();

        // 有查到教材路徑
        if (!materialFilePath.equals(null)) {

            // 將網頁內容顯示出來
            webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            //mWebView.addJavascriptInterface(new MaterialJSCall(this), "Android");
            mWebView.loadUrl("file://" + materialFilePath);
            mWebView.setOnKeyListener(new View.OnKeyListener(){
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    }
                    return false;
                }
            });
            if (Config.DEBUG_SHOW_MESSAGE) {
                Toast.makeText(getActivity(), materialFilePath, Toast.LENGTH_SHORT).show();
            }
        } else {
            ErrorUtils.error(getActivity(), "No Material Files");
        }



        return rootView;
    }

}