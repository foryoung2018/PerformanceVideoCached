package com.bisu.performancevideocached;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.bisu.serverlibrary.Constant;
import com.bisu.serverlibrary.io.NativeHelper;
import com.bisu.serverlibrary.server.CachedServer;

import java.util.Arrays;
import java.util.logging.Logger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

//    private String mockUrl = "https://cv.phncdn.com/videos/201912/16/269072851/1080P_4000K_269072851.mp4";
    private String mockUrl = "http://t8.baidu.com/it/u=3571592872,3353494284&fm=79&app=86&f=JPEG?w=1200&h=1290";
    private String localMp4  = "http://192.168.1.3/1.mp4";
    VideoView videoView;
    Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.videoView);
        play = findViewById(R.id.play);
        verifyStoragePermissions(this);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVideo();
            }
        });
        NativeHelper.ptr = NativeHelper.init("111");
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    long ptr;
    private void startVideo() {
//        NativeHelper.mmapWrite("11112222", getFilesDir().getAbsolutePath(),NativeHelper.ptr, "1.txt");


        CachedServer proxy = new CachedServer.Builder(this).build();
        String proxyUrl = proxy.getProxyUrl(localMp4);
        Log.d(Constant.TAG, "startVideo() called " + proxyUrl);
        videoView.setVideoPath(proxyUrl);
        videoView.start();


//        byte[] buffer = new byte[10];
//        Arrays.fill(buffer, (byte)3);
//         NativeHelper.mmapWriteByte(buffer, getFilesDir().getAbsolutePath(),NativeHelper.ptr, "1.txt");
    }
}
