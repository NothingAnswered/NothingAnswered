package codepathproject.nothinganswered.TinderExperiment;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by jnagaraj on 3/14/16.
 */
public class FragmentTinderQuestionsTimeline extends Fragment implements FlingCardListener.ActionDownInterface{

    private ArrayList<Data> al;
    private GaffeCamera mCamera;

    private CountDownTimer mTimer;
    ImageButton mButtonVideo;

    private SwipeFlingAdapterView flingContainer;
    private ParseListQueryAdapter questionAdapter;
    //private TinderQuestionAdapter questionAdapter;

    public static FragmentTinderQuestionsTimeline newInstance (int page)
    {
        FragmentTinderQuestionsTimeline fragment = new FragmentTinderQuestionsTimeline();
        return fragment;
    }

    @Override
    public void onSwipeLeftOrRight() {
        questionAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tinder_fling_view, container, false);

        mCamera = new GaffeCamera(this.getActivity());

        al = new ArrayList<>();
        al.add(new Data("This is a first question"));
        al.add(new Data("This is a second question"));
        al.add(new Data("This is a third question"));

        questionAdapter = new ParseListQueryAdapter(this.getContext());

        flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);

        flingContainer.setAdapter(questionAdapter);
        questionAdapter.notifyDataSetChanged();
        questionAdapter.setPaginationEnabled(true);
        questionAdapter.loadObjects();

        questionAdapter.notifyDataSetChanged();

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //al.remove(0);
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
                //al.remove(0);
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
                performCameraActions(view, flingContainer.getFirstVisiblePosition());

                view.findViewById(R.id.background).setAlpha(0);

                questionAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void performCameraActions(View view, final int position) {

        Toast.makeText(getActivity(), "position " + position, Toast.LENGTH_SHORT).show();
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
                } else if(!mCamera.isRecordingVideo()){
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
                    Context context = getActivity();

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
}
