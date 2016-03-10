package codepathproject.nothinganswered;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.models.NAUser;
import codepathproject.nothinganswered.models.Question;

/**
 * Created by gpalem on 3/4/16.
 */
public class NothingAnsweredApplication extends Application {
    private static Context context;
    private ParseClient parseClient;
    private FacebookClient facebookClient;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        Parse.enableLocalDatastore(this);
        // set applicationId and server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        ParseObject.registerSubclass(Question.class);
        ParseObject.registerSubclass(NAUser.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("nothinganswered") // should correspond to APP_ID env variable
                .clientKey(null)
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

    public static ParseClient getParseClient() {
        return (ParseClient) ParseClient.getInstance(NothingAnsweredApplication.context);
    }

    public static FacebookClient getFacebookClient() {
        return (FacebookClient) FacebookClient.getInstance(NothingAnsweredApplication.context);
    }

    public static String getProfileImage(String id) {
        return "https://graph.facebook.com/" + id + "/picture?type=large";
    }

}
