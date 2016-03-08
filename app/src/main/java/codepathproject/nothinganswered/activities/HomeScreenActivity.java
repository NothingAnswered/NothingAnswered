package codepathproject.nothinganswered.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.GaffeFragmentPagerAdapter;
import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.fragments.QuestionFragment;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Question;

public class HomeScreenActivity extends AppCompatActivity {

    private static String TAG = HomeScreenActivity.class.getSimpleName();

    private ViewPager vpPager;
    private FragmentManager fragmentManager;

    private ParseClient parseClient;
    private FacebookClient facebookClient;
    private Friends friends;

    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 10000; // milliseconds

    Handler mHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            getQuestionsTimeline();
            mHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
        parseClient = NothingAnsweredApplication.getParseClient();
        facebookClient = NothingAnsweredApplication.getFacebookClient();

        // Get the viewpager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        // set the viewpager adapter for the pager
        vpPager.setAdapter(new GaffeFragmentPagerAdapter(getSupportFragmentManager()));
        // find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);

        //Fragment manager
        fragmentManager = getSupportFragmentManager();
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem composeQuestion = (MenuItem) menu.findItem(R.id.miCompose);
        composeQuestion.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showQuestionDialog();
                return true;
            }
        });
        return true;
    }

    public void showQuestionDialog() {
        QuestionFragment questionFragment = new QuestionFragment();
        questionFragment.show(fragmentManager, "fragment_send_question");
    }

    private void getQuestionsTimeline() {

        Log.i("REFRESH", "IN REFRESH");
        // Construct query to execute
        final ParseQuery<Question> query = parseClient.getQuestionTimeline(Friends.myId, 5);
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Question>() {
            public void done(List<Question> messages, ParseException e) {
                if (e == null) {
                    if (messages != null && messages.size() > 0) {
                        for (int i = 0; i < messages.size(); i++) {
                            Question question = messages.get(i);
                            friends = Friends.getInstance();
                            String sender = question.get(Question.SENDER_ID).toString();
                            Log.i(TAG, question.get(Question.QUESTION).toString());
                            Log.i(TAG, friends.getNameFromId(sender));
                        }
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });

    }

}
