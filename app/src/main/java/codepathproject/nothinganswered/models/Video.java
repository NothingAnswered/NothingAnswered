package codepathproject.nothinganswered.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by gpalem on 3/5/16.
 */
@ParseClassName("Video")
public class Video extends ParseObject {
    public static final String SENDER_ID = "senderId";
    public static final String QUESTION = "question";
    public static final String VIDEO = "video";
    public static final String RECIPIENT_ID = "recipientId";
    public static final String LIKED = "liked";
}
