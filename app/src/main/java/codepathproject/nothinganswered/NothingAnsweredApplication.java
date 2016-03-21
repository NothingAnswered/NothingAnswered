package codepathproject.nothinganswered;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import java.io.File;

import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.NAUser;
import codepathproject.nothinganswered.models.Question;
import codepathproject.nothinganswered.models.Video;

/**
 * Created by gpalem on 3/4/16.
 */
public class NothingAnsweredApplication extends Application {
    private static Context context;
    private ParseClient parseClient;
    private FacebookClient facebookClient;

    public static VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        // set applicationId and server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        ParseObject.registerSubclass(Question.class);
        ParseObject.registerSubclass(NAUser.class);
        ParseObject.registerSubclass(Video.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("nothinganswered") // should correspond to APP_ID env variable
                .clientKey(null)
                .enableLocalDataStore()
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://nothinganswered.herokuapp.com/parse/").build());
        ParseFacebookUtils.initialize(this);
        NothingAnsweredApplication.context = this;

        //Set clients for another
        parseClient = getParseClient();
        facebookClient = getFacebookClient();
        parseClient.setFacebookClient(facebookClient);
        facebookClient.setParseClient(parseClient);
    }

    public static void setParseInstallationObject() {
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.put(NAUser.FACEBOOK_ID, Friends.myId);
        parseInstallation.saveInBackground();

    }

    public static ParseClient getParseClient() {
        return ParseClient.getInstance(NothingAnsweredApplication.context);
    }

    public static FacebookClient getFacebookClient() {
        return FacebookClient.getInstance(NothingAnsweredApplication.context);
    }

    public static String getProfileImage(String id) {
        return "https://graph.facebook.com/" + id + "/picture?type=large";
    }

    public static String getVideosFolder() {
        if (!isExternalStorageAvailable()) {
            Toast.makeText(context, "External Storage not available", Toast.LENGTH_SHORT).show();
            return null;
        }
        File saveFolder = new File(context.getExternalFilesDir(null), "MaterialCameraVideos");
        if (!saveFolder.exists() && !saveFolder.mkdirs())
            throw new RuntimeException("Unable to create save directory, make sure WRITE_EXTERNAL_STORAGE permission is granted.");
        return saveFolder.getPath() + File.separator;
    }

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
