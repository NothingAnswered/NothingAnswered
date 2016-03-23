package codepathproject.nothinganswered.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.GetCallback;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.fragments.FragmentQuestionsReceived;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Question;
import codepathproject.nothinganswered.models.Video;

/**
 * Created by gpalem on 3/12/16.
 */
public class ParseQuestionAdapter extends ParseRecyclerQueryAdapter<Question, ParseQuestionAdapter.GaffeItemHolder> {
    Context context;
    private Friends friends;
    private ParseClient parseClient;
    private FragmentQuestionsReceived fragmentQuestionsReceived;
    private VideoPlayerManager videoPlayerManager;

    public ParseQuestionAdapter(boolean hasStableIds) {
        super(Question.class, hasStableIds);
    }

    public ParseQuestionAdapter(ParseQueryAdapter.QueryFactory<Question> factory, boolean hasStableIds) {
        super(factory, hasStableIds);
        videoPlayerManager = NothingAnsweredApplication.mVideoPlayerManager;
        parseClient = NothingAnsweredApplication.getParseClient();
    }

    public void setParentFragment(FragmentQuestionsReceived fragmentQuestionsReceived) {
        this.fragmentQuestionsReceived = fragmentQuestionsReceived;
    }

    @Override
    public GaffeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        friends = Friends.getInstance();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_gaffecard_beautiful, parent, false);
        GaffeItemHolder gaffeItemHolder = new GaffeItemHolder(view);
        return gaffeItemHolder;
    }

    @Override
    public void onBindViewHolder(GaffeItemHolder holder, int position) {
        Question question = getItem(position);
        GaffeItemHolder gaffeHolder = (GaffeItemHolder) holder;
        gaffeHolder.loadDataIntoView(context, question);
    }

    public class GaffeItemHolder extends RecyclerView.ViewHolder {
        TextView gaffeCardQuestion;
        RoundedImageView gaffeCardProfilePictureUrl;
        ImageView gaffeCardLike;
        ImageView ivOpenCamera;
        ImageView ivVideoThumbnail;
        VideoPlayerView vpVideoTexture;

        public GaffeItemHolder(final View itemView) {
            super(itemView);

            gaffeCardQuestion = (TextView) itemView.findViewById(R.id.gaffeCardQuestion);
            gaffeCardProfilePictureUrl = (RoundedImageView) itemView.findViewById(R.id.gaffeCardProfilePictureUrl);
            gaffeCardLike = (ImageView) itemView.findViewById(R.id.gaffeCardLike);
            ivOpenCamera = (ImageView) itemView.findViewById(R.id.ivOpenCamera);
            ivVideoThumbnail = (ImageView) itemView.findViewById(R.id.ivVideoThumbnail);
            vpVideoTexture = (VideoPlayerView) itemView.findViewById(R.id.vpVideoTexture);

            ivOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentQuestionsReceived.startMaterialCamera(itemView, getLayoutPosition());
                }
            });

            vpVideoTexture.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener() {
                @Override
                public void onVideoPreparedMainThread() {
                    gaffeCardQuestion.setVisibility(View.INVISIBLE);
                    gaffeCardProfilePictureUrl.setVisibility(View.INVISIBLE);
                    ivVideoThumbnail.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoCompletionMainThread() {
                    gaffeCardQuestion.setVisibility(View.VISIBLE);
                    gaffeCardProfilePictureUrl.setVisibility(View.VISIBLE);
                    ivVideoThumbnail.setVisibility(View.VISIBLE);
                }

                @Override
                public void onVideoStoppedMainThread() {
                    gaffeCardQuestion.setVisibility(View.VISIBLE);
                    gaffeCardProfilePictureUrl.setVisibility(View.VISIBLE);
                    ivVideoThumbnail.setVisibility(View.VISIBLE);
                }
            });

            ivVideoThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Question question = getItem(getLayoutPosition());
                    if (question.get(Question.RESPONDED).toString().equals("true")) {
                        final File file = new File(question.get(Question.LOCALVIDEOURL).toString());
                        videoPlayerManager.resetMediaPlayer();
                        if (file.exists()) {
                            videoPlayerManager.playNewVideo(null, vpVideoTexture, file.getAbsolutePath());
                        } else {
                            ParseQuery<Video> query = parseClient.getVideoQuery(question.get(Question.QUESTION).toString());
                            query.getFirstInBackground(new GetCallback<Video>() {
                                @Override
                                public void done(Video object, com.parse.ParseException e) {
                                    if (e == null) {
                                        Log.i("VIDEO LOAD", "PARSE");
                                        videoPlayerManager.playNewVideo(null, vpVideoTexture, ((ParseFile) object.get(Video.VIDEO)).getUrl());
                                    }
                                }
                            });
                            Toast.makeText(context, "Load from Parse", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }

        public void loadDataIntoView(final Context context, Question question) {
            //Question
            gaffeCardQuestion.setText(question.get(Question.QUESTION).toString());

            String sender = question.get(Question.SENDER_ID).toString();
            Log.i("DEBUG", friends.getNameFromId(sender));

            gaffeCardProfilePictureUrl.setImageResource(0);
            String profilePicUrl = NothingAnsweredApplication.getProfileImage(sender);
            Picasso.with(context).load(profilePicUrl).placeholder(R.drawable.ic_launcher).into(gaffeCardProfilePictureUrl);

            if (question.get(Question.RESPONDED) != null && question.get(Question.RESPONDED).toString().equals("true")) {
                ivOpenCamera.setVisibility(View.INVISIBLE);
                ParseQuery<Video> query = parseClient.getVideoQuery(question.get(Question.QUESTION).toString());
                query.getFirstInBackground(new GetCallback<Video>() {
                    @Override
                    public void done(Video object, com.parse.ParseException e) {
                        ivVideoThumbnail.setImageResource(0);
                        if (e == null) {
                            try {
                                Glide.with(context).load(((ParseFile) object.get(Video.VIDEO)).getFile()).into(ivVideoThumbnail);
                            } catch (com.parse.ParseException e1) {
                                e1.printStackTrace();
                            }
                            if (object.get(Video.LIKED) != null && object.get(Video.LIKED).toString().equals("true")) {
                                gaffeCardLike.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            ivVideoThumbnail.setImageResource(R.drawable.ivbackgroundcat);
                        }

                    }
                });
            }
            else {
                ivOpenCamera.setVisibility(View.VISIBLE);
                ivVideoThumbnail.setImageResource(0);
                if (question.get(Question.THUMBMAIL) != null) {
                    try {
                        Glide.with(context).load(((ParseFile) question.get(Question.THUMBMAIL)).getFile()).into(ivVideoThumbnail);
                    } catch (com.parse.ParseException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    ivVideoThumbnail.setImageResource(R.drawable.ivbackgroundcat);
                }
                gaffeCardLike.setVisibility(View.INVISIBLE);
            }
        }

        public String getFormattedTimeStamp(String timeStamp) {
            try {
                long timestamp = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy").parse(timeStamp).getTime();
                long elapsed = Math.max(System.currentTimeMillis() - timestamp, 0);
                long timeleft = DateUtils.DAY_IN_MILLIS - elapsed;
                long hours = timeleft / DateUtils.HOUR_IN_MILLIS;
                long minutes = (timeleft - hours * 60 * 60 * 1000) / DateUtils.MINUTE_IN_MILLIS;
                String timeSpan = String.format("%02d", hours) + ":";
                timeSpan += String.format("%02d", minutes) + "h";
                timeSpan += " left";
                return timeSpan;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }


    }
}
