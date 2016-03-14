package codepathproject.nothinganswered.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.NAUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private ParseUser currentUser;
    private ParseClient parseClient;
    private FacebookClient facebookClient;

    ArrayList<String> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        parseClient = NothingAnsweredApplication.getParseClient();
        facebookClient = NothingAnsweredApplication.getFacebookClient();
        permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_friends");
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            //Load Friends class from local datastore to get going
            //Also the base timeline
            Log.i(TAG, "Auto Login");
            loadParseInfoSelf();
        } else {
            //Login User, check first time user and upload results
            // User clicked to log in.
            Log.i(TAG, "Logged out user");
            ParseLoginBuilder loginBuilder = new ParseLoginBuilder(LoginActivity.this);
            startActivityForResult(loginBuilder.build(), 0);
        }
    }

    public void firstTimeUser() {
        GraphRequest req = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = response.getJSONObject().getString("id");
                    String firstName = response.getJSONObject().getString("first_name");
                    String lastName = response.getJSONObject().getString("last_name");
                    String email = response.getJSONObject().getString("email");
                    Friends.setMyModelInfo(id, firstName, lastName);
                    parseClient.createNAUserInfo(id, firstName, lastName, email);
                    Log.i(TAG, "First time user");
                    getFriendList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("fields", "id,first_name,last_name,email");
        req.setParameters(bundle);
        req.executeAsync();
    }

    public void loadParseInfoSelf() {
        ParseQuery<NAUser> query = ParseQuery.getQuery(NAUser.class);
        query.whereEqualTo(NAUser.CURRENT_USER_ID, ParseUser.getCurrentUser().getObjectId());
        query.fromLocalDatastore();
        query.getFirstInBackground(new GetCallback<NAUser>() {
            @Override
            public void done(NAUser object, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Load parse info self");
                    Friends.loadNAUser(object);
                    facebookClient.getInfo();
                    facebookClient.getFriendList();
                    getHomeActivity();
                } else {
                    Log.i(TAG, "Load parse info self first time user");
                    firstTimeUser();
                }

            }
        });
    }

    public void getFriendList() {
        GraphRequest req = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {

            @Override
            public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                if (response.getError() == null) {
                    final Friends friends = Friends.getInstance();
                    friends.fromJSONArray(jsonArray);
                    //Update friends list user Object
                    ParseQuery<NAUser> query = ParseQuery.getQuery(NAUser.class);
                    query.whereEqualTo(NAUser.CURRENT_USER_ID, ParseUser.getCurrentUser().getObjectId());
                    query.getFirstInBackground(new GetCallback<NAUser>() {
                        @Override
                        public void done(NAUser object, ParseException e) {
                            if (e == null) {
                                Log.i(TAG, "Get friends list");
                                object.put(NAUser.FRIENDS, friends.getFacebookIds());
                                object.saveInBackground();
                                getHomeActivity();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Log.i(TAG, response.getError().getErrorMessage());
                }
            }
        });
        Bundle bundle = req.getParameters();
        bundle.putString("fields", "id,first_name,last_name");
        req.executeAsync();
    }

    public void getHomeActivity() {
        NothingAnsweredApplication.setParseInstallationObject();
        Log.i(TAG, "Start home activity");
        Intent i = new Intent(LoginActivity.this, HomeScreenActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "On Activity Result");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }
}
