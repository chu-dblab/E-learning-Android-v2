package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import tw.edu.chu.csie.dblab.uelearning.android.R;

public class DetailResultAcrivity extends ActionBarActivity implements View.OnClickListener {

    int length;
    TextView text;
    Button[] button_result = new Button[2];
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_result_acrivity);
        bundle = getIntent().getExtras();
        length = bundle.getInt("detail_length_result", 5);
        setContentView(R.layout.activity_detail_result_acrivity);
        text = (TextView) findViewById(R.id.textView2);
        button_result[0] = (Button) findViewById(R.id.button);
        button_result[1] = (Button) findViewById(R.id.button2);

        show_detail_result(0);
        for(int i=0; i<2; i++) {
            button_result[i].setOnClickListener(this);
        }
    }
    public void show_detail_result(int i){

        text.setText("標題:"+bundle.getString("detail_title_result"+i));

    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        int now_data=0;
        Intent browserIntent;
        if (id == R.id.button) {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bundle.getString("detail_url_result" + now_data)));
            startActivity(browserIntent);
        }
        else  if (id == R.id.button2) {
            if(now_data != length)
            {
                now_data++;
                show_detail_result(now_data);
            }
            else Toast.makeText(DetailResultAcrivity.this, "這是最後一筆資料了!", Toast.LENGTH_SHORT).show();
        }
    }

}
