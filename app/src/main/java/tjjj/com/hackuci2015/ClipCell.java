package tjjj.com.hackuci2015;

import java.io.File;

public class ClipCell {
    private String textTranslation, caller, timeCalled;
    private File clip;

    public File getClip() {
        return clip;
    }

    public void setClip(File clip) {
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
}
