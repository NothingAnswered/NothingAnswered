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
        videoPlayerManager = NothingAnsweredApplication.mVideoPlayerManager;
    }

    @Override
    public GaffeVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        friends = Friends.getInstance();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_video, parent, false);
        GaffeVideoHolder gaffeVideoHolder = new GaffeVideoHolder(view);
        return gaffeVideoHolder;
    }

    @Override
    public void onBindViewHolder(GaffeVideoHolder holder, int position) {
        Video video = getItem(position);
        holder.loadDataIntoView(context, video);
    }

    public class GaffeVideoHolder extends RecyclerView.ViewHolder {
        TextView gaffeCardQuestion;
        RoundedImageView gaffeCardProfilePictureUrl;
        ImageView gaffeCardLike;
        ImageView ivVideoThumbnail;
        VideoPlayerView vpVideoTexture;

        public GaffeVideoHolder(final View itemView) {
            super(itemView);

            gaffeCardQuestion = (TextView) itemView.findViewById(R.id.gaffeCardQuestion);
            gaffeCardProfilePictureUrl = (RoundedImageView) itemView.findViewById(R.id.gaffeCardProfilePictureUrl);
            gaffeCardLike = (ImageView) itemView.findViewById(R.id.gaffeCardLike);
            ivVideoThumbnail = (ImageView) itemView.findViewById(R.id.ivVideoThumbnail);
            vpVideoTexture = (VideoPlayerView) itemView.findViewById(R.id.vpVideoTexture);

            gaffeCardProfilePictureUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gaffeCardLike.setImageResource(R.drawable.heart);
                    updateLikeToVideo(getLayoutPosition());
                }
            });

            gaffeCardLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gaffeCardLike.setImageResource(R.drawable.heart);
                    updateLikeToVideo(getLayoutPosition());
                }
            });

            vpVideoTexture.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener() {
                @Override
                public void onVideoPreparedMainThread() {
                    gaffeCardQuestion.setVisibility(View.INVISIBLE);
                    gaffeCardLike.setVisibility(View.INVISIBLE);
                    gaffeCardProfilePictureUrl.setVisibility(View.INVISIBLE);
                    ivVideoThumbnail.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoCompletionMainThread() {
                    gaffeCardQuestion.setVisibility(View.VISIBLE);
                    gaffeCardLike.setVisibility(View.VISIBLE);
                    gaffeCardProfilePictureUrl.setVisibility(View.VISIBLE);
                    ivVideoThumbnail.setVisibility(View.VISIBLE);
                }

                @Override
                public void onVideoStoppedMainThread() {
                    gaffeCardQuestion.setVisibility(View.VISIBLE);
                    gaffeCardLike.setVisibility(View.VISIBLE);
                    gaffeCardProfilePictureUrl.setVisibility(View.VISIBLE);
                    ivVideoThumbnail.setVisibility(View.VISIBLE);
                }
            });

            ivVideoThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Video video = getItem(getLayoutPosition());
                    final ParseFile videoContent = (ParseFile) video.get(Video.VIDEO);
                    videoPlayerManager.resetMediaPlayer();
                    videoPlayerManager.playNewVideo(null, vpVideoTexture, videoContent.getUrl());
                }
            });
        }

        public void updateLikeToVideo(int position) {
            Video video = getItem(position);
            if (video.get(Video.LIKED) == null || video.get(Video.LIKED).toString().equals("false")) {
                Log.i("LIKE", "SENT");
                video.put(Video.LIKED, "true");
                video.saveInBackground();
            }
        }

        public void loadDataIntoView(Context context, Video video) {

            final ParseFile videoContent = (ParseFile) video.get(Video.VIDEO);
            String sender = video.get(Video.SENDER_ID).toString();
            Log.i("VIDEO", sender);

            //Profile Image
            gaffeCardProfilePictureUrl.setImageResource(0);
            String profilePicUrl = NothingAnsweredApplication.getProfileImage(sender);
            Picasso.with(context).load(profilePicUrl).placeholder(R.drawable.ic_launcher).into(gaffeCardProfilePictureUrl);

            //Question
            gaffeCardQuestion.setText(video.get(Video.QUESTION).toString());

            try {
                ivVideoThumbnail.setImageResource(0);
                Glide.with(context).load(videoContent.getFile()).into(ivVideoThumbnail);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (video.get(Video.LIKED) != null && video.get(Video.LIKED).toString().equals("true")) {
                gaffeCardLike.setImageResource(R.drawable.heart);
            }
            else {
                gaffeCardLike.setImageResource(R.drawable.heart_white);
            }
        }
    }
}
