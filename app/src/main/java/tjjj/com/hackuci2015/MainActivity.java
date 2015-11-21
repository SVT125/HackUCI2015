package tjjj.com.hackuci2015;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.microsoft.projectoxford.speechrecognition.DataRecognitionClientWithIntent;
import com.microsoft.projectoxford.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.projectoxford.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.projectoxford.speechrecognition.MicrophoneRecognitionClientWithIntent;
import com.microsoft.projectoxford.speechrecognition.RecognitionResult;
import com.microsoft.projectoxford.speechrecognition.RecognitionStatus;
import com.microsoft.projectoxford.speechrecognition.SpeechRecognitionMode;
import com.microsoft.projectoxford.speechrecognition.SpeechRecognitionServiceFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity implements ISpeechRecognitionServerEvents {
    WAVAudioRecorder recorder = null;
    int m_waitSeconds = 0;
    DataRecognitionClient m_dataClient = null;
    MicrophoneRecognitionClient m_micClient = null;
    private static final boolean m_isMicrophoneReco = false;
    SpeechRecognitionMode m_recoMode;
    boolean m_isIntent;
    FinalResponseStatus isReceivedResponse = FinalResponseStatus.NotReceived;

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

        recorder = new WAVAudioRecorder(false,0,0,0,0);
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
        startActivity(intent);
        finish();
    }

    public void onPartialResponseReceived(final String response) { }

    public void onFinalResponseReceived(final RecognitionResult response)
    {
        boolean isFinalDicationMessage = m_recoMode == SpeechRecognitionMode.LongDictation &&
                (response.RecognitionStatus == RecognitionStatus.EndOfDictation ||
                        response.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);
        if (m_isMicrophoneReco && ((m_recoMode == SpeechRecognitionMode.ShortPhrase) || isFinalDicationMessage)) {
            // we got the final result, so it we can end the mic reco.  No need to do this
            // for dataReco, since we already called endAudio() on it as soon as we were done
            // sending all the data.
            m_micClient.endMicAndRecognition();
        }

        if ((m_recoMode == SpeechRecognitionMode.ShortPhrase) || isFinalDicationMessage)
            this.isReceivedResponse = FinalResponseStatus.OK;

        if (!isFinalDicationMessage) {
            //TODO - Results found here...
            /*
            EditText myEditText = (EditText) findViewById(R.id.editText1);
            myEditText.append("***** Final NBEST Results *****\n");
            for (int i = 0; i < response.Results.length; i++) {
                myEditText.append(i + " Confidence=" + response.Results[i].Confidence +
                        " Text=\"" + response.Results[i].DisplayText + "\"\n");
            }
            */
        }
    }

    /**
     * Called when a final response is received and its intent is parsed
     */
    public void onIntentReceived(final String payload) { }

    public void onError(final int errorCode, final String response)
    {
        Context context = getApplicationContext();
        CharSequence text = errorCode + " " + response + "\n";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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
                // Note for wave files, we can just send data from the file right to the server.
                // In the case you are not an audio file in wave format, and instead you have just
                // raw data (for example audio coming over bluetooth), then before sending up any
                // audio data, you must first send up an SpeechAudioFormat descriptor to describe
                // the layout and format of your raw audio data via DataRecognitionClient's sendAudioFormat() method.
                // String filename = recoMode == SpeechRecognitionMode.ShortPhrase ? "whatstheweatherlike.wav" : "batman.wav";
                InputStream fileStream = getAssets().open(filename);
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
                Contract.fail();
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
        String luisAppID = this.getString(R.string.luisAppID);
        String luisSubscriptionID = this.getString(R.string.luisSubscriptionID);

        if (m_isMicrophoneReco && null == m_micClient) {
            if (!m_isIntent) {
                m_micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(this,
                        m_recoMode,
                        language,
                        this,
                        subscriptionKey);
            }
            else {
                MicrophoneRecognitionClientWithIntent intentMicClient;
                intentMicClient = SpeechRecognitionServiceFactory.createMicrophoneClientWithIntent(this,
                        language,
                        this,
                        subscriptionKey,
                        luisAppID,
                        luisSubscriptionID);
                m_micClient = intentMicClient;

            }
        }
        else if (!m_isMicrophoneReco && null == m_dataClient) {
            if (!m_isIntent) {
                m_dataClient = SpeechRecognitionServiceFactory.createDataClient(this,
                        m_recoMode,
                        language,
                        this,
                        subscriptionKey);
            }
            else {
                DataRecognitionClientWithIntent intentDataClient;
                intentDataClient = SpeechRecognitionServiceFactory.createDataClientWithIntent(this,
                        language,
                        this,
                        subscriptionKey,
                        luisAppID,
                        luisSubscriptionID);
                m_dataClient = intentDataClient;
            }
        }
    }
}
