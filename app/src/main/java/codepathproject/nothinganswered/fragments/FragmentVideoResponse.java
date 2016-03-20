package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.ParseVideoAdapter;
import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Video;

public class FragmentVideoResponse extends Fragment {


    private ParseVideoAdapter videoAdapter;
    public SwipeRefreshLayout swipeContainer;
    public ParseClient parseClient;
    public FacebookClient facebookClient;
    public Friends friends;

    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 10000; // milliseconds

    public RecyclerView rvResults;

    public static FragmentVideoResponse newInstance (int page)
    {
        FragmentVideoResponse fragment = new FragmentVideoResponse();
        return fragment;
    }

    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseClient = NothingAnsweredApplication.getParseClient();
        facebookClient = NothingAnsweredApplication.getFacebookClient();

        createVideoAdapter();
    }

    private void createVideoAdapter() {

        ParseQueryAdapter.QueryFactory<Video> factory = new ParseQueryAdapter.QueryFactory<Video>() {

            public ParseQuery<Video> create() {

                ParseQuery<Video> query = ParseQuery.getQuery(Video.class);
                query.whereEqualTo(Video.RECIPIENT_ID, Friends.myId);
                query.orderByDescending("createdAt");

                return query;
            }
        };
        videoAdapter = new ParseVideoAdapter(factory, true);
        videoAdapter.setVideoPlayerManager(mVideoPlayerManager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.questions_recieved_list_jay, container, false);

        rvResults = (RecyclerView) view.findViewById(R.id.rvGaffes);
        rvResults.setAdapter(videoAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        rvResults.setLayoutManager(layoutManager);
        videoAdapter.notifyDataSetChanged();

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

    public void loadObjects() {
        videoAdapter.loadObjects();
    }
}
