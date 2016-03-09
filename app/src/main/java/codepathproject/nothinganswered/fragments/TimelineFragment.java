package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.activities.HomeScreenActivity;
import codepathproject.nothinganswered.adapters.GaffeRecyclerAdapter;
import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Gaffe;

/**
 * Created by jnagaraj on 3/8/16.
 */
public abstract class TimelineFragment extends Fragment{

    public static String TAG = HomeScreenActivity.class.getSimpleName();

    public ParseClient parseClient;
    public FacebookClient facebookClient;
    public Friends friends;
    //public SimpleCardStackAdapter cardStackAdapter;
    //public ArrayList<CardModel> cards;

    public GaffeRecyclerAdapter gaffeRecyclerAdapter;
    public ArrayList<Gaffe> mGaffes;

    public RecyclerView rvResults;

    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 10000; // milliseconds

    Handler mHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            populateTimeline();
            mHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
        parseClient = NothingAnsweredApplication.getParseClient();
        facebookClient = NothingAnsweredApplication.getFacebookClient();

        mGaffes = new ArrayList<>();
        gaffeRecyclerAdapter = new GaffeRecyclerAdapter(mGaffes);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.questions_recieved_list_jay, container, false);


        rvResults = (RecyclerView) view.findViewById(R.id.rvGaffes);
        rvResults.setAdapter(gaffeRecyclerAdapter);


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        rvResults.setLayoutManager(linearLayoutManager);

        return view;
    }

    public abstract void populateTimeline();
}
