package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

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
    @Bind(R.id.tvCharacterCount) TextView tvCharacterCount;
    @Bind(R.id.ivSendImage) RoundedImageView ivSendImage;
    @Bind(R.id.tvSendName) TextView tvSendName;

    public ArrayAdapter<String> adapter;
    public QuestionFragment() {
        // Empty constructor required for DialogFragment
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get dialog removes the empty space in the fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_send_question, container, false);

        ButterKnife.bind(this, view);

        //Getting the toolbar
        Toolbar toolbar =  (Toolbar) view.findViewById(R.id.send_question_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("Ask your friends");

        //Image
        ivSendImage.setImageResource(0);
        Picasso.with(getContext()).load(Friends.profileImage).placeholder(R.drawable.ic_launcher).into(ivSendImage);
        //TextViews
        tvSendName.setText(Friends.firstName + " " + Friends.lastName);
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
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.miCompose).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                dismiss();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
