package codepathproject.nothinganswered.activities;

import android.content.Intent;
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
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
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
            //refreshMessages();
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

    public void clickLogin(View view) {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user == null) { //Cancelled
                    Log.i(TAG, "CANCELLED PARSE");
                    e.printStackTrace();
                } else if (user.isNew()) {
                    //First time user
                    Log.i(TAG, "FIRST PARSE");
                    facebookClient.getInfo();
                    facebookClient.getFriendList();
                    getHomeActivity();
                } else {
                    //Existing user
                    Log.i(TAG, "EXISTING PARSE");
                    facebookClient.getFriendList();
                    //facebookClient.postOnMyWall();
                    getHomeActivity();
                }
            }
        });
    }

    public void getHomeActivity() {
        Intent i = new Intent(LoginActivity.this, HomeScreenActivity.class);
        startActivity(i);
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        Log.i("REFRESH", "IN REFRESH");
        // Construct query to execute
        ParseQuery<Question> query = parseClient.getQuestionQuery(null, 1);
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Question>() {
            public void done(List<Question> messages, ParseException e) {
                if (e == null) {
                    if (messages != null && messages.size() > 0) {
                        Question video = (Question) messages.get(0);
                        ParseFile parseFile = video.getParseFile(Question.VIDEO);
                        ivImage.setParseFile(parseFile);
                        ivImage.loadInBackground();
                        tvQuestion.setText(video.get(Question.QUESTION).toString());
                        tvSender.setText(video.get(Question.SENDER_ID).toString());
                        tvReceiver.setText(video.get(Question.RECIPIENTS_ID).toString());
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }


    private void getQuestionsTimeline() {

        Log.i("REFRESH", "IN REFRESH");
        // Construct query to execute
        ParseQuery<Question> query = parseClient.getQuestionTimeline("my id", 5);
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Question>() {
            public void done(List<Question> messages, ParseException e) {
                if (e == null) {
                    if (messages != null && messages.size() > 0) {
                        Question video = (Question) messages.get(0);
                        ParseFile parseFile = video.getParseFile(Question.VIDEO);
                        ivImage.setParseFile(parseFile);
                        ivImage.loadInBackground();
                        tvQuestion.setText(video.get(Question.QUESTION).toString());
                        tvSender.setText(video.get(Question.SENDER_ID).toString());
                        tvReceiver.setText(video.get(Question.RECIPIENTS_ID).toString());
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
