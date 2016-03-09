package codepathproject.nothinganswered.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.models.GaffeCardModel;


public class GaffeSimpleCardStackAdapter extends GaffeCardStackAdapter {


    private TextView gaffeCardQuestion;
  //  private ImageView gaffeCardVideoThumbnail;
    private VideoView gaffeCardVideoThumbnail;
    private ImageView gaffeCardProfilePictureUrl;
    private TextView gaffeCardProfileName;

    public GaffeSimpleCardStackAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected View getCardView(int position, GaffeCardModel model, View convertView, ViewGroup parent) {
                if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_gaffecard, parent, false);
            assert convertView != null;
        }

        gaffeCardVideoThumbnail = (VideoView) convertView.findViewById(R.id.gaffeCardVideoThumbnail);
        gaffeCardVideoThumbnail.setVideoPath(model.getGaffeCardVideoThumbnail());
        gaffeCardQuestion = (TextView) convertView.findViewById(R.id.gaffeCardQuestion);
        gaffeCardQuestion.setText(model.getGaffeCardQuestion());
        gaffeCardProfileName = (TextView) convertView.findViewById(R.id.gaffeCardProfileName);
        gaffeCardProfileName.setText(model.getGaffeCardProfileName());


        gaffeCardVideoThumbnail.setMediaController(new MediaController(getContext()));
        gaffeCardVideoThumbnail.requestFocus();
        gaffeCardVideoThumbnail.setZOrderOnTop(true);

        gaffeCardVideoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gaffeCardVideoThumbnail.start();
            }
        });


        return convertView;

    }


}
