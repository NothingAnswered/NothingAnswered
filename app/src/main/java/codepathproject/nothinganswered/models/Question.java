package codepathproject.nothinganswered.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by gpalem on 3/5/16.
 */
@ParseClassName("Question")
public class Question extends ParseObject {
    public static final String SENDER_ID = "senderId";
    public static final String QUESTION = "question";
    public static final String RECIPIENT_ID = "recipientId";
    public static final String LOCALVIDEOURL = "localVideoUrl";
    public static final String RESPONDED = "responded";
    public static final String THUMBMAIL = "thumbNail";
}
