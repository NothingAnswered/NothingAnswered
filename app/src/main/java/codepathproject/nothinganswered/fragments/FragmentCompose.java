package codepathproject.nothinganswered.fragments;

/**
 * Created by jnagaraj on 3/18/16.
 */
public class FragmentCompose extends TimelineFragment {
    @Override
    public void loadObjects() {

    }

    public static FragmentCompose newInstance(int position) {
       FragmentCompose fragmentCompose = new FragmentCompose();
        return fragmentCompose;
    }
}
