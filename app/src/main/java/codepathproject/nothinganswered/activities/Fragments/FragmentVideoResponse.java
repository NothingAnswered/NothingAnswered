package codepathproject.nothinganswered.activities.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import codepathproject.nothinganswered.R;

public class FragmentVideoResponse extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_responses, container, false);
        TextView tvTitle = (TextView) view.findViewById(R.id.textView1);
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
