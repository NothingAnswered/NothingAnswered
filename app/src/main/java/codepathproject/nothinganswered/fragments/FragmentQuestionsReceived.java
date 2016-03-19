package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.Calendar;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.ParseQuestionAdapter;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Question;

public class FragmentQuestionsReceived extends TimelineFragment {


    private static final String TAG = "QuestionsRecieved";
    ImageButton mButtonVideo;

    private ParseQuestionAdapter questionAdapter;

    public static FragmentQuestionsReceived newInstance() {
        return new FragmentQuestionsReceived();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createQuestionAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        rvResults.setAdapter(questionAdapter);
        return view;
    }

    public void createQuestionAdapter() {
        ParseQueryAdapter.QueryFactory<Question> factory = new ParseQueryAdapter.QueryFactory<Question>() {
            public ParseQuery<Question> create() {
                ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
                query.whereEqualTo(Question.RECIPIENT_ID, Friends.myId);
               //query.whereEqualTo(Question.RESPONDED, "false");
                query.orderByDescending("createdAt");

                //-24 hours Date object
                Calendar calendar = Calendar.getInstance();
                Log.i(TAG, "Time " + getActivity().getString(R.string.max_lookback_time));
                calendar.add(Calendar.HOUR_OF_DAY, -Integer.parseInt(getActivity().getString(R.string.max_lookback_time)));
                //query.whereGreaterThan("createdAt", calendar.getTime());
                return query;
            }
        };
        questionAdapter = new ParseQuestionAdapter(factory, true);
    }

    public void loadObjects() {
        questionAdapter.loadObjects();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static FragmentQuestionsReceived newInstance (int page)
    {
        FragmentQuestionsReceived fragment = new FragmentQuestionsReceived();
        return fragment;
    }

   /*
    public void updateQuestion(Question question) {
        question.put(Question.LOCALVIDEOURL, getVideoFile(getActivity()).getAbsolutePath());
        question.put(Question.RESPONDED, "true");
        question.saveInBackground();
    }
    public void uploadVideo(Question question) {
        //Upload video
        parseClient = NothingAnsweredApplication.getParseClient();
        File file = new File(question.get(Question.LOCALVIDEOURL).toString());
        parseClient.sendVideoResponse(question.get(Question.SENDER_ID).toString(), question.get(Question.QUESTION).toString(), file);
        Toast.makeText(getActivity(), question.get(Question.QUESTION).toString(), Toast.LENGTH_SHORT).show();
    }
*/

}
