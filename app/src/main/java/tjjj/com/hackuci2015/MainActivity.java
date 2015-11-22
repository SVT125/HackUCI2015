package tjjj.com.hackuci2015;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.microsoft.projectoxford.speechrecognition.Contract;
import com.microsoft.projectoxford.speechrecognition.DataRecognitionClient;
import com.microsoft.projectoxford.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.projectoxford.speechrecognition.RecognitionResult;
import com.microsoft.projectoxford.speechrecognition.RecognitionStatus;
import com.microsoft.projectoxford.speechrecognition.RecognizedPhrase;
import com.microsoft.projectoxford.speechrecognition.SpeechRecognitionMode;
import com.microsoft.projectoxford.speechrecognition.SpeechRecognitionServiceFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements ISpeechRecognitionServerEvents {
    WAVAudioRecorder recorder = null;
    int m_waitSeconds = 0;
    DataRecognitionClient m_dataClient = null;
    SpeechRecognitionMode m_recoMode;
    boolean m_isIntent;
    FinalResponseStatus isReceivedResponse = FinalResponseStatus.NotReceived;

    private static String destinationPath = null;
    private List<ClipCell> cellList = new ArrayList<ClipCell>();

    public enum FinalResponseStatus { NotReceived, OK, Timeout }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "Logging calls";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");
                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {
                    recorder.stop();
                    isPhoneCalling = false;

                    Log.i("Processing","Conducting recognition...");
                    String filename = MainActivity.this.getFilesDir().getPath() + "callRecording.wav";

                    //TODO - Progress dialog for processing the call file and splitting by silence...
                    File file = new File(destinationPath);
                    file.mkdirs();
                    Trim.trimAudioClip(filename, destinationPath);
                    cellList.clear();

                    //TODO - Add specific call info
                    for(File clip : file.listFiles())
                        cellList.add(new ClipCell(clip.getPath(),null,null));

                    //TODO - Process the call!
                    RecognitionTask doDataReco = new RecognitionTask(m_dataClient, m_recoMode, filename);
                    try
                    {
                        doDataReco.execute().get(m_waitSeconds, TimeUnit.SECONDS);
                    }
                    catch (Exception e)
                    {
                        doDataReco.cancel(true);
                        isReceivedResponse = FinalResponseStatus.Timeout;
                        e.printStackTrace();
                    }

                } else
                    Log.i("Calling","Fell through the phone call ending");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        destinationPath = this.getFilesDir().getPath() + "/finishedclips/";

        // m_recoMode can be SpeechRecognitionMode.ShortPhrase or SpeechRecognitionMode.LongDictation
        m_recoMode = SpeechRecognitionMode.ShortPhrase;
        m_isIntent = false;

        m_waitSeconds = m_recoMode == SpeechRecognitionMode.ShortPhrase ? 20 : 200;

        initializeRecoClient();
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

        recorder = WAVAudioRecorder.getInstance(false);
            File file = new File(this.getFilesDir().getPath() + "callRecording.wav");
        recorder.setOutputFile(file.getAbsolutePath());
        recorder.prepare();

        recorder.start();

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
        intent.putExtra("destinationPath",destinationPath);
        startActivity(intent);
        finish();
    }

    public void lastCallClick(View v) {
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

    public void onPartialResponseReceived(final String response) {
        Log.i("Partial phrase",response);
    }

    public void onFinalResponseReceived(final RecognitionResult response)
    {
        boolean isFinalDicationMessage = m_recoMode == SpeechRecognitionMode.LongDictation &&
                (response.RecognitionStatus == RecognitionStatus.EndOfDictation ||
                        response.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);

        if ((m_recoMode == SpeechRecognitionMode.ShortPhrase) || isFinalDicationMessage)
            this.isReceivedResponse = FinalResponseStatus.OK;

        if (!isFinalDicationMessage) {
            //TODO - Results found here! For now, log out the phrases and their confidences.
            Log.i("Result length",""+response.Results.length);
            for (int i = 0; i < response.Results.length; i++) {
                Log.i("Phrase",response.Results[i].Confidence +
                        "|" + response.Results[i].DisplayText);
            }
        }
    }

    /**
     * Called when a final response is received and its intent is parsed
     */
    public void onIntentReceived(final String payload) { }

    public void onError(final int errorCode, final String response)
    {
        final CharSequence text = errorCode + " " + response + "\n";
        final int duration = Toast.LENGTH_SHORT;

        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MainActivity.this, text, duration);
                toast.show();
            }
        });
    }

    /**
     * Invoked when the audio recording state has changed.
     *
     * @param recording The current recording state
     */
    public void onAudioEvent(boolean recording) { }

    /**
     * Speech recognition with data (for example from a file or audio source).
     * The data is broken up into buffers and each buffer is sent to the Speech Recognition Service.
     * No modification is done to the buffers, so the user can apply their
     * own VAD (Voice Activation Detection) or Silence Detection
     *
     * Parameters: dataClient, recoMode, fileName
     */
    private class RecognitionTask extends AsyncTask<Void, Void, Void> {
        DataRecognitionClient dataClient;
        SpeechRecognitionMode recoMode;
        String filename;

        RecognitionTask(DataRecognitionClient dataClient, SpeechRecognitionMode recoMode, String filename) {
            this.dataClient = dataClient;
            this.recoMode = recoMode;
            this.filename = filename;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //TODO - Clean up!
                InputStream fileStream = new FileInputStream(filename);
                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                do {
                    // Get  Audio data to send into byte buffer.
                    bytesRead = fileStream.read(buffer);

                    if (bytesRead > -1) {
                        // Send of audio data to service.
                        dataClient.sendAudio(buffer, bytesRead);
                    }
                } while (bytesRead > 0);
            }
            catch(IOException ex) {
                //+Contract.fail();
                ex.printStackTrace();
            }
            finally {
                dataClient.endAudio();
            }
            return null;
        }
    }

    void initializeRecoClient()
    {
        String language = "en-us";

        String subscriptionKey = this.getString(R.string.subscription_key);

        if (m_dataClient == null) {
            if (!m_isIntent) {
                m_dataClient = SpeechRecognitionServiceFactory.createDataClient(this,
                        m_recoMode,
                        language,
                        this,
                        subscriptionKey);
            }
        }
    }
}
