package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;

/**
 * Created by gpalem on 3/6/16.
 */
public class QuestionFragment extends DialogFragment {
    String TAG = QuestionFragment.class.getSimpleName();
    private ParseClient parseClient;
    private FacebookClient facebookClient;

    @Bind(R.id.etSendQuestion) EditText etSendQuestion;
    @Bind(R.id.etRecipient) EditText etRecipient;
    @Bind(R.id.btnSend) Button btnSend;

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
                String sender = ParseUser.getCurrentUser().getObjectId();
                String question = etSendQuestion.getText().toString();
                String recipient = etRecipient.getText().toString();
                ParseObject qObject = parseClient.createQuestionObject(question, parseClient.parseTemplateFile(), Arrays.asList(recipient));
                qObject.saveInBackground();
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
}
