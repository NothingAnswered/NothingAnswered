package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.models.Friends;

/**
 * Created by jnagaraj on 3/18/16.
 */
public class FragmentCompose extends Fragment {

    String TAG = QuestionFragment.class.getSimpleName();
    private ParseClient parseClient;
    private FacebookClient facebookClient;

    //@Bind(R.id.etSendQuestion)
    EditText etSendQuestion;

    //@Bind(R.id.etRecipient)
    MultiAutoCompleteTextView etRecipient;

    //@Bind(R.id.btnSend)
    Button btnSend;

    //@Bind(R.id.tvCharacterCount)
    TextView tvCharacterCount;

    //@Bind(R.id.cancelButton)
    ImageView btnCancelButton;


    public ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get dialog removes the empty space in the fragment
        View view = inflater.inflate(R.layout.fragment_send_question, container, false);

        etSendQuestion = (EditText)view.findViewById(R.id.etSendQuestion);
        etRecipient = (MultiAutoCompleteTextView) view.findViewById(R.id.etRecipient);
        btnSend = (Button) view.findViewById(R.id.btnSend);
        tvCharacterCount = (TextView)view.findViewById(R.id.tvCharacterCount);
        btnCancelButton = (ImageView) view.findViewById(R.id.cancelButton);

        //Cancel Button
        btnCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        etSendQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharacterCount.setText(String.valueOf(s.length()) + "/100");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = etSendQuestion.getText().toString();
                String recipientStr = etRecipient.getText().toString();
                String[] recipients = recipientStr.split(",");
                for (String recipient : recipients) {
                    Log.i(TAG, recipient.trim());
                    String facebookId = Friends.getInstance().getIdFromName(recipient.trim());
                    if (facebookId != null) {
                        parseClient.sendQuestionObject(question, facebookId);
                        parseClient.sendQuestionNotification(question, facebookId);
                    }
                }
                Log.i(TAG, question);
                Log.i(TAG, recipientStr);
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

    public static FragmentCompose newInstance(int position) {
       FragmentCompose fragmentCompose = new FragmentCompose();
        return fragmentCompose;
    }
}
