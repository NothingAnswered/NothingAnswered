package codepathproject.nothinganswered.clients;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.NAUser;
import codepathproject.nothinganswered.models.Question;

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

    public void updateProfileInfo(String id, String firstName, String lastName, String email) {
        ParseObject userInfo = ParseObject.create("NAUser");
        userInfo.put(NAUser.CURRENT_USER_ID, ParseUser.getCurrentUser().getObjectId());
        userInfo.put(NAUser.FACEBOOK_ID, id);
        userInfo.put(NAUser.FIRST_NAME, firstName);
        userInfo.put(NAUser.LAST_NAME, lastName);
        userInfo.put(NAUser.EMAIL, email);
        userInfo.put(NAUser.PROFILE_PICTURE, "");
        userInfo.put(NAUser.FRIENDS, Arrays.asList(""));
        userInfo.saveInBackground();
    }

    public ParseFile createParseBlob(String name, byte[] blob) {
        // Create the ParseFile
        ParseFile file = new ParseFile(name, blob);
        // Upload the image into Parse Cloud
        file.saveInBackground();
        return file;
    }

    public ParseObject createQuestionObject(String question, ParseFile video, List<String> recipients) {
        ParseObject qObject = ParseObject.create("Question");
        qObject.put(Question.SENDER_ID, Friends.myId);
        qObject.put(Question.QUESTION, question);
        if (video != null) {
            qObject.put(Question.VIDEO, video);
        }
        else {
            qObject.put(Question.VIDEO, "");
        }
        qObject.put(Question.RECIPIENTS_ID, recipients);
        return qObject;
    }

    public ParseQuery<Question> getQuestionQuery(ArrayList<String> columns, int limit) {
        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        if (columns != null) {
            query.selectKeys(columns);
        }
        // Configure limit and sort order
        query.setLimit(limit);
        query.orderByAscending("createdAt");
        return query;
    }

    public ParseQuery<Question> getQuestionTimeline(String facebookId, int limit) {
        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        if (facebookId != null) {
            query.whereContains(Question.RECIPIENTS_ID, facebookId);
            // Configure limit and sort order
            query.setLimit(limit);
            query.orderByAscending("createdAt");
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

    public void sendVideoResponse(String recipientId, String question, File video) {
        ParseObject rObject = ParseObject.create("Question");
        rObject.put(Question.SENDER_ID, "012345"); //TODO change
        rObject.put(Question.QUESTION, question);
        rObject.put(Question.VIDEO, videoToParseFile(video));
        //rObject.put(Question.RECIPIENTS_ID, Arrays.asList(recipientId));
        rObject.put(Question.RECIPIENTS_ID, Arrays.asList("012345"));
        rObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ParseFile videoToParseFile(File video) {
        byte[] bFile = new byte[(int) video.length()];

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
    public ParseFile parseTemplateFile() {
        // Locate the image in res > drawable-hdpi
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);
        // Convert it to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        // Create the ParseFile
        ParseFile file = createParseBlob("androidbegin.png", image);
        return file;
    }

}
