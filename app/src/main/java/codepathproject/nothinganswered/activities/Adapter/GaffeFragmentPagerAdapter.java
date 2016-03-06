package codepathproject.nothinganswered.activities.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import codepathproject.nothinganswered.activities.Fragments.FragmentQuestionsReceived;
import codepathproject.nothinganswered.activities.Fragments.FragmentVideoResponse;

public class GaffeFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] {"Questions Received", "Video Response"};

    public GaffeFragmentPagerAdapter (FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0)
        {
            return FragmentQuestionsReceived.newInstance(position);
        }
        if (position == 1) {
            return FragmentVideoResponse.newInstance(position);
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
