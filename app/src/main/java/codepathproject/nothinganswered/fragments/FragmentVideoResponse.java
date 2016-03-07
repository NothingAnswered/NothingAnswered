package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.GaffeSimpleCardStackAdapter;

public class FragmentVideoResponse extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    VideoView testVideo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_responses, container, false);

//     //  swipeable cards checking here
        CardContainer mcardContainer = (CardContainer) view.findViewById(R.id.layoutview);
        mcardContainer.setOrientation(Orientations.Orientation.Disordered);

        CardModel card = new CardModel("Video Response","Video Response",view.getResources().getDrawable(R.drawable.picture1));
        CardModel card1 = new CardModel("Video Response","Video Response",view.getResources().getDrawable(R.drawable.picture2));


        GaffeSimpleCardStackAdapter adapter = new GaffeSimpleCardStackAdapter(getActivity());
        adapter.add(card);
        adapter.add(card1);
        mcardContainer.setAdapter(adapter);



       // testVideo = (VideoView) view.findViewById(R.id.testVideo);
//        testVideo.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
//       // MediaController mediaController = new MediaController(this);
//        testVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                testVideo.start();
//            }
//        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static FragmentVideoResponse newInstance (int page)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FragmentVideoResponse fragment = new FragmentVideoResponse();
        fragment.setArguments(args);
        return fragment;
    }
}
