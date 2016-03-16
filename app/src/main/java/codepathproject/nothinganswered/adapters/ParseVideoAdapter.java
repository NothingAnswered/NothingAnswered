package codepathproject.nothinganswered.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import codepathproject.nothinganswered.NothingAnsweredApplication;
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
        View view = inflater.inflate(R.layout.item_video_timeline, parent, false);
        GaffeVideoHolder gaffeVideoHolder = new GaffeVideoHolder(view);
        return gaffeVideoHolder;
    }

    @Override
    public void onBindViewHolder(GaffeVideoHolder holder, int position) {
        Video video = getItem(position);
        holder.loadDataIntoView(context, video);
    }

    public class GaffeVideoHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rivVideoProfilePic) RoundedImageView rivVideoProfilePic;
        @Bind(R.id.tvVideoQuestion) TextView tvVideoQuestion;
        @Bind(R.id.vpVideoTexture) VideoPlayerView vpVideoTexture;
        @Bind(R.id.ivVideoThumbnail) ImageView ivVideoThumbnail;

        public GaffeVideoHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }

        public void loadDataIntoView(Context context, Video video) {

            final ParseFile videoContent = (ParseFile)video.get(Video.VIDEO);
            String sender = video.get(Video.SENDER_ID).toString();
            Log.i("VIDEO", sender);

            //Profile Image
            rivVideoProfilePic.setImageResource(0);
            String profilePicUrl = NothingAnsweredApplication.getProfileImage(sender);
            Picasso.with(context).load(profilePicUrl).placeholder(R.drawable.ic_launcher).into(rivVideoProfilePic);

            //Question
            tvVideoQuestion.setText(video.get(Video.QUESTION).toString());

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
