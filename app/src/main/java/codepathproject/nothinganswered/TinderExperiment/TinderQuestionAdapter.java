package codepathproject.nothinganswered.TinderExperiment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.RecordActionListener;

/**
 * Created by jnagaraj on 3/14/16.
 */

public class TinderQuestionAdapter extends BaseAdapter {

    private RecordActionListener listener;

    public List<Data> parkingList;
    public Context context;

    public TinderQuestionAdapter(List<Data> apps, Context context) {
        this.parkingList = apps;
        this.context = context;
    }

    public void setRecordActionListener(RecordActionListener listener){
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return parkingList.size();
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
            rowView = inflater.inflate(R.layout.tinder_card_item, parent, false);
            // configure view holder
            viewHolder = new ViewHolder(rowView);
            viewHolder.question = (TextView) rowView.findViewById(R.id.gaffeCardQuestion);
            viewHolder.mOpenCamera = (ImageButton)rowView.findViewById(R.id.openCamera);
            viewHolder.mTimeStamp = (TextView) rowView.findViewById(R.id.tvQuestionTimeStamp);

           /* viewHolder.mOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(listener != null) {
                        listener.onRecordButtonClick(rowView, position);
                    }
                }
            });*/

            rowView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.question.setText(parkingList.get(position).getDescription() + "");

        return rowView;
    }

    public class ViewHolder {

        View view;
        public TextView question;
        private ImageButton mOpenCamera;
        private TextView mTimeStamp;

        public ViewHolder(View v) {
            view = v;
        }
    }

}

