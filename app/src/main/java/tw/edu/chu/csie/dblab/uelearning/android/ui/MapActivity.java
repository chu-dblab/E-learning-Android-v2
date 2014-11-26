package tw.edu.chu.csie.dblab.uelearning.android.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import tw.edu.chu.csie.dblab.uelearning.android.R;
import tw.edu.chu.csie.dblab.uelearning.android.util.FileUtils;

public class MapActivity extends ActionBarActivity {

    private TextView nextPointView, nextPointTimeView, remainedTimeView;
    private ImageView mapView;
    private FileUtils fileUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fileUtils = new FileUtils();
        mapView = (ImageView)findViewById(R.id.learning_map);
        nextPointView = (TextView)findViewById(R.id.learning_next_point);
        nextPointTimeView = (TextView)findViewById(R.id.learning_next_point_time);
        remainedTimeView = (TextView)findViewById(R.id.learning_remaining_time);
        Bitmap bmp = BitmapFactory.decodeFile(fileUtils.getMaterialPath() + "map/1F.gif");
        mapView.setImageBitmap(bmp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
