package tjjj.com.hackuci2015;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;

public class MainActivity extends Activity {

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
        final MediaRecorder callrecorder = new MediaRecorder();
        callrecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        callrecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        callrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        callrecorder.setOutputFile("call.mp4");

        try {
            callrecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            callrecorder.start();
        } catch(IllegalStateException e) {
            e.printStackTrace();
        }

        //TODO - Initiate call!

        callrecorder.stop();

        //TODO - Progress dialog for processing the call file...

        //TODO - Process the call!
    }

    public void clipsClick(View v) {
        Intent intent = new Intent(this,CallClipsActivity.class);
        startActivity(intent);
        finish();
    }
}
