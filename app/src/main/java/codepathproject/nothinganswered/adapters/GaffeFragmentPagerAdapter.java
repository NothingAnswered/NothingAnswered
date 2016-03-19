package codepathproject.nothinganswered.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.astuetz.PagerSlidingTabStrip;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.fragments.FragmentCompose;
import codepathproject.nothinganswered.fragments.FragmentQuestionsReceived;
import codepathproject.nothinganswered.fragments.FragmentVideoResponse;

public class GaffeFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] {"Questions Received", "Video Response"};
    private int tabIcons[] = {R.drawable.icon_questions, R.drawable.icon_videos, R.drawable.icon_compose};

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
        if (position == 2) {
            return FragmentCompose.newInstance(position);
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

    @Override
    public int getPageIconResId(int position) {
        return tabIcons[position];
    }
}
