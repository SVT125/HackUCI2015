package tjjj.com.hackuci2015;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class ClipCell {
    private String textTranslation, caller, timeCalled;
    private Uri clip;

    public ClipCell(String translation, String caller, String timeCalled) {
        this.textTranslation = translation;
        this.caller = caller;
        this.timeCalled = timeCalled;
    }

    public Uri getClip() {
        return clip;
    }

    public void setClip(Uri clip) {
        this.clip = clip;
    }

    public String getTextTranslation() {
        return textTranslation;
    }

    public void setTextTranslation(String textTranslation) {
        this.textTranslation = textTranslation;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getTimeCalled() {
        return timeCalled;
    }

    public void setTimeCalled(String timeCalled) {
        this.timeCalled = timeCalled;
    }

    public void playClip() {
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(clip.getPath());
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
