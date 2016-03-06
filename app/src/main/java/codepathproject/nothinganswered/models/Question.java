package codepathproject.nothinganswered.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by gpalem on 3/5/16.
 */
@ParseClassName("Question")
public class Question extends ParseObject {
    public static final String QUESTION_SENDER_ID_KEY = "userId";
    public static final String QUESTION = "question";
    public static final String VIDEO_KEY = "video";
    public static final String RECIPIENT_ID_KEY = "userId";

    public static String getQuestionSenderIdKey() {
        return QUESTION_SENDER_ID_KEY;
    }

    public static String getQUESTION() {
        return QUESTION;
    }

    public static String getVideoKey() {
        return VIDEO_KEY;
    }

    public static String getRecipientIdKey() {
        return RECIPIENT_ID_KEY;
    }
}
