package com.zeng.bbfeeding;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xianganzeng on 2016/10/27.
 */

public class HomePage extends Page implements View.OnClickListener{
    private TextView mStartTimingTextView;
    private TextView mResetTimingTextView;
    private Chronometer mTimer;
    private Button mFeedButton;

    @Override
    protected void onCreatePage(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.page_home);
        mStartTimingTextView= (TextView)findViewById(R.id.start_timing_textview);
        mResetTimingTextView = (TextView)findViewById(R.id.reset_timing_textview);
        mTimer = (Chronometer)findViewById(R.id.feed_timer);
        mFeedButton = (Button)findViewById(R.id.feed_button);


        initTimingPanel();
        return;
    }

    private void initTimingPanel(){
        long now = Calendar.getInstance().getTimeInMillis();
        long feedBaseTime = Data.getInstance().getFeedBaseTime();
        if (feedBaseTime != 0
                && (now - feedBaseTime) > 24*60*60*1000) {
            feedBaseTime = 0;
            Data.getInstance().setFeedBaseTime(feedBaseTime);
        }

        mStartTimingTextView.setVisibility(feedBaseTime == 0 ? View.VISIBLE : View.GONE);
        mStartTimingTextView.setOnClickListener(this);
        mResetTimingTextView.setVisibility(feedBaseTime == 0 ? View.GONE: View.VISIBLE);
        mResetTimingTextView.setOnClickListener(this);

        mTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long span = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
                long h = span / 3600;
                span = span - h * 3600;
                long m = span / 60;
                long s = span % 60;
                chronometer.setText(String.format("%1$02d:%2$02d:%3$02d", h, m, s));
            }
        });
        if (feedBaseTime == 0) {
            mTimer.setBase(SystemClock.elapsedRealtime());
        } else {
            mTimer.setBase(SystemClock.elapsedRealtime() - (now - feedBaseTime));
            mTimer.start();
        }

        mFeedButton.setOnClickListener(this);

        return;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_timing_textview:
                onClickStartTimingTextView();
                break;
            case R.id.reset_timing_textview:
                onClickResetTimingTextView();
                break;
            case R.id.feed_button:
                onClickFeedButton();
                break;
        }
        return;
    }

    private void onClickStartTimingTextView() {
        mStartTimingTextView.setVisibility(View.GONE);
        mResetTimingTextView.setVisibility(View.VISIBLE);

        long now = Calendar.getInstance().getTimeInMillis();
        Data.getInstance().setFeedBaseTime(now);
        mTimer.setBase(SystemClock.elapsedRealtime());
        mTimer.start();;
    }

    private void onClickResetTimingTextView(){
        mStartTimingTextView.setVisibility(View.VISIBLE);
        mResetTimingTextView.setVisibility(View.GONE);

        Data.getInstance().setFeedBaseTime(0);
        mTimer.stop();
        mTimer.setBase(SystemClock.elapsedRealtime());
    }

    private void onClickFeedButton(){

    }
}
