package com.bisu.performancevideocached;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.bisu.serverlibrary.Constant;
import com.bisu.serverlibrary.server.CachedServer;

import java.util.logging.Logger;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

//    private String mockUrl = "https://cv.phncdn.com/videos/201912/16/269072851/1080P_4000K_269072851.mp4";
    private String mockUrl = "http://t8.baidu.com/it/u=3571592872,3353494284&fm=79&app=86&f=JPEG?w=1200&h=1290";
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
        Log.d(Constant.TAG, "startVideo() called " + proxyUrl);
        videoView.setVideoPath(proxyUrl);
        videoView.start();
    }
}
