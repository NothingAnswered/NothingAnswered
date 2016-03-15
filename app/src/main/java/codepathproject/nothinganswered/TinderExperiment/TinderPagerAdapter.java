package codepathproject.nothinganswered.TinderExperiment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by jnagaraj on 3/14/16.
 */
public class TinderPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] {"Questions Received", "Video Response"};

    public TinderPagerAdapter (FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0)
        {
            return FragmentTinderQuestionsTimeline.newInstance(position);
        }
        if (position == 1) {
            return FragmentTinderVideosTimeline.newInstance(position);
        }
        else
        {
            return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
