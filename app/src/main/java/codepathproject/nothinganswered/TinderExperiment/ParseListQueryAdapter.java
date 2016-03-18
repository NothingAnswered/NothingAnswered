package codepathproject.nothinganswered.TinderExperiment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Question;

/**
 * Created by jnagaraj on 3/17/16.
 */
public class ParseListQueryAdapter extends ParseQueryAdapter<ParseObject> {

    Context context;
    public ParseListQueryAdapter(Context context) {

        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>(){

            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery query = ParseQuery.getQuery(Question.class);
                query.whereEqualTo(Question.RECIPIENT_ID, Friends.myId);
                //query.whereEqualTo(Question.RESPONDED, "false");
                query.orderByDescending("createdAt");

                //-24 hours Date object
                //Calendar calendar = Calendar.getInstance();
                //Log.i(TAG, "Time " + getActivity().getString(R.string.max_lookback_time));
                //calendar.add(Calendar.HOUR_OF_DAY, -Integer.parseInt(getActivity().getString(R.string.max_lookback_time)));
                //query.whereGreaterThan("createdAt", calendar.getTime());
                return query;
            }


        });

        this.context = context;
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            //v = View.inflate(getContext(), R.layout.tinder_card_item, parent);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.tinder_card_item, parent, false);

        }

        super.getItemView(object, v, parent);

        ImageView ivProfilePic = (ImageView) v.findViewById(R.id.gaffeCardProfilePictureUrl);
        TextView tvQuestionText = (TextView)v.findViewById(R.id.gaffeCardQuestion);

        String sender = object.get(Question.SENDER_ID).toString();
        String profileUrl = NothingAnsweredApplication.getProfileImage(sender);
        ivProfilePic.setImageResource(0);
        Picasso.with(getContext()).load(profileUrl).into(ivProfilePic);

        String questionTitle = object.get(Question.QUESTION).toString();
        tvQuestionText.setText(questionTitle);

        return v;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public ParseObject getItem(int index) {
        return super.getItem(index);
    }




}
