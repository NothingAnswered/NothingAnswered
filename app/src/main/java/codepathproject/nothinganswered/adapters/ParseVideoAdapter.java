package codepathproject.nothinganswered.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQueryAdapter;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Video;

/**
 * Created by jnagaraj on 3/13/16.
 */
public class ParseVideoAdapter extends ParseRecyclerQueryAdapter<Video, ParseVideoAdapter.GaffeVideoHolder> {

    Context context;
    private Friends friends;
    private VideoPlayerManager videoPlayerManager;


    public ParseVideoAdapter(boolean hasStableIds) {

        super(Video.class, hasStableIds);
    }

    public ParseVideoAdapter(ParseQueryAdapter.QueryFactory<Video> factory, boolean hasStableIds) {

        super(factory, hasStableIds);

    }

    public void setVideoPlayerManager(VideoPlayerManager manager) {
        videoPlayerManager = manager;
    }

    @Override
    public GaffeVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        friends = Friends.getInstance();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_video_response, parent, false);
        GaffeVideoHolder gaffeVideoHolder = new GaffeVideoHolder(view);
        return gaffeVideoHolder;
    }

    @Override
    public void onBindViewHolder(GaffeVideoHolder holder, int position) {
        Video video = getItem(position);
        holder.loadDataIntoView(context, video);
    }

    public class GaffeVideoHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.vpVideoTexture) VideoPlayerView vpVideoTexture;
        @Bind(R.id.ivVideoThumbnail) ImageView ivVideoThumbnail;

        public GaffeVideoHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }

        public void loadDataIntoView(Context context, Video video) {

            final ParseFile videoContent = (ParseFile)video.get(Video.VIDEO);


            try {
                Glide.with(context).load(videoContent.getFile()).into(ivVideoThumbnail);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            vpVideoTexture.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener() {
                @Override
                public void onVideoPreparedMainThread() {
                    ivVideoThumbnail.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoCompletionMainThread() {
                    ivVideoThumbnail.setVisibility(View.VISIBLE);
                }

                @Override
                public void onVideoStoppedMainThread() {
                    ivVideoThumbnail.setVisibility(View.VISIBLE);
                }
            });


            ivVideoThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("VIDEO", "Clicked on " + videoContent.getUrl());
                    videoPlayerManager.resetMediaPlayer();
                    videoPlayerManager.playNewVideo(null, vpVideoTexture, videoContent.getUrl());
                }
            });

        }
        }
    }
