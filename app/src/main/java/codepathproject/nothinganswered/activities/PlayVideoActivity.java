package codepathproject.nothinganswered.activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import codepathproject.nothinganswered.R;

public class PlayVideoActivity extends AppCompatActivity {

    public final static String VIDEO_URI = "video_uri";
    public static final String IMAGE_URI = "image_uri";
    private VideoView videoView;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        Uri videoUri = getIntent().getParcelableExtra(VIDEO_URI);
        videoView = (VideoView)findViewById(R.id.vvVideo);
        videoView.setVideoURI(videoUri);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVisibility(View.INVISIBLE);
                ivImage.setVisibility(View.VISIBLE);
            }
        });

        ivImage = (ImageView) findViewById(R.id.ivImage);

        Uri imageUri = getIntent().getParcelableExtra(IMAGE_URI);
        Picasso.with(this).load(imageUri).into(ivImage);

    }

    public void onThumbnailClick(View view){

        ivImage.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.VISIBLE);
        videoView.start();

    }
}
