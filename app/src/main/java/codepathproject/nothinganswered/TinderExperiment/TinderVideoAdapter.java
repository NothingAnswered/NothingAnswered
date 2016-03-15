package codepathproject.nothinganswered.TinderExperiment;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.views.AutoFitTextureView;

/**
 * Created by jnagaraj on 3/14/16.
 */
public class TinderVideoAdapter extends BaseAdapter {

    public List<TinderVideo> videos;
    public Context context;
    private MediaPlayer mediaPlayer;

    public TinderVideoAdapter(List<TinderVideo> videoList, Context context, MediaPlayer player) {
        this.videos = videoList;
        this.context = context;
        this.mediaPlayer = player;
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
            rowView = inflater.inflate(R.layout.tinder_card_item, parent, false);
            // configure view holder
            viewHolder = new ViewHolder(rowView);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //viewHolder.question.setText(videos.get(position).getDescription() + "");

        return rowView;
    }

    public class ViewHolder {

        View view;
        public TextView question;
        private ImageButton mOpenCamera;
        private TextView mTimeStamp;
        private AutoFitTextureView textureView;
        private SurfaceTexture surfaceTexture;
        private ImageView thumbnail;
        private TinderVideo video;

        public ViewHolder(View v) {
            view = v;

            question = (TextView) rowView.findViewById(R.id.gaffeCardQuestion);
            mOpenCamera = (ImageButton)rowView.findViewById(R.id.openCamera);
            mTimeStamp = (TextView) rowView.findViewById(R.id.tvQuestionTimeStamp);
            textureView = (AutoFitTextureView)rowView.findViewById(R.id.texture);
            thumbnail = (ImageView)rowView.findViewById(R.id.ivVideoImage);
            rowView.setTag(this);

            mOpenCamera.setVisibility(View.INVISIBLE);

            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture sTexture, int width, int height) {

                    surfaceTexture = sTexture;

                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
            textureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        thumbnail.setVisibility(View.VISIBLE);
                    }
                }
            });

            thumbnail.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(textureView.isAvailable() && surfaceTexture != null) {
                        Surface surface = new Surface(surfaceTexture);
                        thumbnail.setVisibility(View.INVISIBLE);

                        try {
                            //AssetFileDescriptor afd = context.getAssets().openFd(itemVideo.getItemDescripter());
                            AssetFileDescriptor afd = context.getAssets().openFd(video.stringFileName);
                            //mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            mediaPlayer.setSurface(surface);
                            mediaPlayer.setLooping(true);
                            mediaPlayer.prepareAsync();

                            // Play video when the media source is ready for playback.
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {

                                    if(mediaPlayer.isPlaying()){
                                        mediaPlayer.stop();
                                    }else {
                                        mediaPlayer.start();
                                    }
                                }
                            });

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    mediaPlayer.stop();
                                    thumbnail.setVisibility(View.VISIBLE);
                                }
                            });


                        } catch (IllegalArgumentException e) {
                            Log.d("VIDEO", e.getMessage());
                        } catch (SecurityException e) {
                            Log.d("VIDEO", e.getMessage());
                        } catch (IllegalStateException e) {
                            Log.d("VIDEO", e.getMessage());
                        } catch (IOException e) {
                            Log.d("VIDEO", e.getMessage());
                        }
                    }
                }
            });

        }
    }

}

