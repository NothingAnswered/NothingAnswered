package codepathproject.nothinganswered.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gpalem on 3/6/16.
 */
public class Friends {
    private static String TAG = Friends.class.getSimpleName();
    private static Friends mInstance = null;

    public static String myId;
    public static ArrayList<Friend> friends;
    public static HashMap<String, String> facebookIds;
    public static HashMap<String, String> facebookNames;

    private Friends() {
        friends = new ArrayList<>();
        facebookIds = new HashMap<>();
        facebookNames = new HashMap<>();
    }

    public static Friends getInstance() {
        if (mInstance == null) {
            mInstance = new Friends();
        }
        return mInstance;
    }

    private class Friend {
        public String facebookId;
        public String firstName;
        public String lastName;
        public Friend(String facebookId, String firstName, String lastName) {
            this.facebookId = facebookId;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public String getIdFromName(String name) {
        return facebookIds.get(name);
    }

    public String getNameFromId(String id) {
        return facebookNames.get(id);
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
        if (facebookIds == null) {
            facebookIds = new HashMap<>();
        }
        if (facebookNames == null) {
            facebookNames = new HashMap<>();
        }
        friends.clear(); //TODO dummy thing I guess
        facebookIds.clear(); //TODO dummy thing I guess
        facebookNames.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String id = jsonArray.getJSONObject(i).getString("id");
                String firstName = jsonArray.getJSONObject(i).getString("first_name");
                String lastName = jsonArray.getJSONObject(i).getString("last_name");
                Log.i(TAG, "id " + id);
                Log.i(TAG, "first_name " + firstName);
                Log.i(TAG, "last_name " + lastName);
                facebookIds.put(firstName + " " + lastName, id);
                facebookNames.put(id, firstName + " " + lastName);
                friends.add(new Friend(id, firstName, lastName));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}