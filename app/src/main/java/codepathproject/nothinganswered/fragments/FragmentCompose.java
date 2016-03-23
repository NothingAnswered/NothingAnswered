package codepathproject.nothinganswered.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;

import java.io.IOException;
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

    public static final int PICTURE_REQUEST_CODE = 2;
    public interface ComposeFragmentActionListener {
        void onFragmentExit(int position);
    }

    private ComposeFragmentActionListener listener;

    String TAG = FragmentCompose.class.getSimpleName();
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

    ImageView ivCamera;
    ImageView ivBgImage;

    public ArrayAdapter<String> adapter;

    private int mPosition;

    Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get dialog removes the empty space in the fragment


        View view = inflater.inflate(R.layout.fragment_send_question, container, false);

        mPosition = getArguments().getInt("POSITION");
        etSendQuestion = (EditText)view.findViewById(R.id.etSendQuestion);
        etRecipient = (MultiAutoCompleteTextView) view.findViewById(R.id.etRecipient);
        btnSend = (Button) view.findViewById(R.id.btnSend);
        tvCharacterCount = (TextView)view.findViewById(R.id.tvCharacterCount);
        btnCancelButton = (ImageView) view.findViewById(R.id.cancelButton);
        ivCamera = (ImageView) view.findViewById(R.id.ivCamera);
        ivBgImage = (ImageView)view.findViewById(R.id.ivBgImage);



        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICTURE_REQUEST_CODE);

                //intent.setType("image/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);//
                //startActivityForResult(Intent.createChooser(intent, "Select Picture") , PICTURE_REQUEST_CODE);
            }
        });

        btnCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                closeKeyboard(getActivity(), etSendQuestion.getWindowToken());
                getParentFragment().getFragmentManager().popBackStack();
            }
        });

        etSendQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                final String question = etSendQuestion.getText().toString();
                final String recipientStr = etRecipient.getText().toString();
                String[] recipients = recipientStr.split(",");
                ParseFile imageFile = parseClient.createQuestionThumbNail(bitmap);
                for (String recipient : recipients) {
                    Log.i(TAG, recipient.trim());
                    String facebookId = Friends.getInstance().getIdFromName(recipient.trim());
                    if (facebookId != null) {
                        parseClient.sendQuestionObject(question, facebookId, imageFile);
                    }
                }

                Log.i(TAG, question);
                Log.i(TAG, recipientStr);


                closeKeyboard(getActivity(), etSendQuestion.getWindowToken());
                listener = (ComposeFragmentActionListener) getActivity();
                if(listener != null) {
                   listener.onFragmentExit(mPosition);
                }
            }
        });
        return view;
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseClient = NothingAnsweredApplication.getParseClient();
        facebookClient = NothingAnsweredApplication.getFacebookClient();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Toast.makeText(getActivity(), "Done!" + requestCode, Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);


       // if (requestCode == PICTURE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                        Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
                        ivBgImage.setVisibility(View.VISIBLE);
                        ivBgImage.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
       // }
    }

    @Override
    public void onResume() {
        super.onResume();
       setAutoCompleteAdapter();

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.showSoftInput(etSendQuestion, 0);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
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
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        fragmentCompose.setArguments(bundle);
        return fragmentCompose;
    }
}
