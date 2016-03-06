package codepathproject.nothinganswered.activities.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import codepathproject.nothinganswered.activities.Fragments.FragmentVideoResponse;

public class GaffeFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] {"Tab 1", "Tab 2"};

    public GaffeFragmentPagerAdapter (FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentVideoResponse.newInstance(position + 1);
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
