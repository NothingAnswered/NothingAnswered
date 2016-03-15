package codepathproject.nothinganswered.TinderExperiment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import codepathproject.nothinganswered.R;

/**
 * Created by jnagaraj on 3/14/16.
 */
public class TinderVideoAdapter extends BaseAdapter {

    public List<TinderVideo> videos;
    public Context context;

    public TinderVideoAdapter(List<TinderVideo> videoList, Context context) {
        this.videos = videoList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    View rowView;

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        rowView = convertView;
        ViewHolder viewHolder;

        if (rowView == null) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.tinder_card_video_item, parent, false);
            // configure view holder
            viewHolder = new ViewHolder(rowView);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.question.setText("Here's my response!");

        return rowView;
    }

    public class ViewHolder {

        View view;
        public TextView question;

        public ViewHolder(View v) {
            view = v;

            question = (TextView) v.findViewById(R.id.gaffeCardQuestion);
            view.setTag(this);
        }
    }

}

