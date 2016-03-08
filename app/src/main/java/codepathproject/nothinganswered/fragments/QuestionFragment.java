package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.models.Friends;

/**
 * Created by gpalem on 3/6/16.
 */
public class QuestionFragment extends DialogFragment {
    String TAG = QuestionFragment.class.getSimpleName();
    private ParseClient parseClient;
    private FacebookClient facebookClient;

    @Bind(R.id.etSendQuestion) EditText etSendQuestion;
    @Bind(R.id.etRecipient) MultiAutoCompleteTextView etRecipient;
    @Bind(R.id.btnSend) Button btnSend;

    public ArrayAdapter<String> adapter;
    public QuestionFragment() {
        // Empty constructor required for DialogFragment
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_question, container, false);
        ButterKnife.bind(this, view);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = etSendQuestion.getText().toString();
                String recipientStr = etRecipient.getText().toString();
                String[] recipients = recipientStr.split(",");
                ArrayList<String> recipientIds = new ArrayList<String>();
                for (String recipient : recipients) {
                    Log.i(TAG, recipient.trim());
                    String facebookId = Friends.getInstance().getIdFromName(recipient.trim());
                    if (facebookId != null) {
                        recipientIds.add(facebookId);
                    }
                }
                ParseObject qObject = parseClient.createQuestionObject(question, parseClient.parseTemplateFile(), recipientIds);
                qObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
                Log.i(TAG, question);
                Log.i(TAG, recipientStr);
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseClient = NothingAnsweredApplication.getParseClient();
        facebookClient = NothingAnsweredApplication.getFacebookClient();
    }

    @Override
    public void onResume() {
        super.onResume();
        setAutoCompleteAdapter();
    }

    public void setAutoCompleteAdapter() {
        ArrayList<String> friends = Friends.getInstance().getNames();
        Log.i(TAG, friends.get(0));
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friends.toArray(new String[friends.size()]));
        etRecipient.setAdapter(adapter);
        etRecipient.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }
}
