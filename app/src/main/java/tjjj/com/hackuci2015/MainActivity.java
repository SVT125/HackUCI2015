package tjjj.com.hackuci2015;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {
    WAVAudioRecorder recorder = null;

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "Logging calls";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {
                    recorder.stop();
                    isPhoneCalling = false;

                    //TODO - Progress dialog for processing the call file...

                    //TODO - Process the call!

                    //set up MediaPlayer
                    MediaPlayer mp = new MediaPlayer();

                    try {
                        mp.setDataSource(MainActivity.this.getFilesDir().getPath() + "callRecording.wav");
                        mp.prepare();
                        mp.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

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

        recorder = new WAVAudioRecorder(false,0,0,0,0);
        File file = new File(this.getFilesDir().getPath() + "callRecording.wav");
        recorder.setOutputFile(file.getAbsolutePath());
        recorder.prepare();

        recorder.start();

        //TODO - Initiate call!
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        EditText phoneText = (EditText) findViewById(R.id.phone_number_view);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneText.getText()));

        try {
            startActivity(callIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clipsClick(View v) {
        Intent intent = new Intent(this,CallClipsActivity.class);
        startActivity(intent);
        finish();
    }
}
