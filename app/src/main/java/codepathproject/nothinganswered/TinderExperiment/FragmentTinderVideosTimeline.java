package codepathproject.nothinganswered.TinderExperiment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.tindercard.SwipeFlingAdapterView;

/**
 * Created by jnagaraj on 3/14/16.
 */
public class FragmentTinderVideosTimeline extends Fragment {

    private SwipeFlingAdapterView flingContainer;
    private TinderSimpleAdapter questionAdapter;
    private ArrayList<Data> al;

    public static FragmentTinderVideosTimeline newInstance (int page)
    {
        FragmentTinderVideosTimeline fragment = new FragmentTinderVideosTimeline();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.tinder_fling_view, container, false);


        al = new ArrayList<>();
        al.add(new Data("This is a first video response"));
        al.add(new Data("This is a second video response"));
        al.add(new Data("This is a third video response"));

        questionAdapter = new TinderSimpleAdapter(al, this.getActivity());

        flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);

        flingContainer.setAdapter(questionAdapter);



        return view;
    }


}
