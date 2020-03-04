package com.bisu.performancevideocached;

import android.os.Bundle;
import android.widget.VideoView;

import com.bisu.serverlibrary.server.CachedServer;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String mockUrl = "";
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.videoView);
        startVideo();
    }

    private void startVideo() {
        CachedServer proxy = new CachedServer.Builder().build();
        String proxyUrl = proxy.getProxyUrl(mockUrl);
        videoView.setVideoPath(proxyUrl);
        videoView.start();
    }
}
