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
    private String tabTitles[] = new String[] {"Video Response", "Questions Received", "Compose Question"};
    private int tabIcons[] = {R.drawable.icon_questions, R.drawable.icon_videos, R.drawable.icon_compose};
    public Fragment fragmentRef[] = new Fragment[PAGE_COUNT];

    public GaffeFragmentPagerAdapter (FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position < 0 || position > 2)
            return null;

        if (position == 0) {
            fragmentRef[position] = FragmentVideoResponse.newInstance(position);
        }
        if (position == 1) {
            fragmentRef[position] = FragmentQuestionsReceived.newInstance(position);
        }
        if (position == 2) {
            fragmentRef[position] = FragmentCompose.newInstance(position);
        }
        return fragmentRef[position];
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
