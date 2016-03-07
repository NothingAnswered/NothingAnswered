package codepathproject.nothinganswered.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andtinder.model.CardModel;
import com.andtinder.view.CardStackAdapter;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.activities.PlayVideoActivity_rahul;

/**
 * Created by rmukhedkar on 3/6/16.
 */
public class GaffeSimpleCardStackAdapter extends CardStackAdapter {

    private ImageView imageView;


    public GaffeSimpleCardStackAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected View getCardView(int i, CardModel model, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.std_card_inner, parent, false);
            assert convertView != null;
        }

        imageView = (ImageView) convertView.findViewById(R.id.image);
        imageView.setImageDrawable(model.getCardImageDrawable());
        ((TextView) convertView.findViewById(R.id.title)).setText(model.getTitle());
        ((TextView) convertView.findViewById(R.id.description)).setText(model.getDescription());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getContext(), PlayVideoActivity_rahul.class);
                getContext().startActivity(i);
                }
        });


        return convertView;
    }
}
