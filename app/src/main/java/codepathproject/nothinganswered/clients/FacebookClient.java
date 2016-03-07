package codepathproject.nothinganswered.clients;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                    ParseObject userInfo = ParseObject.create("NAUser");
                    userInfo.put(NAUser.CURRENT_USER_ID, ParseUser.getCurrentUser().getObjectId());
                    userInfo.put(NAUser.FACEBOOK_ID, id);
                    userInfo.put(NAUser.FIRST_NAME, firstName);
                    userInfo.put(NAUser.LAST_NAME, lastName);
                    userInfo.put(NAUser.EMAIL, email);
                    userInfo.put(NAUser.PROFILE_PICTURE, "");
                    userInfo.put(NAUser.FRIENDS, Arrays.asList(""));
                    userInfo.saveInBackground();
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
                    final List<String> friendId = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            Log.i(TAG, "friendid " + jsonArray.getJSONObject(i).getString("id"));
                            Log.i(TAG, "friend " + jsonArray.getJSONObject(i).getString("name"));
                            friendId.add(jsonArray.getJSONObject(i).getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //Update friends list user Object
                    ParseQuery<NAUser> query = parseClient.getNAUserQuery(null, 10);
                    query.whereEqualTo(NAUser.CURRENT_USER_ID, ParseUser.getCurrentUser().getObjectId());
                    query.findInBackground(new FindCallback<NAUser>() {
                        @Override
                        public void done(List<NAUser> objects, ParseException e) {
                            if (e == null) {
                                Log.i(TAG, "NAUser Objects returned " + objects.size());
                                for (int i = 0; i < objects.size(); i++) {
                                    NAUser user = objects.get(i);
                                    user.put(NAUser.FRIENDS, friendId);
                                    try {
                                        user.save();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }
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
        req.executeAsync();
    }

}
