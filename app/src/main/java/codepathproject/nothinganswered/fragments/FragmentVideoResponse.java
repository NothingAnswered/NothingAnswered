package codepathproject.nothinganswered.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class FragmentVideoResponse extends TimelineFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static FragmentQuestionsReceived newInstance (int page)
    {
        FragmentQuestionsReceived fragment = new FragmentQuestionsReceived();
        return fragment;
    }

    public void populateTimeline() {

    }
}
