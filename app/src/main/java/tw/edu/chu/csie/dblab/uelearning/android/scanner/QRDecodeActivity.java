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
                    Toast.makeText(this, R.string.qr_is_url_not_num, Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    if(text.length()>2)
                    {
                        Toast.makeText(this, R.string.qr_is_illegal, Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        try {
                            int targetId = Integer.valueOf(text);
                            // 解讀正確，進入學習教材
                            // 進入教材頁面
                            if(TargetManager.isForceStudyInRecommand(QRDecodeActivity.this) &&
                                    !TargetManager.isInRecommand(QRDecodeActivity.this, targetId)) {

                                Toast.makeText(QRDecodeActivity.this, R.string.is_not_in_recommand, Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {
                                Intent toLearning = new Intent(this, MaterialActivity.class);
                                toLearning.putExtra("tId", targetId);
                                startActivityForResult(toLearning, LearningActivity.RESULT_MATERIAL);

                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("LearnedPointId", targetId);
                                setResult(RESULT_OK, returnIntent);
                                finish();
                            }

                        } catch(IllegalArgumentException e) {
                            Toast.makeText(this, R.string.qr_is_not_num, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
            }
            else
            {
                Toast.makeText(this, R.string.qr_no_content, Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, R.string.no_camera, Toast.LENGTH_LONG).show();
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