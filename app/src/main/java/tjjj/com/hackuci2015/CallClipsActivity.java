package tjjj.com.hackuci2015;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CallClipsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_clips);
        ListView lv = (ListView)findViewById(R.id.listView);

        class ClipAdapter extends ArrayAdapter<ClipCell> {
            public ClipAdapter(Context context, ArrayList<ClipCell> cellList) {
                super(context,0,cellList);
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ClipCell card = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.clip_cell_info, parent, false);
                }
                //TODO - Initialize each of the fields in the cell...
                return convertView;
            }
        }
    }

}
