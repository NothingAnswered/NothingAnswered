package codepathproject.nothinganswered.TinderExperiment;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

/**
 * Created by jnagaraj on 3/14/16.
 */
public class GaffeVideoPlayer {

    private TextureView mTextureView;
    private Activity activity;
    private Surface mSurface;
    private MediaPlayer mediaPlayer;

    public GaffeVideoPlayer(Activity a){
        activity = a;
        mediaPlayer = new MediaPlayer();

    }

    public void StartMediaPlayer(TextureView textureView){

        mTextureView = textureView;
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    }

    public boolean isVideoPlaying() {
        if(mediaPlayer != null) {

            return mediaPlayer.isPlaying();
        }
        return false;
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {

            //Toast.makeText(getApplicationContext(), "Is Now Available", Toast.LENGTH_SHORT).show();
            mSurface = new Surface(surfaceTexture);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

    };

    public void play(TinderVideo video) {

        Log.i("VIDEO", "Trying to Play" + video.stringFileName);
        if(mTextureView.isAvailable() && mSurface != null) {

            try {
                //AssetFileDescriptor afd = context.getAssets().openFd(itemVideo.getItemDescripter());
                AssetFileDescriptor afd = video.getItemDescripter();
                //AssetFileDescriptor afd = activity.getAssets().openFd(filename);
                //mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.setSurface(mSurface);
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

    public void stop() {
        mediaPlayer.stop();
    }

}
