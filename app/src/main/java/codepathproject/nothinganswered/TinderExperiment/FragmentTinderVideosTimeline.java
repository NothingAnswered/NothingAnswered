package codepathproject.nothinganswered.TinderExperiment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.tindercard.SwipeFlingAdapterView;
import codepathproject.nothinganswered.views.AutoFitTextureView;

/**
 * Created by jnagaraj on 3/14/16.
 */
public class FragmentTinderVideosTimeline extends Fragment {

    private SwipeFlingAdapterView flingContainer;
    private TinderVideoAdapter videoAdapter;
    private ArrayList<TinderVideo> videos;
    private GaffeVideoPlayer videoPlayer;

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
        /*videos.add(new TinderVideo("video_1.mp4", this.getContext()));
        videos.add(new TinderVideo("video_1.mp4", this.getContext()));
        videos.add(new TinderVideo("video_1.mp4", this.getContext()));*/

        videoPlayer = new GaffeVideoPlayer(this.getActivity());

        videoAdapter = new TinderVideoAdapter(videos, this.getContext());

        flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);

        flingContainer.setAdapter(videoAdapter);

        videoAdapter.notifyDataSetChanged();

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                videos.remove(0);
                videoAdapter.notifyDataSetChanged();
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

            }

            @Override
            public void onRightCardExit(Object dataObject) {

                //Data data = al.remove(0);
                //al.add(data);
                //al.set(lastIndex, data);
                videos.remove(0);
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {


            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }


        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

                View view = flingContainer.getSelectedView();
                performActionsOnItemClick(view, flingContainer.getFirstVisiblePosition());

                view.findViewById(R.id.background).setAlpha(0);

                videoAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void performActionsOnItemClick(View view, final int position) {

        Toast.makeText(getActivity(), "position " + position, Toast.LENGTH_SHORT).show();
        final TextureView textureView = (TextureView) view.findViewById(R.id.texture);
        final ImageView playIcon = (ImageView)view.findViewById(R.id.ivPlayIcon);

        videoPlayer.StartMediaPlayer(textureView);

        if(videoPlayer.isVideoPlaying()) {
            videoPlayer.stop();
        }else{
            videoPlayer.play(videos.get(position));
        }

        textureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoPlayer.isVideoPlaying()) {
                    videoPlayer.stop();

                    playIcon.setVisibility(View.VISIBLE);
                    textureView.setVisibility(View.INVISIBLE);

                    Log.i("VIDEO", "Tapped on video texture");
                }
            }
        });

        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoPlayer.isVideoPlaying()) {
                    videoPlayer.play(videos.get(position));

                    playIcon.setVisibility(View.INVISIBLE);
                    textureView.setVisibility(View.VISIBLE);

                    Log.i("VIDEO", "Tapped on video texture");
                }
            }
        });

    }

}
