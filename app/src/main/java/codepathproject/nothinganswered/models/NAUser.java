package codepathproject.nothinganswered.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by gpalem on 3/6/16.
 */
@ParseClassName("NAUser")
public class NAUser extends ParseObject{
    public static final String CURRENT_USER_ID = "userId";
    public static final String FACEBOOK_ID = "facebookId";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String PROFILE_PICTURE = "profile_pic";
    public static final String FRIENDS = "friends";
}
