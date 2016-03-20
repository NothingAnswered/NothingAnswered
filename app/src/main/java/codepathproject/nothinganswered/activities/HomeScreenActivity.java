package codepathproject.nothinganswered.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.astuetz.PagerSlidingTabStrip;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.GaffeFragmentPagerAdapter;
import codepathproject.nothinganswered.fragments.FragmentQuestionsReceived;

public class HomeScreenActivity extends AppCompatActivity {

    private static String TAG = HomeScreenActivity.class.getSimpleName();

    private ViewPager vpPager;
    private FragmentManager fragmentManager;
    private GaffeFragmentPagerAdapter gaffeFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Get the viewpager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        gaffeFragmentPagerAdapter = new GaffeFragmentPagerAdapter(getSupportFragmentManager());
        // set the viewpager adapter for the pager
        vpPager.setAdapter(gaffeFragmentPagerAdapter);
        // find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);

        //Fragment manager
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FragmentQuestionsReceived.CAMERA_RQ) {
            FragmentQuestionsReceived fragmentQuestionsReceived = (FragmentQuestionsReceived) gaffeFragmentPagerAdapter.fragmentRef[0];
            fragmentQuestionsReceived.onActivityResult(requestCode, resultCode, data);
        }
    }
}
