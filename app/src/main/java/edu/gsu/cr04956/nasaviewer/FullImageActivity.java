package edu.gsu.cr04956.nasaviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import edu.gsu.cr04956.nasaviewer.R;

import static android.R.attr.bitmap;

public class FullImageActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URL = "edu.gsu.cr04956.nasaviewer.fullimageactivity.url";
    public static final String EXTRA_STREAM_URL = "edu.gsu.cr04956.nasaviewer.fullimageactivity.streamurl";
    public static final String EXTRA_CAPTION = "edu.gsu.cr04956.nasaviewer.fullimageactivity.caption";
    private MediaPlayer mediaPlayer;
    private static final String MY_CLIENT_ID = "PLACEHOLDER_ID";
    private boolean isMediaPrepared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        ThumbnailDownloader<ImageView> downloader;
        Intent intent = getIntent();
        ImageView imgView = (ImageView) findViewById(R.id.imgView);
        String imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);
        String streamUrl = intent.getStringExtra(EXTRA_STREAM_URL);
        String caption = intent.getStringExtra(EXTRA_CAPTION);

        Handler responseHandler = new Handler();
        downloader = new ThumbnailDownloader<>(responseHandler);
        downloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<ImageView>() {
                    @Override
                    public void onThumbnailDownloaded(ImageView view, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        view.setImageDrawable(drawable);
                    }
                }
        );
        downloader.start();
        downloader.getLooper();


        downloader.queueThumbnail(imgView, imageUrl);

        TextView captionView = (TextView) findViewById(R.id.txtCaption);
        captionView.setText(caption);

        //allow_redirects=False
        //client_id=MY_CLIENT_ID
        final String soundUrl = Uri.parse(streamUrl)
                .buildUpon()
                .appendQueryParameter("client_id", MY_CLIENT_ID)
                .appendQueryParameter("allow_redirects", "False")
                .build().toString();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        AsyncTask task = new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object[] params) {

                try {
                    mediaPlayer.setDataSource(soundUrl);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    isMediaPrepared = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;

            }
        };
        task.execute(new Object());


    }

    @Override
    public void onPause() {
        super.onPause();
        if(isMediaPrepared && mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    public void onResume() {
        super.onResume();
        if(isMediaPrepared && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
}
