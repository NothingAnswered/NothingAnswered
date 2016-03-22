package codepathproject.nothinganswered.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.bumptech.glide.Glide;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.io.File;
import java.util.Calendar;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.ParseQuestionAdapter;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Question;

public class FragmentQuestionsReceived extends TimelineFragment {

    private static final String TAG = "QuestionsReceived";
    public final static int CAMERA_RQ = 6969;
    private int currLayoutPosition;
    private View currLayoutView;

    private ParseQuestionAdapter questionAdapter;

    public static FragmentQuestionsReceived newInstance() {
        return new FragmentQuestionsReceived();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createQuestionAdapter();
        questionAdapter.setParentFragment(this);
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

    public void startMaterialCamera(View view, int position) {
        currLayoutPosition = position;
        currLayoutView = view;
        new MaterialCamera(getActivity())                       // Constructor takes an Activity
                .allowRetry(false)                          // Whether or not 'Retry' is visible during playback
                .autoSubmit(true)                         // Whether or not user is allowed to playback videos after recording. This can affect other things, discussed in the next section.
                .saveDir(NothingAnsweredApplication.getVideosFolder())                       // The folder recorded videos are saved to
                .primaryColorAttr(R.attr.colorPrimary)     // The theme color used for the camera, defaults to colorPrimary of Activity in the constructor
                .showPortraitWarning(false)                 // Whether or not a warning is displayed if the user presses record in portrait orientation
                .defaultToFrontFacing(true)               // Whether or not the camera will initially show the front facing camera
                .retryExits(true)                         // If true, the 'Retry' button in the playback screen will exit the camera instead of going back to the recorder
                .countdownSeconds(Float.parseFloat(getActivity().getString(R.string.max_video_duration)))
                .start(CAMERA_RQ);                         // Starts the camera activity, the result will be sent back to the current Activity
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Received recording or error from MaterialCamera
        if (requestCode == CAMERA_RQ) {
            if (resultCode == Activity.RESULT_OK) {
                final File file = new File(data.getData().getPath());
                updateQuestionView(file);
                dealWithParse(file);
            } else if(data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateQuestionView(File video) {
        ImageView ivVideoThumbnail = (ImageView) currLayoutView.findViewById(R.id.ivVideoThumbnail);
        ImageView ivOpenCamera = (ImageView) currLayoutView.findViewById(R.id.ivOpenCamera);
        Glide.with(getContext()).load(video.getAbsolutePath()).into(ivVideoThumbnail);
        ivOpenCamera.setVisibility(View.INVISIBLE);
    }

    private void dealWithParse(File video) {
        Question question = questionAdapter.getItem(currLayoutPosition);
        updateQuestion(question, video);
        uploadVideo(question);
    }

    public void updateQuestion(Question question, File video) {
        question.put(Question.LOCALVIDEOURL, video.getAbsolutePath());
        question.put(Question.RESPONDED, "true");
        question.saveInBackground();
    }

    public void uploadVideo(final Question question) {
        //Upload video
        parseClient = NothingAnsweredApplication.getParseClient();
        final File file = new File(question.get(Question.LOCALVIDEOURL).toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseClient.sendVideoResponse(question.get(Question.SENDER_ID).toString(), question.get(Question.QUESTION).toString(), file);
            }
        }).start();
    }
}
