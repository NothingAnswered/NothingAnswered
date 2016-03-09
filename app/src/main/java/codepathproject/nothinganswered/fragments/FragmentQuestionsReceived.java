package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import codepathproject.nothinganswered.models.Gaffe;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Question;


public class FragmentQuestionsReceived extends TimelineFragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static FragmentQuestionsReceived newInstance (int page)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FragmentQuestionsReceived fragment = new FragmentQuestionsReceived();
        fragment.setArguments(args);
        return fragment;
    }

    public void populateTimeline() {

        Log.i("REFRESH", "IN REFRESH");
        // Construct query to execute
        final ParseQuery<Question> query = parseClient.getQuestionTimeline(Friends.myId, 5);
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Question>() {
            public void done(List<Question> messages, ParseException e) {
                if (e == null) {
                    if (messages != null && messages.size() > 0) {

                        mGaffes.clear();

                        for (int i = 0; i < messages.size(); i++) {


                            Question question = messages.get(i);
                            friends = Friends.getInstance();
                            String sender = question.get(Question.SENDER_ID).toString();

                            Gaffe card = new Gaffe();
                            card.questionTitle = question.get(Question.QUESTION).toString();
                            card.username = (friends.getNameFromId(sender));

                            mGaffes.add(card);

                            Log.i(TAG, question.get(Question.QUESTION).toString());
                            Log.i(TAG, friends.getNameFromId(sender));
                        }
                        //clearListAndAddNew(mGaffes);

                        gaffeRecyclerAdapter.notifyDataSetChanged();
                    }

                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });

    }

   /* public void populateTimeline() {

        Log.d(TAG, "Populate Timeline");

        ArrayList<Gaffe> gaffes = new ArrayList<>();
        for(int i = 0; i < 10; i++) {


            Gaffe card = new Gaffe();
            card.questionTitle = "What's on your tongue?";
            card.username = "Jayashree";

            Log.d(TAG, card.questionTitle);
            gaffes.add(card);

        }

        mGaffes.addAll(gaffes);
        gaffeRecyclerAdapter.notifyDataSetChanged();

        //clearListAndAddNew(mGaffes);
    }*/

}
