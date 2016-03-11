package codepathproject.nothinganswered.models;

/**
 * Created by jnagaraj on 3/6/16.
 */
public class Gaffe {

    public String questionTitle;
    public String videoResponseUrl;
    public String profilePicUrl;
    public boolean responded;

    public String getUsername() {
        return username;
    }

    public String username;

    public String getQuestionTitle() {
        return questionTitle;
    }

    public String getVideoResponseUrl() {
        return videoResponseUrl;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }
}
