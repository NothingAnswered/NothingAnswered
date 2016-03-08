package codepathproject.nothinganswered.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collection;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.models.GaffeCardModel;


public abstract class GaffeCardStackAdapter extends BaseAdapter {

    private final Context mContext;

    private final Object mLock = new Object();
    private ArrayList<GaffeCardModel> mData;

    private boolean mShouldFillCardBackground = false;

    public GaffeCardStackAdapter (Context context){
        mContext = context;
        mData = new ArrayList<GaffeCardModel>();

    }

    public GaffeCardStackAdapter (Context context, Collection<?extends GaffeCardModel> items)
    {
        mContext = context;
        mData = new ArrayList<GaffeCardModel>(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FrameLayout wrapper = (FrameLayout) convertView;
        FrameLayout innerWrapper;
        View cardView;
        View convertedCardView;
        if (wrapper == null) {
            wrapper = new FrameLayout(mContext);
            wrapper.setBackgroundResource(R.drawable.card_bg);
            if (shouldFillCardBackground()) {
                innerWrapper = new FrameLayout(mContext);
                innerWrapper.setBackgroundColor(mContext.getResources().getColor(R.color.card_bg));
                wrapper.addView(innerWrapper);
            } else {
                innerWrapper = wrapper;
            }
            cardView = getCardView(position, getCardModel(position), null, parent);
            innerWrapper.addView(cardView);
        } else {
            if (shouldFillCardBackground()) {
                innerWrapper = (FrameLayout) wrapper.getChildAt(0);
            } else {
                innerWrapper = wrapper;
            }
            cardView = innerWrapper.getChildAt(0);
            convertedCardView = getCardView(position, getCardModel(position), cardView, parent);
            if (convertedCardView != cardView) {
                wrapper.removeView(cardView);
                wrapper.addView(convertedCardView);
            }

        }
        return wrapper;
    }

    protected abstract View getCardView (int position, GaffeCardModel model, View convertView, ViewGroup parent);

    public void setShouldFillCardBackground(boolean isShouldFillCardBackground) {
        this.mShouldFillCardBackground = isShouldFillCardBackground;
    }

    public boolean shouldFillCardBackground() {
        return mShouldFillCardBackground;
    }

    public void add (GaffeCardModel item)
    {
        synchronized (mLock) {
            mData.add(item);
        }
        notifyDataSetChanged();
    }

    public GaffeCardModel pop()
    {
        GaffeCardModel model;
        synchronized (mLock) {
            model = mData.remove(mData.size() - 1);
        }
        notifyDataSetChanged();
        return model;
    }

    @Override
    public Object getItem(int position) {
        return getCardModel(position);
    }

    public GaffeCardModel getCardModel (int position)
    {
        synchronized (mLock) {
            return mData.get(mData.size() - 1 - position);
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    public Context getContext() {
        return mContext;
    }

}
