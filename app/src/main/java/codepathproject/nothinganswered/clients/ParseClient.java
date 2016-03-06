package codepathproject.nothinganswered.clients;

import android.content.Context;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import codepathproject.nothinganswered.models.Question;

/**
 * Created by gpalem on 3/5/16.
 */
public class ParseClient {
    private static ParseClient mInstance = null;
    private Context context;

    private ParseClient(Context context) {
        this.context = context;
    }

    public static ParseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ParseClient(context);
        }
        return mInstance;
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
        qObject.put(Question.QUESTION_SENDER_ID_KEY, "Gangi");
        qObject.put(Question.QUESTION, "Who am I?");
        qObject.put(Question.VIDEO_KEY, video);
        qObject.put(Question.RECIPIENT_ID_KEY, "Palem");
        return qObject;
    }

    public ParseQuery<Question> getQuery(ArrayList<String> columns, int limit) {
        ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
        if (columns != null) {
            query.selectKeys(columns);
        }
        // Configure limit and sort order
        query.setLimit(limit);
        query.orderByAscending("createdAt");
        return query;
    }

}
