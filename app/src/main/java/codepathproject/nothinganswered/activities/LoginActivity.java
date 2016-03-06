package codepathproject.nothinganswered.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.models.Question;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private CallbackManager callbackManager;

    private ParseClient parseClient;
    private FacebookClient facebookClient;

    @Bind(R.id.tvQuestion) TextView tvQuestion;
    @Bind(R.id.tvSender) TextView tvSender;
    @Bind(R.id.tvReceiver) TextView tvReceiver;
    @Bind(R.id.ivImage) ParseImageView ivImage;

    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 10000; // milliseconds

    Handler mHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            mHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    ArrayList<String> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();

        ButterKnife.bind(this);
        mHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
        parseClient = NothingAnsweredApplication.getParseClient();
        facebookClient = NothingAnsweredApplication.getFacebookClient();
        permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_friends");
    }

    public void parseUploadExample() {
        // Locate the image in res > drawable-hdpi
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        // Convert it to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        // Create the ParseFile
        ParseFile file = parseClient.createParseBlob("androidbegin.png", image);

        ParseObject qObject = parseClient.createQuestionObject("Who am I?", file, Arrays.asList("Palem"));
        // Create the class and the columns
        qObject.saveInBackground();
        refreshMessages();
    }

    public void clickLogin(View view) {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user == null) { //Cancelled
                    Log.i(TAG, "CANCELLED PARSE");
                } else if (user.isNew()) {
                    //First time user
                    Log.i(TAG, "FIRST PARSE");
                    facebookClient.getInfo();
                } else {
                    //Existing user
                    Log.i(TAG, "EXISTING PARSE");
                    facebookClient.getInfo();
                    facebookClient.postOnMyWall();
                }
            }
        });
    }
    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        Log.i("REFRESH", "IN REFRESH");
        // Construct query to execute
        ParseQuery<Question> query = parseClient.getQuery(null, 1);
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Question>() {
            public void done(List<Question> messages, ParseException e) {
                if (e == null) {
                    if (messages != null && messages.size() > 0) {
                        Question video = (Question) messages.get(0);
                        ParseFile parseFile = video.getParseFile(Question.VIDEO_KEY);
                        ivImage.setParseFile(parseFile);
                        ivImage.loadInBackground();
                        tvQuestion.setText(video.get(Question.QUESTION).toString());
                        tvSender.setText(video.get(Question.QUESTION_SENDER_ID_KEY).toString());
                        tvReceiver.setText(video.get(Question.RECIPIENT_ID_KEY).toString());
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }
}
