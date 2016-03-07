package codepathproject.nothinganswered.clients;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import codepathproject.nothinganswered.R;
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

    public ParseFile createParseBlob(String name, byte[] blob) {
        // Create the ParseFile
        ParseFile file = new ParseFile(name, blob);
        // Upload the image into Parse Cloud
        file.saveInBackground();
        return file;
    }

    public ParseObject createQuestionObject(String question, ParseFile video, List<String> recipients) {
        ParseObject qObject = ParseObject.create("Question");
        qObject.put(Question.SENDER_ID, ParseUser.getCurrentUser().getObjectId());
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
        file.saveInBackground();

        return file;
    }

}
