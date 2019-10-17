package com.example.videoviewtest;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mController;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取界面上的VideoView组件
        videoView = findViewById(R.id.video);
        // 创建MediaController对象
        mController = new MediaController(this);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0x123);
    }

    @Override public void onRequestPermissionsResult(int requestCode,
                                                     @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == 0x123
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 设为横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //打开手机存储就已经进入了sdcard 没有mnt的文件夹在手机上看
            File video = new File("/mnt/sdcard/dengshaofeng.mp4");
            if (video.exists()) {
                videoView.setVideoPath(video.getAbsolutePath()); // ①
                // 设置videoView与mController建立关联
                videoView.setMediaController(mController);  // ②
                // 设置mController与videoView建立关联
                mController.setMediaPlayer(videoView);  // ③
                // 让VideoView获取焦点
                videoView.requestFocus();
                videoView.start(); // 开始播放
            }
        }
    }
}
