package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.File;
import java.util.List;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.adapters.RecordActionListener;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Gaffe;
import codepathproject.nothinganswered.models.Question;


public class FragmentQuestionsReceived extends TimelineFragment implements RecordResponseDialogActionListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        adapter.setOnItemClickListener(new ContactsAdapter.OnItemClickListener() {
    @Override
    public void onItemClick(View view, int position) {
        String name = users.get(position).name;
        Toast.makeText(UserListActivity.this, name + " was clicked!", Toast.LENGTH_SHORT).show();
    }
         */
        gaffeRecyclerAdapter.setRecordActionListener(new RecordActionListener() {
            @Override
            public void onRecordButtonClick(View view, int position) {
                FragmentManager fm = getActivity().getSupportFragmentManager();

                VideoRecorderDialogFragment recordVideo = VideoRecorderDialogFragment.newInstance(position);
                recordVideo.setTargetFragment(getParentFragment(), 300);
                recordVideo.show(fm, "dialog_compose_tweet");
            }
        });

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

    @Override
    public void onStart() {
        super.onStart();
        populateTimeline();
    }

    public void populateTimeline() {

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
                            card.profilePicUrl = NothingAnsweredApplication.getProfileImage(sender);


                            mGaffes.add(card);

                            Log.i(TAG, question.get(Question.QUESTION).toString());
                            Log.i(TAG, friends.getNameFromId(sender));
                        }
                        //clearListAndAddNew(mGaffes);

                        gaffeRecyclerAdapter.notifyDataSetChanged();
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onRecordResponse(File videoFile, int cardPosition) {

        Gaffe gaffe = mGaffes.get(cardPosition);

        //Upload Video
        parseClient = NothingAnsweredApplication.getParseClient();
        parseClient.sendVideoResponse(Friends.myId, gaffe.questionTitle, videoFile);

        Toast.makeText(this.getContext(), gaffe.username + " :: "  + gaffe.questionTitle, Toast.LENGTH_SHORT).show();
    }
}
