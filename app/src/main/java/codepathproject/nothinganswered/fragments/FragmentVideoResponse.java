package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.VideoView;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.GaffeSimpleCardStackAdapter;
import codepathproject.nothinganswered.models.GaffeCardModel;

public class FragmentVideoResponse extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    VideoView testVideo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_responses, container, false);

//     //  swipeable cards checking here
//        codepathproject.nothinganswered.container.CardContainer mcardContainer = (codepathproject.nothinganswered.container.CardContainer) view.findViewById(R.id.layoutview);
//        mcardContainer.setOrientation(Orientations.Orientation.Disordered);

        ListView mcardContainer = (ListView)view.findViewById(R.id.layoutview);
         GaffeCardModel card = new GaffeCardModel("Card 1","http://techslides.com/demos/sample-videos/small.mp4",
                 view.getResources().getDrawable(R.drawable.picture1),"End Card 1");
        GaffeCardModel card1 = new GaffeCardModel("Card 2","http://techslides.com/demos/sample-videos/small.mp4",
                view.getResources().getDrawable(R.drawable.picture2),"End Card 2");


        GaffeSimpleCardStackAdapter adapter = new GaffeSimpleCardStackAdapter(getActivity());
        adapter.add(card);
        adapter.add(card1);
        mcardContainer.setAdapter(adapter);



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
