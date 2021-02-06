package com.androidx.ao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.VideoView;
import android.view.ViewGroup;
import java.io.File;
import android.widget.RelativeLayout;
import android.media.MediaPlayer;
import android.annotation.NonNull;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.view.View;
import android.widget.TextView;
import android.graphics.Typeface;
import android.widget.Button;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation;
import android.animation.ObjectAnimator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import android.graphics.Color;
import android.text.TextPaint;

public class MainActivity extends AppCompatActivity {
    
    public static final String VIDEO_NAME = "running.mp4";

    private VideoView mVideoView;

    private ViewGroup container;
    
    TextView appName;
    private TextView nullTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);
        
        nullTV = (TextView)findViewById(R.id.nullTV);
		Button tv = (Button) findViewById(R.id.activitymainButton1);
        Typeface face = Typeface.createFromAsset(getAssets(),
                                                 "fonts/SAOWelcomeTT-Bold.ttf");
		tv.setTypeface(face);
        TextView tv2 = (TextView) findViewById(R.id.activitymainTextView1);
        Typeface face2 = Typeface.createFromAsset(getAssets(),
                                                 "fonts/SAOWelcomeTT-Bold.ttf");
		tv2.setTypeface(face2);
        appName = (TextView) findViewById(R.id.HeaderTV);
        Typeface face3 = Typeface.createFromAsset(getAssets(),
                                                  "fonts/Barbarian.ttf");
		appName.setTypeface(face3);
        
        TextPaint paint = appName.getPaint();
        float width = paint.measureText("War Heroes Online");

        Shader textShader = new LinearGradient(0, 0, width, appName.getTextSize(),
                                               new int[]{
                                                   Color.parseColor("#FCC201"),
                                                   Color.parseColor("#B78628"),
                                                   Color.parseColor("#DBA514"),
                                               }, null, Shader.TileMode.CLAMP);
        appName.getPaint().setShader(textShader);
        
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        RotateAnimation rotate = new RotateAnimation(
            0, 360,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotate.setDuration(900);
        rotate.setRepeatCount(Animation.INFINITE);
        settingsButton.startAnimation(rotate);
		findView();

        File videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists()) {
            videoFile = copyVideoFile();
        }

        playVideo(videoFile);
        
        playAnim();
        
        isConnect();
    }

    private void isConnect() {
        // Check for Internet Connection
        if (isConnected()) {
            nullTV.setText("Internet Connected");
        } else {
            nullTV.setText("No Internet Connection");
        }
    }
    
    
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
    
    
    private void findView() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        
    }
    private void playVideo(File videoFile) {
        mVideoView.setVideoPath(videoFile.getPath());
        mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            });
    }
    private void playAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(appName, "alpha", 0,1);
        anim.setDuration(2000);
        anim.setRepeatCount(50);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    appName.setVisibility(View.VISIBLE);
                    appName.setBackgroundColor(android.R.color.white);
                }
            });
    }
    @NonNull
    private File copyVideoFile() {
        File videoFile;
        try {
            FileOutputStream fos = openFileOutput(VIDEO_NAME, MODE_PRIVATE);
            InputStream in = getResources().openRawResource(R.raw.kirito);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists())
            throw new RuntimeException("video file has problem, are you sure you have welcome_video.mp4 in res/raw folder?");
        return videoFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }
    
}
