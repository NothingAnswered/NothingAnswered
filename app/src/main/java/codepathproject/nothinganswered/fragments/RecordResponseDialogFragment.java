package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import codepathproject.nothinganswered.R;

public class RecordResponseDialogFragment extends DialogFragment {

    public static String QUESTION_TITLE = "title";
    public static String IMAGE_URL = "imageUrl";
    public static String VIDEO_URI = "videoUri";

    private RecordResponseDialogActionListener listener;

    private Button btnStop;
    private Button btnStart;

    public interface RecordResponseDialogActionListener {
        void onRecordResponse(String videoUri);
    }


    private final String DEFAULT_PIC = "http://pbs.twimg.com/profile_images/501991114090364930/bSQe0m2B_normal.jpeg";

    public RecordResponseDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static RecordResponseDialogFragment newInstance(String title, String imageUrl) {
        RecordResponseDialogFragment frag = new RecordResponseDialogFragment();
        Bundle args = new Bundle();
        args.putString(RecordResponseDialogFragment.QUESTION_TITLE, title);
        args.putString(RecordResponseDialogFragment.IMAGE_URL, imageUrl);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera2_video, container);

        btnStart = (Button) view.findViewById(R.id.start);
        btnStop = (Button) view.findViewById(R.id.stop);


        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String title = getArguments().getString(RecordResponseDialogFragment.QUESTION_TITLE, "Enter Name");
        //String profileUrl = getArguments().getString(RecordResponseDialogFragment.IMAGE_URL, DEFAULT_PIC);
        getDialog().setTitle(title);
        //Picasso.with(view.getContext()).load(profileUrl).into(ivProfilePic);


        View.OnClickListener onStartRecording = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        };

        View.OnClickListener onClickStopRecordResponse = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                listener = (RecordResponseDialogActionListener) getTargetFragment();
                listener.onRecordResponse("");
                dismiss();

            }
        };

        btnStop.setOnClickListener(onClickStopRecordResponse);
        btnStart.setOnClickListener(onStartRecording);
    }

}
