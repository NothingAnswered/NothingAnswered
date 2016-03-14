package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.activities.HomeScreenActivity;
import codepathproject.nothinganswered.adapters.CustomGridLayoutManager;
import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.models.Friends;

/**
 * Created by jnagaraj on 3/8/16.
 */
public abstract class TimelineFragment extends Fragment{

    public static String TAG = HomeScreenActivity.class.getSimpleName();

    //flag to check if the scrolling is disabled or enabled
    //because of camera preview running
    public boolean scrolling = true;

    public SwipeRefreshLayout swipeContainer;
    public ParseClient parseClient;
    public FacebookClient facebookClient;
    public Friends friends;

    public RecyclerView rvResults;

    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 10000; // milliseconds

    Handler mHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            //populateTimeline();
            mHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
        parseClient = NothingAnsweredApplication.getParseClient();
        facebookClient = NothingAnsweredApplication.getFacebookClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.questions_recieved_list_jay, container, false);


        rvResults = (RecyclerView) view.findViewById(R.id.rvGaffes);
        final CustomGridLayoutManager linearLayoutManager = new CustomGridLayoutManager(this.getContext());
        rvResults.setLayoutManager(linearLayoutManager);

        swipeContainer = (SwipeRefreshLayout)view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadObjects();
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    public abstract void loadObjects();
}
