package codepathproject.nothinganswered.TinderExperiment;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.tindercard.FlingCardListener;
import codepathproject.nothinganswered.tindercard.SwipeFlingAdapterView;
import codepathproject.nothinganswered.views.AutoFitTextureView;

/**
 * Created by jnagaraj on 3/13/16.
 */
public class ActivityTinder extends AppCompatActivity implements FlingCardListener.ActionDownInterface {

    private ArrayList<Data> al;
    private GaffeCamera mCamera;
    private CountDownTimer mTimer;
    ImageButton mButtonVideo;

    private SwipeFlingAdapterView flingContainer;

    private TinderSimpleAdapter questionAdapter;

    @Override
    public void onSwipeLeftOrRight() {

        questionAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCamera = new GaffeCamera(this);

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
        final AutoFitTextureView mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        mButtonVideo = (ImageButton) view.findViewById(R.id.openCamera);
        final ImageButton ibsendVideo = (ImageButton) view.findViewById(R.id.sendVideo);

        final ImageView ivPlayIcon = (ImageView) view.findViewById(R.id.ivPlayIcon);
        final ImageView ivVideoImage = (ImageView) view.findViewById(R.id.ivVideoImage);
        mCamera.StartCameraPreview(mTextureView);

        mButtonVideo.setImageResource(R.drawable.record);

        final TextView tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        mButtonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mCamera.isRecordingVideo()) {
                    mCamera.stopRecordingVideo();
                    tvTimer.setVisibility(View.INVISIBLE);
                } else {
                    mCamera.startRecordingVideo();
                    mButtonVideo.setImageResource(R.drawable.stoprecord);
                    tvTimer.setVisibility(View.VISIBLE);
                    mTimer = new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvTimer.setText(millisUntilFinished / 1000 + "");
                        }

                        @Override
                        public void onFinish() {
                            mCamera.stopRecordingVideo();
                            ibsendVideo.setVisibility(View.VISIBLE);
                            ivPlayIcon.setVisibility(View.VISIBLE);
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

        ivPlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    final MediaPlayer mediaPlayer = new MediaPlayer();
                    Context context = ActivityTinder.this;

                    mediaPlayer.setDataSource(context, Uri.fromFile(mCamera.getVideoFile(context)));

                    mediaPlayer.setSurface(mCamera.GetSurface());
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
                            ivVideoImage.setVisibility(View.INVISIBLE);
                        }
                    });

                } catch (IllegalArgumentException e) {
                    Log.d("", e.getMessage());
                } catch (SecurityException e) {
                    Log.d("", e.getMessage());
                } catch (IllegalStateException e) {
                    Log.d("", e.getMessage());
                } catch (IOException e) {
                    Log.d("", e.getMessage());
                }
            }
        });

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
