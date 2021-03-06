package codepathproject.nothinganswered.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Question;

/**
 * Created by gpalem on 3/12/16.
 */
public class ParseQuestionAdapter extends ParseRecyclerQueryAdapter<Question, ParseQuestionAdapter.GaffeItemHolder> {
    Context context;
    private RecordActionListener listener;
    private Friends friends;

    public ParseQuestionAdapter(boolean hasStableIds) {
        super(Question.class, hasStableIds);
    }

    public ParseQuestionAdapter(ParseQueryAdapter.QueryFactory<Question> factory, boolean hasStableIds) {
        super(factory, hasStableIds);
    }

    public void setRecordActionListener(RecordActionListener listener){
        this.listener = listener;
    }

    @Override
    public GaffeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        friends = Friends.getInstance();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_gaffecard, parent, false);
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
        private TextView mQuestionTitle;
        private TextView mUsername;
        private ImageButton mOpenCamera;
        private TextView mTimeStamp;
        private ImageView ivPlay;
        private ImageView ivVideoThumbnail;
        private TextView tvTimer;

        private ImageView mProfileImage;

        public GaffeItemHolder(final View itemView) {
            super(itemView);

            mQuestionTitle = (TextView)itemView.findViewById(R.id.gaffeCardQuestion);
            //mUsername = (TextView)itemView.findViewById(R.id.gaffeCardProfileName);
            mOpenCamera = (ImageButton)itemView.findViewById(R.id.openCamera);
            mTimeStamp = (TextView) itemView.findViewById(R.id.tvQuestionTimeStamp);
            ivPlay = (ImageView)itemView.findViewById(R.id.ivPlayIcon);
            ivVideoThumbnail = (ImageView)itemView.findViewById(R.id.ivVideoImage);
            tvTimer = (TextView) itemView.findViewById(R.id.tvTimer);

            mOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(listener != null) {
                        listener.onRecordButtonClick(itemView, getLayoutPosition());
                    }
                }
            });

            ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onPlayButtonClick(itemView, getLayoutPosition());
                    }

                }
            });

            mProfileImage = (ImageView) itemView.findViewById(R.id.gaffeCardProfilePictureUrl);

        }

        public void loadDataIntoView(Context context, Question question) {
            //Question
            mQuestionTitle.setText(question.get(Question.QUESTION).toString());
            //TimeStamp
            mTimeStamp.setText(getFormattedTimeStamp(question.getCreatedAt().toString()));
            //Sender
            String sender = question.get(Question.SENDER_ID).toString();
            Log.i("DEBUG", sender);
            Log.i("DEBUG", friends.getNameFromId(sender));
            //mUsername.setText(friends.getNameFromId(sender));
            //Profile Image
            mProfileImage.setImageResource(0);
            String profilePicUrl = NothingAnsweredApplication.getProfileImage(sender);
            Picasso.with(context).load(profilePicUrl).placeholder(R.drawable.ic_launcher).into(mProfileImage);

            ivVideoThumbnail.setImageResource(R.drawable.ic_launcher);

            if(question.get(Question.RESPONDED).toString().equals("true")){
                //display video playback view
                ivPlay.setVisibility(View.VISIBLE);
                ivVideoThumbnail.setVisibility(View.VISIBLE);

                //autoFitTextureView.setVisibility(View.INVISIBLE);
                mOpenCamera.setVisibility(View.INVISIBLE);
                tvTimer.setVisibility((View.INVISIBLE));

            }else{
                //display video recorder view
                ivPlay.setVisibility(View.INVISIBLE);
                ivVideoThumbnail.setVisibility(View.INVISIBLE);

                //autoFitTextureView.setVisibility(View.VISIBLE);
                mOpenCamera.setVisibility(View.VISIBLE);
                tvTimer.setVisibility((View.VISIBLE));
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
