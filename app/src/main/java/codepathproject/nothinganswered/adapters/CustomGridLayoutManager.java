package codepathproject.nothinganswered.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by jnagaraj on 3/11/16.
 */

//Overriding the vertical scroll capability to be set with a public api

public class CustomGridLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomGridLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
