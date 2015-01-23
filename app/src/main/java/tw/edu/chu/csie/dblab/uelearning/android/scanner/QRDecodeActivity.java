package tw.edu.chu.csie.dblab.uelearning.android.scanner;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.Toast;

import qrcodereaderview.QRCodeReaderView;
import qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.learning.TargetManager;
import tw.edu.chu.csie.dblab.uelearning.android.ui.LearningActivity;
import tw.edu.chu.csie.dblab.uelearning.android.ui.MaterialActivity;

public class QRDecodeActivity extends ActionBarActivity implements OnQRCodeReadListener {
    //private TextView myTextView;
    private boolean tabEnable = true;
    private QRCodeReaderView mydecoderview;
    private String gettext = "";
    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_decode);

        // 設定ActionBar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_qr_decode);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setSubtitle(R.string.subtitle_activity_qrdecode);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // 當QRCode被Decode時呼叫
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if(tabEnable){
            tabEnable = false;

            this.gettext = text;

            if(text!="")
            {
                if(URLUtil.isNetworkUrl(text))
                {
                    // TODO 拉開成String
                    Toast.makeText(this, "此 QR-code內容 是個網址，非「標的編號」!!", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    if(text.length()>2)
                    {
                        // TODO 拉開成String
                        Toast.makeText(this, "此內容不符合!!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        try {
                            int targetId = Integer.valueOf(text);
                            // 解讀正確，進入學習教材
                            if(TargetManager.isInRecommand(QRDecodeActivity.this, targetId)) {
                                Intent toLearning = new Intent(this, MaterialActivity.class);
                                toLearning.putExtra("tId", targetId);
                                startActivityForResult(toLearning, LearningActivity.RESULT_MATERIAL);

                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("LearnedPointId", targetId);
                                setResult(RESULT_OK, returnIntent);
                                finish();
                            }
                            else {
                                // TODO 拉開成String
                                Toast.makeText(this, "這不是這次的推薦學習點喔～", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } catch(IllegalArgumentException e) {
                            // TODO 拉開成String
                            Toast.makeText(this, "此內容不是數字喔!!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
            }
            else
            {
                // TODO 拉開成String
                Toast.makeText(this, "掃描內容為空!!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    // 回傳標地編號，方便其他Class使用
    public String getText()
    {
        return gettext;
    }

    // 當行動裝置裝置沒有Camera
    @Override
    public void cameraNotFound() {
        // TODO 拉開成String
        Toast.makeText(this, "行動裝置找不到Camera!!", Toast.LENGTH_LONG).show();
    }

    // Called when there's no QR codes in the camera preview image
    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }
}