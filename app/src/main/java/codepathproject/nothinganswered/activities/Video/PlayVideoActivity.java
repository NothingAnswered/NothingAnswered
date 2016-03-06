package codepathproject.nothinganswered.activities.Video;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import codepathproject.nothinganswered.R;

public class PlayVideoActivity extends AppCompatActivity {

    public final static String VIDEO_URI = "video_uri";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        Uri videoUri = getIntent().getParcelableExtra(VIDEO_URI);
        VideoView videoView = (VideoView)findViewById(R.id.vvVideo);
        videoView.setVideoURI(videoUri);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
    }
}
