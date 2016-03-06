package codepathproject.nothinganswered.clients;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gpalem on 3/5/16.
 */
public class FacebookClient {
    private static String TAG = FacebookClient.class.getSimpleName();
    private static FacebookClient mInstance = null;
    private Context context;

    private FacebookClient(Context context) {
        this.context = context;
    }

    public static FacebookClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FacebookClient(context);
        }
        return mInstance;
    }


    public void getInfo() {
        GraphRequest req = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String firstName = response.getJSONObject().getString("first_name");
                    String lastName = response.getJSONObject().getString("last_name");
                    String email = response.getJSONObject().getString("email");
                    Log.i(TAG, firstName);
                    Log.i(TAG, lastName);
                    Log.i(TAG, email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("fields", "first_name,last_name,email");
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

}
