package tw.edu.chu.csie.dblab.uelearning.android.ui.js_handler;

import android.webkit.JavascriptInterface;
import android.widget.Toast;

import tw.edu.chu.csie.dblab.uelearning.android.ui.MaterialActivity;

/**
 * Created by yuan on 2015/1/24.
 */
public class MaterialJSHandler {

    private MaterialActivity context;

    public MaterialJSHandler(MaterialActivity context) {
        this.context = context;
    }

    /**
     * 結束學習
     *
     * 當接到由教材網頁發出已完成學習的訊號時，觸發MaterialActivity的學習完成函式
     */
    @JavascriptInterface
    public void learnFinish() {
        context.finishLearn();
    }

    /**
     * 相容於一代教材的結束學習
     *
     * 注意！這僅是為了相容一代教材因應的函式，請避免使用此方法。回答問題的傳遞請另外使用。
     * 當接到由教材網頁發出已完成學習的訊號時，接應學生作答狀況，並觸發MaterialActivity的學習完成函式
     * @param ansQID 問題編號陣列
     * @param ansCheck 回答內容陣列
     */
    @JavascriptInterface
    public void learnFinish(String[] ansQID, String[] ansCheck) {
        learnFinish();
    }

    @JavascriptInterface
    public void goBack() {
        //context.webViewBackToView();
    }
}
