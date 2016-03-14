package codepathproject.nothinganswered.TinderExperiment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.RecordActionListener;
import codepathproject.nothinganswered.clients.FacebookClient;
import codepathproject.nothinganswered.clients.ParseClient;
import codepathproject.nothinganswered.fragments.FragmentQuestionsReceived;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.tindercard.FlingCardListener;
import codepathproject.nothinganswered.tindercard.SwipeFlingAdapterView;
import codepathproject.nothinganswered.views.AutoFitTextureView;

/**
 * Created by jnagaraj on 3/13/16.
 */
public class ActivityTinder extends AppCompatActivity implements FlingCardListener.ActionDownInterface {

    public ParseClient parseClient;
    public FacebookClient facebookClient;
    public Friends friends;

    private ArrayList<Data> al;

    private final static int CAMERA_RQ = 6969;

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private CameraDevice mCameraDevice;
    private Size mPreviewSize;
    private MediaRecorder mMediaRecorder;
    private CaptureRequest.Builder mPreviewBuilder;
    private Size mVideoSize;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    //to play video on a texture
    private Surface mSurface;
    private CameraCaptureSession mPreviewSession;

    private static final String TAG = "QuestionsRecieved";
    private CountDownTimer mTimer;
    AutoFitTextureView mTextureView;
    ImageButton mButtonVideo;

    private SwipeFlingAdapterView flingContainer;

    private TinderSimpleAdapter questionAdapter;

    public static ActivityTinder newInstance() {
        return new ActivityTinder();
    }

    @Override
    public void onSwipeLeftOrRight() {

        questionAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tinder_fling_view);

        al = new ArrayList<>();
        al.add(new Data("This is a first question"));
        al.add(new Data("This is a second question"));
        al.add(new Data("This is a third question"));

        questionAdapter = new TinderSimpleAdapter(al, this);

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(questionAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                al.remove(0);
                questionAdapter.notifyDataSetChanged();
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

            }

            @Override
            public void onRightCardExit(Object dataObject) {

                //Data data = al.remove(0);
                //al.add(data);
                //al.set(lastIndex, data);
                al.remove(0);
                questionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {


            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }


        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

                View view = flingContainer.getSelectedView();
                performCameraActions(view, flingContainer.getId());

                view.findViewById(R.id.background).setAlpha(0);

                questionAdapter.notifyDataSetChanged();
            }
        });

    }


    private void performCameraActions(View view, final int position) {

        Toast.makeText(ActivityTinder.this, "position " + position, Toast.LENGTH_SHORT).show();
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        mButtonVideo = (ImageButton) view.findViewById(R.id.openCamera);
        final ImageButton ibsendVideo = (ImageButton) view.findViewById(R.id.sendVideo);

        StartCameraPreview();

        mButtonVideo.setImageResource(R.drawable.record);

        final TextView tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        mButtonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mIsRecordingVideo) {
                    stopRecordingVideo(position);
                    tvTimer.setVisibility(View.INVISIBLE);
                } else {
                    startRecordingVideo();
                    mButtonVideo.setImageResource(R.drawable.stoprecord);
                    //mButtonVideo.setVisibility(View.VISIBLE);
                    tvTimer.setVisibility(View.VISIBLE);
                    mTimer = new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvTimer.setText(millisUntilFinished / 1000 + "");
                        }

                        @Override
                        public void onFinish() {
                            stopRecordingVideo(position);
                            ibsendVideo.setVisibility(View.VISIBLE);
                        }
                    }.start();
                }
            }
        });

        ibsendVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }


    public void setVideoEventListener() {
        questionAdapter.setRecordActionListener(new RecordActionListener() {
            @Override
            public void onRecordButtonClick(View view, final int position) {

                Toast.makeText(ActivityTinder.this, "position " + position, Toast.LENGTH_SHORT).show();
                mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
                mButtonVideo = (ImageButton) view.findViewById(R.id.openCamera);
                final ImageButton ibsendVideo = (ImageButton) view.findViewById(R.id.sendVideo);

                StartCameraPreview();

                mButtonVideo.setImageResource(R.drawable.record);

                final TextView tvTimer = (TextView) view.findViewById(R.id.tvTimer);
                mButtonVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mIsRecordingVideo) {
                            stopRecordingVideo(position);
                            tvTimer.setVisibility(View.INVISIBLE);
                        } else {
                            startRecordingVideo();
                            mButtonVideo.setImageResource(R.drawable.stoprecord);
                            //mButtonVideo.setVisibility(View.VISIBLE);
                            tvTimer.setVisibility(View.VISIBLE);
                            mTimer = new CountDownTimer(5000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    tvTimer.setText(millisUntilFinished / 1000 + "");
                                }

                                @Override
                                public void onFinish() {
                                    stopRecordingVideo(position);
                                    ibsendVideo.setVisibility(View.VISIBLE);
                                }
                            }.start();
                        }
                    }
                });

                ibsendVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });
            }

            @Override
            public void onPlayButtonClick(View view, final int position) {
                final ImageView ivPlayIcon = (ImageView) view.findViewById(R.id.ivPlayIcon);
                final ImageView ivVideoImage = (ImageView) view.findViewById(R.id.ivVideoImage);

                ivPlayIcon.setVisibility(View.INVISIBLE);
                ivVideoImage.setVisibility(View.INVISIBLE);
                mTextureView.setVisibility(View.VISIBLE);


                try {
                    final MediaPlayer mediaPlayer = new MediaPlayer();
                    Context context = ActivityTinder.this;

                    mediaPlayer.setDataSource(context, Uri.fromFile(getVideoFile(context)));
                    mediaPlayer.setSurface(mSurface);
                    mediaPlayer.setLooping(false);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            ivPlayIcon.setVisibility(View.VISIBLE);
                            ivVideoImage.setVisibility(View.VISIBLE);
                        }

                    });

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                        }
                    });

                } catch (IllegalArgumentException e) {
                    Log.d(TAG, e.getMessage());
                } catch (SecurityException e) {
                    Log.d(TAG, e.getMessage());
                } catch (IllegalStateException e) {
                    Log.d(TAG, e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }

            }
        });
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {

            Toast.makeText(getApplicationContext(), "Is Now Available", Toast.LENGTH_SHORT).show();
            mSurface = new Surface(surfaceTexture);

            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

    };

    private void StartCameraPreview() {

        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            Toast.makeText(this, "Is Available", Toast.LENGTH_SHORT).show();
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            mButtonVideo.setImageResource(R.drawable.stoprecord);
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
            mCameraOpenCloseLock.release();
            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = ActivityTinder.this;
            if (null != activity) {
                activity.finish();
            }
        }

    };

    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = ActivityTinder.this;
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    private void openCamera(int width, int height) {

        final Activity activity = ActivityTinder.this;
        if (null == activity || activity.isFinishing()) {
            return;
        }
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.d("CAMERA_INFO", "tryAcquire");
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            //String cameraId = manager.getCameraIdList()[0];
            String cameraIdForFrontFacing = "";


            for(String cameraId : manager.getCameraIdList()){
                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if(facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {

                    Toast.makeText(this, "FRONT CAMERA", Toast.LENGTH_SHORT).show();
                    cameraIdForFrontFacing = cameraId;
                }
            }

            if(cameraIdForFrontFacing.equals("")){
                Toast.makeText(this, "BACK CAMERA", Toast.LENGTH_SHORT).show();
                cameraIdForFrontFacing = manager.getCameraIdList()[0];
            }
            // Choose the sizes for camera preview and video recording
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIdForFrontFacing);

            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    width, height, mVideoSize);

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }
            configureTransform(width, height);
            mMediaRecorder = new MediaRecorder();
            manager.openCamera(cameraIdForFrontFacing, mStateCallback, null);
        } catch (CameraAccessException e) {
            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            activity.finish();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.

        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }

    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }


    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }


    private void startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            setUpMediaRecorder();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<Surface>();

            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Activity activity = ActivityTinder.this;
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpMediaRecorder() throws IOException {
        final Activity activity = ActivityTinder.this;
        if (null == activity) {
            return;
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(getVideoFile(activity).getAbsolutePath());
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = ORIENTATIONS.get(rotation);
        mMediaRecorder.setOrientationHint(orientation);
        mMediaRecorder.prepare();
    }

    private File getVideoFile(Context context) {
        return new File(context.getExternalFilesDir(null), "video.mp4");
    }

    private boolean mIsRecordingVideo;

    private void startRecordingVideo() {
        try {

            mIsRecordingVideo = true;

            // Start recording
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void stopRecordingVideo(int position) {
        mIsRecordingVideo = false;
        Picasso.with(ActivityTinder.this).load(R.drawable.record).fit().centerInside().into(mButtonVideo);
        // Stop recording
        mMediaRecorder.stop();
        mMediaRecorder.reset();

        Activity activity = ActivityTinder.this;
        if (null != activity) {
            Toast.makeText(activity, "Video saved: " + getVideoFile(activity),
                    Toast.LENGTH_SHORT).show();
        }

        //startPreview();
        closeCamera();
        stopBackgroundThread();
    }


}
