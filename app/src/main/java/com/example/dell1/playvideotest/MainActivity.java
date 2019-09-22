package com.example.dell1.playvideotest;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,SurfaceHolder.Callback {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private VideoView videoView;
    private MediaController controller;

    private SurfaceView sv;
    private SurfaceHolder holder;
    private MediaPlayer mediaPlayer2;
    private RelativeLayout rl;
    private Timer timer;
    private TimerTask task;
    private SeekBar sbar;
    private ImageView play2;
    private static final int END =1;
    private static final int PLAYING=0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
           switch (msg.what){
               case END:

                   play2.setImageResource(android.R.drawable.ic_media_play);

                   break;
               case PLAYING:

                       //第一次点击屏幕后隐藏播放按钮和进度条
                    //  rl.setVisibility(View.INVISIBLE);

               default:
                   break;
           }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Button playMusic = (Button) findViewById(R.id.playMusic);
        Button pauseMusic = (Button) findViewById(R.id.pauseMusic);
        Button stopMusic = (Button) findViewById(R.id.stopMusic);
        Button loopMusic = (Button) findViewById(R.id.loopMusic);
        playMusic.setOnClickListener(this);
        pauseMusic.setOnClickListener(this);
        stopMusic.setOnClickListener(this);
        loopMusic.setOnClickListener(this);

         String url = "android.resource://" + getPackageName() + "/" + R.raw.yegui7;
         Uri uri = Uri.parse(url);
         videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoURI(uri);
        controller = new MediaController(this);
        videoView.setMediaController(controller);

        Button play = (Button) findViewById(R.id.play);
        Button pause = (Button) findViewById(R.id.pause);
        Button replay = (Button) findViewById(R.id.replay);
        Button loop = (Button) findViewById(R.id.loop);
      //  Button stop = (Button) findViewById(R.id.stop);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        replay.setOnClickListener(this);
        loop.setOnClickListener(this);
       // stop.setOnClickListener(this);



        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }else {
            initMediaPlayer();
           // initVideoPath();

        }

        sv = (SurfaceView) findViewById(R.id.sv);
        holder = sv.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//4.0以上系统不写这句没事
        holder.addCallback(this);
        rl = (RelativeLayout) findViewById(R.id.rl);
        play2 =(ImageView) findViewById(R.id.play_surf);
        sbar = (SeekBar) findViewById(R.id.sbar);
        play2.setOnClickListener(this);
        sbar.setOnSeekBarChangeListener(this);
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                if(mediaPlayer2 !=null && mediaPlayer2.isPlaying()){
                    message.what = PLAYING;
                    handler.sendMessage(message);
                    int total = mediaPlayer2.getDuration();
                    sbar.setMax(total);
                    int progress = mediaPlayer2.getCurrentPosition();
                    sbar.setProgress(progress);
                }else {

                    message.what = END;
                    handler.sendMessage(message);


                    //  android.content.res.Resources$NotFoundException: Resource ID #0x0
                 //  play2.setImageResource(android.R.drawable.ic_media_play);
                    //不能在子线程中对UI进行操作
                    //android.view.ViewRootImpl$CalledFromWrongThreadException:
                    // Only the original thread that created a view hierarchy can touch its views
                    //下面这句去掉再点击好像没闪退了  换到外面去了
                  //  play2.setImageResource(android.R.drawable.ic_media_play);


                }
            }
        };
        timer.schedule(task,500,500);



    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        try{
            mediaPlayer2 =new MediaPlayer();
            mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //漏掉://  导致FileNotFoundException
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+
            getPackageName() + "/" + R.raw.yegui);
            try{
                mediaPlayer2.setDataSource(MainActivity.this,uri);
            }catch (IOException e){
                Toast.makeText(MainActivity.this,"播放失败",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            mediaPlayer2.setDisplay(holder);
            mediaPlayer2.prepareAsync();
            mediaPlayer2.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer mp){
                    //
                   // mediaPlayer2.start();//这个不去掉程序已启动就会播放
                }
            });
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"播放失败",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int which,int height){

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        if(mediaPlayer2.isPlaying()){
            mediaPlayer2.stop();
        }
    }


    private void initMediaPlayer(){
        try{
            File file = new File(Environment.getExternalStorageDirectory(),"music.mp3");
            //   Log.d("路径",Environment.getExternalStorageDirectory()+"");
            //   Log.d("路径",file.getPath());
            Log.i("音乐文件路径", file.getPath());
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
            //    Log.d("提示",e.getMessage());
        }
    }



   /*private void initVideoPath(){
        try{
            File file = new File(Environment.getExternalStorageDirectory(),"pp.mp4");
            //   Log.d("路径",Environment.getExternalStorageDirectory()+"");
            //   Log.d("路径",file.getPath());
            videoView.setVideoPath(file.getPath());
        }catch (Exception e){
            e.printStackTrace();
            //    Log.d("提示",e.getMessage());
        }
    }*/
   private void play(){
       if(videoView !=null && videoView.isPlaying()){
           videoView.stopPlayback();//这个方法这里不会执行 因为外部条件 ！videoView.isPlaying()
           return;
       }
       videoView.start();
      /* videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
           @Override
           public void onCompletion(MediaPlayer mp){
               //这里处理视频播放完时所需内容
           }
       });*/
   }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   // initVideoPath();
                    initMediaPlayer();
                }else{
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.playMusic:
                if(!mediaPlayer.isPlaying()){

                    mediaPlayer.start();
                    Toast.makeText(this,"开始播放",Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.loopMusic:
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                    Toast.makeText(this,"单曲循环",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pauseMusic:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    Toast.makeText(this,"暂停播放",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stopMusic:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.reset();
                    initMediaPlayer();
                    Toast.makeText(this,"停止播放",Toast.LENGTH_SHORT).show();
                }
                break;




            case R.id.play:
             if(!videoView.isPlaying()){
                    play();
                Toast.makeText(this,"开始播放",Toast.LENGTH_SHORT).show();
                   // videoView.start();

                }
                break;
            case R.id.loop:
                  //点击此按钮后循环播放

                    //监听视频播放完的代码
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mPlayer) {
                     // TODO Auto-generated method stub
                            mPlayer.start();
                            mPlayer.setLooping(true);

                        }
                    });
                Toast.makeText(this,"循环播放",Toast.LENGTH_SHORT).show();


                break;

            case R.id.pause:
                if(videoView.isPlaying()){
                    videoView.pause();
                    Toast.makeText(this,"暂停播放",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.replay:
                if(videoView.isPlaying()){

                    videoView.resume();
                    Toast.makeText(this,"重新播放",Toast.LENGTH_SHORT).show();
                }
                break;
           /* case R.id.stop:
                if(videoView.isPlaying()){//

                    videoView.stopPlayback();//视频停止播放应该不是用这个 用暂停方法就好了
                    videoView.;

                   // initVideoPath();
                    Toast.makeText(this,"停止播放",Toast.LENGTH_SHORT).show();

                }
                break;*/
            case R.id.play_surf:

                if(mediaPlayer2 != null && mediaPlayer2.isPlaying()){
                    mediaPlayer2.pause();
                    play2.setImageResource(android.R.drawable.ic_media_play);
                }else{
                    mediaPlayer2.start();
                    play2.setImageResource(android.R.drawable.ic_media_pause);
                }
                break;

            default:
                break;
        }
    }
    @Override
    public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
       //进度发生变化时触发
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar){
       //进度条开始拖动时触发
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar){
        //进度条拖动停止时触发
        int position = seekBar.getProgress();
        if(mediaPlayer2 !=null){
            mediaPlayer2.seekTo(position);//将进度条的拖动位置设置给MediaPlayer对象
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){//屏幕触摸事件
       switch (event.getAction()){
           case MotionEvent.ACTION_DOWN:
               if(rl.getVisibility() == View.INVISIBLE){//进度条和播放按钮不显示
                   rl.setVisibility(View.VISIBLE);
                   CountDownTimer cdt = new CountDownTimer(3000,1000) {
                       @Override
                       public void onTick(long millisUnitlFinished) {
                           System.out.println(millisUnitlFinished);

                       }

                       @Override
                       public void onFinish() {
                           rl.setVisibility(View.INVISIBLE);

                       }
                   };
                   cdt.start();

               }else if(rl.getVisibility() == View.VISIBLE ){
                   rl.setVisibility(View.INVISIBLE);

               }
               break;
       }
       return super.onTouchEvent(event);
    }




    @Override
    protected void onDestroy(){
       task.cancel();
       timer.cancel();
       timer = null;
       task = null;
       mediaPlayer2.release();
       mediaPlayer2 = null;


        super.onDestroy();
        if(videoView !=null ){

          videoView.suspend();
        }
        if(mediaPlayer !=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }


    }


}
