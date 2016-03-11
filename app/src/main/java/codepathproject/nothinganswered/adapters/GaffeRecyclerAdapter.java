package codepathproject.nothinganswered.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.models.Gaffe;

/**
 * Created by jnagaraj on 3/6/16.
 */
public class GaffeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private RecordActionListener listener;

    private List<Gaffe> mGaffes;

    public GaffeRecyclerAdapter(List<Gaffe> gaffes) {
        mGaffes = gaffes;
    }

    public void setRecordActionListener(RecordActionListener listener){
        this.listener = listener;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        GaffeItemHolder gaffeItemHolder;
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_gaffecard, parent, false);
        gaffeItemHolder = new GaffeItemHolder(view);

        return gaffeItemHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        GaffeItemHolder gaffeHolder = (GaffeItemHolder) holder;
        gaffeHolder.loadDataIntoView(mGaffes.get(position), context);

    }

    @Override
    public int getItemCount() {
        return mGaffes.size();
    }

    public class GaffeItemHolder extends RecyclerView.ViewHolder {
        private TextView mQuestionTitle;
        private TextView mUsername;
        private Button mOpenCamera;

        private ImageView mProfileImage;

        public GaffeItemHolder(final View itemView) {
            super(itemView);

            mQuestionTitle = (TextView)itemView.findViewById(R.id.gaffeCardQuestion);
            mUsername = (TextView)itemView.findViewById(R.id.gaffeCardProfileName);
            mOpenCamera = (Button)itemView.findViewById(R.id.openCamera);

            mOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(listener != null) {
                        listener.onRecordButtonClick(itemView, getLayoutPosition());
                    }
                }
            });
            mProfileImage = (ImageView) itemView.findViewById(R.id.gaffeCardProfilePictureUrl);

        }

        public void loadDataIntoView(Gaffe gaffe, Context context) {
            mQuestionTitle.setText(gaffe.getQuestionTitle());
            mUsername.setText(gaffe.getUsername());
            Log.i("DEBUG", gaffe.getQuestionTitle());
            mProfileImage.setImageResource(0);
            Picasso.with(context).load(gaffe.getProfilePicUrl()).placeholder(R.drawable.ic_launcher).into(mProfileImage);
        }


    }
}
