package com.zeng.bbfeeding;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.kaku.sldv.SlideUnlockView;

import java.io.IOException;

public class AlarmActivity extends AppCompatActivity {

    MediaPlayer mMediaPlayer;
    Vibrator mVibrator;
    boolean mActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_alarm);

        Data.getInstance().init(this);

        TextView slideTextView = (TextView)findViewById(R.id.slide_textview);
        AnimationDrawable animationDrawable = (AnimationDrawable) slideTextView.getCompoundDrawables()[0];
        animationDrawable.start();

        SlideUnlockView slideToCloseView = (SlideUnlockView)findViewById(R.id.slide_to_close_view);
        slideToCloseView.setSlidingTipListener(new SlideUnlockView.SlidingTipListener() {
            @Override
            public void onSlidFinish() {
                Intent intent = new Intent(AlarmActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        wakeupDevice();
        activeAlarm();
        return;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Log.e("##########", "onWindowFocusChanged " + (hasFocus?"true":"false"));
        mActive = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Log.e("##########", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Log.e("##########", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Log.e("##########", "onPause");

        if (mActive) {
            cleanUp();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Log.e("##########", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Log.e("##########", "onDestory");
    }

    private void cleanUp() {
        if (mMediaPlayer != null) { mMediaPlayer.stop(); }
        if (mVibrator != null) { mVibrator.cancel(); }
    }

    private void activeAlarm(){
        String alarmType = Data.getInstance().getAlarmType();
        if (alarmType.equals("ring") || alarmType.equals("both")) {
            mMediaPlayer = new MediaPlayer();
            try {
                float volume = Data.getInstance().getAlarmVolume() / 100.0f;
                mMediaPlayer.setDataSource(this, Data.getInstance().getAlarmRingtone());
                mMediaPlayer.setVolume(volume, volume);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }

        if (alarmType.equals("vibra") || alarmType.equals("both")) {
            mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(new long[]{3000, 3000}, 0);
        }
    }

    private void wakeupDevice(){
        // wake up device
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "BBFeedingAlarm");
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
            wakeLock.release();
        }

        // disable keyguard
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("BBFeedingAlarm");
        keyguardLock.disableKeyguard();
    }
}
