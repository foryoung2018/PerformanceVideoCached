package com.bisu.performancevideocached;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.bisu.serverlibrary.server.CachedServer;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String mockUrl = "1";
    VideoView videoView;
    Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.videoView);
        play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVideo();
            }
        });
    }

    private void startVideo() {
        CachedServer proxy = new CachedServer.Builder().build();
        String proxyUrl = proxy.getProxyUrl(mockUrl);
        videoView.setVideoPath(proxyUrl);
        videoView.start();
    }
}
