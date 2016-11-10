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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.kaku.sldv.SlideUnlockView;

import java.io.IOException;

public class AlarmActivity extends AppCompatActivity {

    MediaPlayer mMediaPlayer;
    Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        activeAlarm();
        wakeupDevice();
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

        if (mVibrator != null) {
            mVibrator.cancel();
        }

        finish();
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
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "FULL WAKE LOCK");
        fullWakeLock.acquire();

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();
    }
}
