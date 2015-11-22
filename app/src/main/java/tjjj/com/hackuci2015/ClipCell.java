package tjjj.com.hackuci2015;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ClipCell implements Parcelable {
    private String textTranslation, caller, timeCalled, path;

    public static final Parcelable.Creator<ClipCell> CREATOR
            = new Parcelable.Creator<ClipCell>() {
        public ClipCell createFromParcel(Parcel in) {
            String[] params = new String[4];
            in.readStringArray(params);
            ClipCell cell = new ClipCell(params[0],params[1],params[2],params[3]);
            return cell;
        }

        public ClipCell[] newArray(int size) {
            return new ClipCell[size];
        }
    };


    public ClipCell(String path, String translation, String caller, String timeCalled) {
        this.path = path;
        this.textTranslation = translation;
        this.caller = caller;
        this.timeCalled = timeCalled;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        String[] params = new String[] {path,textTranslation,caller,timeCalled};
        parcel.writeStringArray(params);
    }

    public int describeContents() { return 0; }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
