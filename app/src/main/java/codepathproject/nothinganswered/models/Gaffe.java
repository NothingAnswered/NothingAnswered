package codepathproject.nothinganswered.models;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by jnagaraj on 3/6/16.
 */
public class Gaffe {

    public String questionTitle;
    public Uri videoResponseUrl;
    public String profilePicUrl;
    public boolean responded;
    public Bitmap thumbnail;

    public String getUsername() {
        return username;
    }

    public String username;

    public String getQuestionTitle() {
        return questionTitle;
    }

    public Uri getVideoResponseUrl() {
        return videoResponseUrl;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }
}
