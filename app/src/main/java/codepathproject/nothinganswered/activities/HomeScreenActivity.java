package codepathproject.nothinganswered.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.astuetz.PagerSlidingTabStrip;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.GaffeFragmentPagerAdapter;
import codepathproject.nothinganswered.fragments.QuestionFragment;

public class HomeScreenActivity extends AppCompatActivity {

    private static String TAG = HomeScreenActivity.class.getSimpleName();

    private ViewPager vpPager;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //Toolbar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Get the viewpager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        // set the viewpager adapter for the pager
        vpPager.setAdapter(new GaffeFragmentPagerAdapter(getSupportFragmentManager()));
        // find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);

        //Fragment manager
        fragmentManager = getSupportFragmentManager();
    }

    /*// Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem composeQuestion = (MenuItem) menu.findItem(R.id.miCompose);
        composeQuestion.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showQuestionDialog();
                return true;
            }
        });
        return true;
    }*/

    public void showQuestionDialog() {
        QuestionFragment questionFragment = new QuestionFragment();
        questionFragment.show(fragmentManager, "fragment_send_question");
    }

}
