package codepathproject.nothinganswered.clients;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.NAUser;
import codepathproject.nothinganswered.models.Question;
import codepathproject.nothinganswered.models.Video;

/**
 * Created by gpalem on 3/5/16.
 */
public class ParseClient {
    private static ParseClient mInstance = null;
    private Context context;
    private FacebookClient facebookClient;

    private ParseClient(Context context) {
        this.context = context;
    }

    public static ParseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ParseClient(context);
        }
        return mInstance;
    }

    public void setFacebookClient(FacebookClient client) {
        facebookClient = client;
    }

    public void createNAUserInfo(String id, String firstName, String lastName, String email) {
        ParseObject userInfo = ParseObject.create("NAUser");
        userInfo.put(NAUser.CURRENT_USER_ID, ParseUser.getCurrentUser().getObjectId());
        userInfo.put(NAUser.FACEBOOK_ID, id);
        userInfo.put(NAUser.FIRST_NAME, firstName);
        userInfo.put(NAUser.LAST_NAME, lastName);
        userInfo.put(NAUser.EMAIL, email);
        userInfo.put(NAUser.PROFILE_PICTURE, "");
        userInfo.put(NAUser.FRIENDS, Arrays.asList(""));
        userInfo.pinInBackground(id);
        userInfo.saveInBackground();
        Log.i("PIN", "CREATE " + firstName);
    }

    public void updateNAUserInfo(final String id, final String firstName, final String lastName, final String email) {
        ParseQuery<NAUser> query = ParseQuery.getQuery(NAUser.class);
        query.fromLocalDatastore();
        query.whereEqualTo(NAUser.CURRENT_USER_ID, ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<NAUser>() {
            @Override
            public void done(NAUser object, ParseException e) {
                if (e == null) {
                    object.put(NAUser.FACEBOOK_ID, id);
                    object.put(NAUser.FIRST_NAME, firstName);
                    object.put(NAUser.LAST_NAME, lastName);
                    object.put(NAUser.EMAIL, email);
                    object.saveInBackground();
                    object.pinInBackground(id);
                    Log.i("PIN", "UPDATE " + firstName);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateFriendsList() {
        ParseQuery<NAUser> query = ParseQuery.getQuery(NAUser.class);
        query.fromLocalDatastore();
        query.whereEqualTo(NAUser.CURRENT_USER_ID, ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<NAUser>() {
            @Override
            public void done(NAUser object, ParseException e) {
                if (e == null) {
                    final Friends friends = Friends.getInstance();
                    object.put(NAUser.FRIENDS, friends.getFacebookIds());
                    object.saveInBackground();
                    object.pinInBackground(Friends.myId);
                    Log.i("PIN", "UPDATE FRIENDSID " + friends.getFacebookIds());
                    updateLocalStoreFriendsList();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateLocalStoreFriendsList() {
        final List<String> facebookIds = Friends.getInstance().getFacebookIds();
        for (int i = 0; i < facebookIds.size(); i++) {
            ParseQuery<NAUser> query = ParseQuery.getQuery(NAUser.class);
            query.whereEqualTo(NAUser.FACEBOOK_ID, facebookIds.get(i));
            query.getFirstInBackground(new GetCallback<NAUser>() {
                @Override
                public void done(NAUser object, ParseException e) {
                    if (e == null) {
                        Log.i("PIN", "UPDATE FRIENDS " + object.get(NAUser.FIRST_NAME).toString());
                        object.pinInBackground(object.get(NAUser.FACEBOOK_ID).toString());
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void sendVideoResponse(String recipientId, String question, File video) {
        ParseObject rObject = ParseObject.create("Video");
        rObject.put(Video.SENDER_ID, Friends.myId); //TODO change
        rObject.put(Video.QUESTION, question);
        rObject.put(Video.VIDEO, videoToParseFile(video));
        rObject.put(Video.RECIPIENT_ID, recipientId);
        rObject.put(Video.LIKED, "false");
        Log.i("VIDEO", Friends.myId + " " + recipientId + " " + question);
        rObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ParseFile createParseBlob(String name, byte[] blob) {
        // Create the ParseFile
        ParseFile file = new ParseFile(name, blob);
        // Upload the image into Parse Cloud
        try {
            file.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return file;
    }

    public ParseFile createQuestionThumbNail(Bitmap thumbNail) {
        if (thumbNail == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        thumbNail.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        ParseFile parseFile = new ParseFile("bgImage", image);
        parseFile.saveInBackground();
        return parseFile;
    }

    public void sendQuestionObject(String question, String recipient, ParseFile thumbNail) {
        ParseObject qObject = ParseObject.create("Question");
        qObject.put(Question.SENDER_ID, Friends.myId);
        qObject.put(Question.QUESTION, question);
        qObject.put(Question.RECIPIENT_ID, recipient);
        qObject.put(Question.RESPONDED, "false");
        if (thumbNail != null) {
            qObject.put(Question.THUMBMAIL, thumbNail);
        }
        qObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendQuestionNotification(String question, String facebookId) {
        // Create our Installation query
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo(NAUser.FACEBOOK_ID, facebookId);

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage(question);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("PUSH", "Push successful");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public ParseQuery<Question> getQuestionQuery(ArrayList<String> columns, int limit) {
        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        if (columns != null) {
            query.selectKeys(columns);
        }
        // Configure limit and sort order
        query.setLimit(limit);
        query.orderByDescending("createdAt");
        return query;
    }

    public ParseQuery<Video> getVideoQuery(String question) {
        ParseQuery<Video> query = ParseQuery.getQuery(Video.class);
        query.whereEqualTo(Video.SENDER_ID, Friends.myId);
        query.whereEqualTo(Video.QUESTION, question);
        query.orderByDescending("createdAt");
        return query;
    }

    public ParseQuery<Question> getQuestionTimeline(String facebookId, int limit) {
        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        if (facebookId != null) {
            query.whereEqualTo(Question.RECIPIENT_ID, facebookId);
            // Configure limit and sort order
            query.setLimit(limit);
            query.orderByDescending("createdAt");
            return query;
        }

        return null;
    }

    public ParseQuery<NAUser> getNAUserQuery(ArrayList<String> columns, int limit) {
        ParseQuery<NAUser> query = ParseQuery.getQuery(NAUser.class);
        if (columns != null) {
            query.selectKeys(columns);
        }
        // Configure limit and sort order
        query.setLimit(limit);
        query.orderByAscending("createdAt");
        return query;
    }

    public ParseFile videoToParseFile(File video) {
        byte[] bFile = new byte[(int) video.length()];
        Log.i("VIDEO", "Length " + video.length());
        if (video.exists()) {
            Log.i("VIDEO", "file exists " + video);
        }

        try {
            //convert file into array of bytes
            FileInputStream fileInputStream = new FileInputStream(video);
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createParseBlob("response.3gpp", bFile);
    }

}
