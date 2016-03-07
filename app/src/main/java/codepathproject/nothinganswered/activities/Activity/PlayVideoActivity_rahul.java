package codepathproject.nothinganswered.activities.Activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import codepathproject.nothinganswered.R;

public class PlayVideoActivity_rahul extends AppCompatActivity {

    public  VideoView testVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_rahul);


         testVideo = (VideoView) findViewById(R.id.testVideo);
        testVideo.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
       // MediaController mediaController = new MediaController(this);
        testVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                testVideo.start();
            }
        });

    }
}
