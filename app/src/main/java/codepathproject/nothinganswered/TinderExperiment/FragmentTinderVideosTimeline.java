package codepathproject.nothinganswered.TinderExperiment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.tindercard.SwipeFlingAdapterView;

/**
 * Created by jnagaraj on 3/14/16.
 */
public class FragmentTinderVideosTimeline extends Fragment {

    private SwipeFlingAdapterView flingContainer;
    private TinderVideoAdapter videoAdapter;
    private ArrayList<TinderVideo> videos;
    private MediaPlayer mediaPlayer;

    public static FragmentTinderVideosTimeline newInstance (int page)
    {
        FragmentTinderVideosTimeline fragment = new FragmentTinderVideosTimeline();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.tinder_fling_view, container, false);

        videos = new ArrayList<>();
        videos.add(new TinderVideo("video_1.mp4", this.getContext()));
        videos.add(new TinderVideo("video_1.mp4", this.getContext()));
        videos.add(new TinderVideo("video_1.mp4", this.getContext()));
        videos.add(new TinderVideo("video_1.mp4", this.getContext()));

        mediaPlayer = new MediaPlayer();
        videoAdapter = new TinderVideoAdapter(videos, this.getContext(), mediaPlayer);

        flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);

        flingContainer.setAdapter(videoAdapter);

        videoAdapter.notifyDataSetChanged();

        return view;
    }




}
