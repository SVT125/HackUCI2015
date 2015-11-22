package tjjj.com.hackuci2015;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CallClipsActivity extends Activity {
    ArrayList<ClipCell> cellList = new ArrayList<ClipCell>();
    private static String destinationPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_clips);
        ListView lv = (ListView)findViewById(R.id.listView);

        Intent intent = getIntent();
        String nPath = intent.getStringExtra("destinationPath");
        cellList = intent.getParcelableArrayListExtra("cellList");
        Log.i("Cell list length",""+cellList.size());

        class ClipAdapter extends ArrayAdapter<ClipCell> {
            public ClipAdapter(Context context, ArrayList<ClipCell> cellList) {
                super(context,0,cellList);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ClipCell cell = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(CallClipsActivity.this).inflate(R.layout.clip_cell_info, parent, false);
                }

                TextView translationView = (TextView)convertView.findViewById(R.id.translation_text),
                        infoView = (TextView)convertView.findViewById(R.id.info_text);
                translationView.setText("Cell " + Integer.toString(position+1));//translationView.setText(cell.getTextTranslation());

                String description = cell.getCaller() + "|" + cell.getTimeCalled();
                infoView.setText(description);
                return convertView;
            }
        }

        ClipAdapter adapter = new ClipAdapter(this,cellList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cellList.get(position).playClip();
            }
        });
    }

}
