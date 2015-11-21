package tjjj.com.hackuci2015;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {
    public static final int REQUEST_RECORDING = 1;
    MediaRecorder recorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void callClick(View v) {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        File file = new File(this.getFilesDir().getPath() + "audio.mp4");
        recorder.setOutputFile(file.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("MediaRecorder", "io problems while preparing [" +
                    file.getAbsolutePath() + "]: " + e.getMessage());
        }

        recorder.start();

        //TODO - Initiate call!

        recorder.stop();

        //TODO - Progress dialog for processing the call file...

        //TODO - Process the call!
    }

    public void clipsClick(View v) {
        Intent intent = new Intent(this,CallClipsActivity.class);
        startActivity(intent);
        finish();
    }
}
