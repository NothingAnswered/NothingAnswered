package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.andtinder.model.CardModel;
import com.andtinder.view.SimpleCardStackAdapter;

import codepathproject.nothinganswered.R;


public class FragmentQuestionsReceived extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions_received, container, false);

        //   swipeable cards checking here
        ListView mcardContainer = (ListView) view.findViewById(R.id.layoutview);
//        CardContainer mcardContainer = (CardContainer) view.findViewById(R.id.layoutview);
//        mcardContainer.setOrientation(Orientations.Orientation.Disordered);
        CardModel card = new CardModel("Questions Recieved","Decription GOes there",view.getResources().getDrawable(R.drawable.picture2));
        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(getActivity());
        adapter.add(card);
        mcardContainer.setAdapter(adapter);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static FragmentQuestionsReceived newInstance (int page)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FragmentQuestionsReceived fragment = new FragmentQuestionsReceived();
        fragment.setArguments(args);
        return fragment;
    }
}
