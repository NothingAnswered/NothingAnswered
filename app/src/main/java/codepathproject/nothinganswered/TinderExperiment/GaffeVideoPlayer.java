package codepathproject.nothinganswered.TinderExperiment;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by jnagaraj on 3/14/16.
 */
public class GaffeVideoPlayer {

    private TextureView mTextureView;
    private Activity activity;
    private SurfaceTexture surfaceTexture;
    private MediaPlayer mediaPlayer;

    public GaffeVideoPlayer(Activity a){
        activity = a;
        mediaPlayer = new MediaPlayer();
    }

    public void startMediaPlayer(TextureView textureView){

        Log.i("VIDEO", "Start Media Player");
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
        public void onSurfaceTextureAvailable(SurfaceTexture st,
                                              int width, int height) {

            Toast.makeText(activity, "Is Now Available", Toast.LENGTH_SHORT).show();
            Log.i("VIDEO", "Texture just got available!");
            surfaceTexture = st;
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
        public void onSurfaceTextureUpdated(SurfaceTexture st) {

        }

    };

    public void play(TinderVideo video) {

        if(surfaceTexture == null) {
            surfaceTexture = mTextureView.getSurfaceTexture();
        }

        if(mTextureView.isAvailable() && surfaceTexture != null) {

            try {

                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
                Surface surface = new Surface(surfaceTexture);

                AssetFileDescriptor afd = video.getItemDescripter();
                if(mediaPlayer == null){
                    mediaPlayer = new MediaPlayer();
                }
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.setSurface(surface);
                //mediaPlayer.setLooping(true);
                mediaPlayer.prepareAsync();

                // Play video when the media source is ready for playback.
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        } else {
                            mediaPlayer.start();
                        }
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                });

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        mediaPlayer.release();
                        return true;
                    }
                });



            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

    }

    public void resetAndRelease(){
        mediaPlayer.reset();
        mediaPlayer.release();

    }

/*
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);


    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    public void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

}
