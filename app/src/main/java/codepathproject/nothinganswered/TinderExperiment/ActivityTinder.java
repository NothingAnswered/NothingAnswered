package codepathproject.nothinganswered.TinderExperiment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.astuetz.PagerSlidingTabStrip;

import codepathproject.nothinganswered.R;

/**
 * Created by jnagaraj on 3/13/16.
 */
public class ActivityTinder extends AppCompatActivity {


    private ViewPager vpPager;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinder);

        vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(new TinderPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
        fragmentManager = getSupportFragmentManager();
    }


/*    public void setVideoEventListener() {
        questionAdapter.setRecordActionListener(new RecordActionListener() {
            @Override
            public void onRecordButtonClick(View view, final int position) {

                Toast.makeText(ActivityTinder.this, "position " + position, Toast.LENGTH_SHORT).show();
                mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
                mButtonVideo = (ImageButton) view.findViewById(R.id.openCamera);
                final ImageButton ibsendVideo = (ImageButton) view.findViewById(R.id.sendVideo);

                GaffeCamera.Instance().StartCameraPreview();

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
    }*/










}
