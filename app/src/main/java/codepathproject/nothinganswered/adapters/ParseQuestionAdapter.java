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
    private Friends friends;

    public ParseQuestionAdapter(boolean hasStableIds) {
        super(Question.class, hasStableIds);
    }

    public ParseQuestionAdapter(ParseQueryAdapter.QueryFactory<Question> factory, boolean hasStableIds) {
        super(factory, hasStableIds);
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

        private TextView mQuestionTitle;
        private ImageView ivOpenCamera;
        private ImageView mProfileImage;

        public GaffeItemHolder(final View itemView) {
            super(itemView);

            mQuestionTitle = (TextView)itemView.findViewById(R.id.gaffeCardQuestion);
            ivOpenCamera = (ImageView)itemView.findViewById(R.id.ivOpenCamera);
            ivOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Start camera activity here
                    Toast.makeText(context, "Start camera activity now", Toast.LENGTH_SHORT).show();
                }
            });

            mProfileImage = (ImageView) itemView.findViewById(R.id.gaffeCardProfilePictureUrl);

        }

        public void loadDataIntoView(Context context, Question question) {
            //Question
            mQuestionTitle.setText(question.get(Question.QUESTION).toString());

            String sender = question.get(Question.SENDER_ID).toString();
            Log.i("DEBUG", friends.getNameFromId(sender));

            mProfileImage.setImageResource(0);
            String profilePicUrl = NothingAnsweredApplication.getProfileImage(sender);
            Picasso.with(context).load(profilePicUrl).placeholder(R.drawable.ic_launcher).into(mProfileImage);
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
