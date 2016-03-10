package codepathproject.nothinganswered.clients;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.NAUser;

/**
 * Created by gpalem on 3/5/16.
 */
public class FacebookClient {
    private static String TAG = FacebookClient.class.getSimpleName();
    private static FacebookClient mInstance = null;
    private Context context;
    private ParseClient parseClient;

    private FacebookClient(Context context) {
        this.context = context;
    }

    public static FacebookClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FacebookClient(context);
        }
        return mInstance;
    }

    public void setParseClient(ParseClient client) {
        parseClient = client;
    }

    public void getFBId() {
        GraphRequest req = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = response.getJSONObject().getString("id");
                    Log.i(TAG, "MyID " + id);
                    Friends.myId = id;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("fields", "id");
        req.setParameters(bundle);
        req.executeAsync();
    }

    public void getInfo() {
        GraphRequest req = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = response.getJSONObject().getString("id");
                    String firstName = response.getJSONObject().getString("first_name");
                    String lastName = response.getJSONObject().getString("last_name");
                    String email = response.getJSONObject().getString("email");
                    Log.i(TAG, ParseUser.getCurrentUser().getObjectId());
                    Log.i(TAG, id);
                    Log.i(TAG, firstName);
                    Log.i(TAG, lastName);
                    Log.i(TAG, email);
                    Friends.setMyModelInfo(id, firstName, lastName);
                    parseClient.updateNAUserInfo(id, firstName, lastName, email);
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

    public void postOnMyWall() {
        GraphRequest req = GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(), "/me/feed", null, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                Log.i(TAG, "Success on posting");
            }
        });
        Bundle bundle = req.getParameters();
        bundle.putString("message", "bots up!");
        req.setParameters(bundle);
        req.executeAsync();
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
                                object.put(NAUser.FRIENDS, friends.getFacebookIds());
                                object.saveInBackground();
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

}
