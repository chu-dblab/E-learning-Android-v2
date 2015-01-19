package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import tw.edu.chu.csie.dblab.uelearning.android.R;

public class InternetResultActivity extends ActionBarActivity implements View.OnClickListener {

    //Intent intent = getIntent();
    int length;
    TextView text;
    Button[] button_result = new Button[3];
    Bundle bundle;
    int now_data=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        length = bundle.getInt("internet_length_result", 5);
        setContentView(R.layout.activity_internet_result);
        text = (TextView) findViewById(R.id.textView2);
        button_result[0] = (Button) findViewById(R.id.button);
        button_result[1] = (Button) findViewById(R.id.button2);
        button_result[2] = (Button) findViewById(R.id.button3);
        show_internet_result(0);
        for(int i=0; i<3; i++) {
            button_result[i].setOnClickListener(this);
        }

    }

    public void show_internet_result(int i){

        text.setText("標題:"+bundle.getString("internet_title_result"+i)+"\n簡介:"+bundle.getString("internet_content_result"+i));

    }
    @Override
    public void onClick(View v) {
        int id = v.getId();

        Intent browserIntent;
        if (id == R.id.button) {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bundle.getString("internet_url_result"+now_data)));
            startActivity(browserIntent);
        }
        else  if (id == R.id.button2) {
            if(now_data != length-1)
            {
                now_data++;
                show_internet_result(now_data);
            }
            else Toast.makeText(InternetResultActivity.this, "這是最後一筆資料了!", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.button3)
        {
            if(now_data != 0)
            {
                now_data--;
                show_internet_result(now_data);
            }
            else Toast.makeText(InternetResultActivity.this, "這是第一筆資料了!", Toast.LENGTH_SHORT).show();
        }
    }

}
