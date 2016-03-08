package codepathproject.nothinganswered.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by gpalem on 3/6/16.
 */
public class Friends {
    private static String TAG = Friends.class.getSimpleName();
    private static Friends mInstance = null;

    public static ArrayList<Friend> friends;

    private Friends() {
        friends = new ArrayList<>();
    }

    public static Friends getInstance() {
        if (mInstance == null) {
            mInstance = new Friends();
        }
        return mInstance;
    }

    public class Friend {
        public String facebookId;
        public String firstName;
        public String lastName;
        public Friend(String facebookId, String firstName, String lastName) {
            this.facebookId = facebookId;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public ArrayList<String> getFacebookIds() {
        if (friends == null)
            return null;
        ArrayList<String> facebookIds = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            facebookIds.add(friends.get(i).facebookId);
        }
        return facebookIds;
    }

    public ArrayList<String> getNames() {
        if (friends == null)
            return null;
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            names.add(friends.get(i).firstName + " " + friends.get(i).lastName);
        }
        return names;
    }

    public void fromJSONArray(JSONArray jsonArray) {
        if (friends == null) {
            friends = new ArrayList<>();
        }
        friends.clear(); //TODO dummy thing I guess
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String id = jsonArray.getJSONObject(i).getString("id");
                String firstName = jsonArray.getJSONObject(i).getString("first_name");
                String lastName = jsonArray.getJSONObject(i).getString("last_name");
                Log.i(TAG, "id " + id);
                Log.i(TAG, "first_name " + firstName);
                Log.i(TAG, "last_name " + lastName);
                friends.add(new Friend(id, firstName, lastName));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}