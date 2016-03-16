package codepathproject.nothinganswered.fragments;

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
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import codepathproject.nothinganswered.NothingAnsweredApplication;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.adapters.CustomGridLayoutManager;
import codepathproject.nothinganswered.adapters.ParseQuestionAdapter;
import codepathproject.nothinganswered.adapters.RecordActionListener;
import codepathproject.nothinganswered.models.Friends;
import codepathproject.nothinganswered.models.Question;
import codepathproject.nothinganswered.views.AutoFitTextureView;

public class FragmentQuestionsReceived extends TimelineFragment {

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

    private ParseQuestionAdapter questionAdapter;

    public static FragmentQuestionsReceived newInstance() {
        return new FragmentQuestionsReceived();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createQuestionAdapter();
        setVideoEventListener();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        rvResults.setAdapter(questionAdapter);
        return view;
    }

    public void createQuestionAdapter() {
        ParseQueryAdapter.QueryFactory<Question> factory = new ParseQueryAdapter.QueryFactory<Question>() {
            public ParseQuery<Question> create() {
                ParseQuery<Question> query = ParseQuery.getQuery(Question.class);
                query.whereEqualTo(Question.RECIPIENT_ID, Friends.myId);
                query.whereEqualTo(Question.RESPONDED, "false");
                query.orderByDescending("createdAt");

                //-24 hours Date object
                Calendar calendar = Calendar.getInstance();
                Log.i(TAG, "Time " + getActivity().getString(R.string.max_lookback_time));
                calendar.add(Calendar.HOUR_OF_DAY, -Integer.parseInt(getActivity().getString(R.string.max_lookback_time)));
                query.whereGreaterThan("createdAt", calendar.getTime());
                return query;
            }
        };
        questionAdapter = new ParseQuestionAdapter(factory, true);
    }

    public void setVideoEventListener() {
        questionAdapter.setRecordActionListener(new RecordActionListener() {
            @Override
            public void onRecordButtonClick(View view, final int position) {
                mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
                mButtonVideo = (ImageButton) view.findViewById(R.id.openCamera);
                final ImageButton ibsendVideo = (ImageButton) view.findViewById(R.id.sendVideo);
                StartCameraPreview();
                mButtonVideo.setImageResource(R.drawable.record);
                Toast.makeText(getActivity(), "position " + position, Toast.LENGTH_SHORT).show();
                final TextView tvTimer = (TextView) view.findViewById(R.id.tvTimer);
                mButtonVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mIsRecordingVideo) {
                            if (mTimer != null) {
                                mTimer.cancel();
                                mTimer = null;
                            }
                            tvTimer.setVisibility(View.GONE);
                            mButtonVideo.setVisibility(View.GONE);
                            stopRecordingVideo(position);
                        } else {
                            startRecordingVideo();
                            mButtonVideo.setImageResource(R.drawable.stoprecord);
                            tvTimer.setVisibility(View.VISIBLE);
                            mTimer = new CountDownTimer(5000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    tvTimer.setText(millisUntilFinished / 1000 + "");
                                }

                                @Override
                                public void onFinish() {
                                    mTimer = null;
                                    tvTimer.setVisibility(View.GONE);
                                    mButtonVideo.setVisibility(View.GONE);
                                    ibsendVideo.setVisibility(View.GONE);
                                    stopRecordingVideo(position);
                                }
                            }.start();
                        }
                    }
                });

                ibsendVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Question question = questionAdapter.getItem(position);


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
                    Context context = getActivity();

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

    public void loadObjects() {
        questionAdapter.loadObjects();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static FragmentQuestionsReceived newInstance (int page)
    {
        FragmentQuestionsReceived fragment = new FragmentQuestionsReceived();
        return fragment;
    }

    private void setScrolling(boolean tf) {
        scrolling = tf;
        CustomGridLayoutManager manager = (CustomGridLayoutManager)rvResults.getLayoutManager();
        manager.setScrollEnabled(tf);
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {

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

        setScrolling(false);
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
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
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
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

        final Activity activity = getActivity();
        if (null == activity || activity.isFinishing()) {
            return;
        }
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.d("CAMERA_INFO", "tryAcquire");
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            String cameraId = manager.getCameraIdList()[0];

            // Choose the sizes for camera preview and video recording
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
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
            manager.openCamera(cameraId, mStateCallback, null);
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
                    Activity activity = getActivity();
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
        final Activity activity = getActivity();
        if (null == activity) {
            return;
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(getVideoFile(activity).getAbsolutePath());
        mMediaRecorder.setVideoEncodingBitRate(100000);
        mMediaRecorder.setVideoFrameRate(15);
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
        Picasso.with(getContext()).load(R.drawable.record).fit().centerInside().into(mButtonVideo);
        // Stop recording
        mMediaRecorder.stop();
        mMediaRecorder.reset();

        closeCamera();
        stopBackgroundThread();
        setScrolling(true);

        Question question = questionAdapter.getItem(position);
        updateQuestion(question);
        uploadVideo(question);
    }

    public void updateQuestion(Question question) {
        question.put(Question.LOCALVIDEOURL, getVideoFile(getActivity()).getAbsolutePath());
        question.put(Question.RESPONDED, "true");
        question.saveInBackground();
    }
    public void uploadVideo(Question question) {
        //Upload video
        parseClient = NothingAnsweredApplication.getParseClient();
        File file = new File(question.get(Question.LOCALVIDEOURL).toString());
        parseClient.sendVideoResponse(question.get(Question.SENDER_ID).toString(), question.get(Question.QUESTION).toString(), file);
        Toast.makeText(getActivity(), question.get(Question.QUESTION).toString(), Toast.LENGTH_SHORT).show();
    }


}
