package com.zeng.bbfeeding;


import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by xianganzeng on 2016/10/27.
 */

public class HomePage extends Page implements View.OnClickListener{
    private TextView mStartTimingTextView;
    private TextView mResetTimingTextView;
    private Chronometer mTimer;
    private Button mFeedButton;
    private TextView mLastFeedingInfoTextView;

    private SwitchCompat mAlarmSwitch;
    private View mAlarmSettingPanel;

    @Override
    protected void onCreatePage(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.page_home);
        mStartTimingTextView= (TextView)findViewById(R.id.start_timing_textview);
        mResetTimingTextView = (TextView)findViewById(R.id.reset_timing_textview);
        mTimer = (Chronometer)findViewById(R.id.feed_timer);
        mFeedButton = (Button)findViewById(R.id.feed_button);
        mLastFeedingInfoTextView = (TextView)findViewById(R.id.last_feeding_info_textview);

        mAlarmSwitch = (SwitchCompat) findViewById(R.id.alarm_switch);
        mAlarmSettingPanel = findViewById(R.id.alarm_setting_panel);

        initTimingPanel();
        initAlarmPanel();
        return;
    }

    private void initTimingPanel(){
        long now = Calendar.getInstance().getTimeInMillis();
        long feedBaseTime = Data.getInstance().getFeedingBaseTime();
        if (feedBaseTime != 0
                && (now - feedBaseTime) > 24*60*60*1000) {
            feedBaseTime = 0;
            Data.getInstance().setFeedingBaseTime(feedBaseTime);
        }

        mStartTimingTextView.setVisibility(feedBaseTime == 0 ? View.VISIBLE : View.GONE);
        mStartTimingTextView.setOnClickListener(this);
        mResetTimingTextView.setVisibility(feedBaseTime == 0 ? View.GONE: View.VISIBLE);
        mResetTimingTextView.setOnClickListener(this);

        mTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long span = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
                long h = span / 3600; span = span % 3600;
                long m = span / 60; long s = span % 60;
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

        updateLastFeedingInfo();
        return;
    }

    private void initAlarmPanel(){
        initAlarmSwitch();
        initAlarmInterval();
    }

    private void initAlarmSwitch(){
        final boolean enabled = Data.getInstance().getAlarmEnabled();
        Utils.enumView(mAlarmSettingPanel, new Utils.EnumViewListener() {
            @Override
            public void onEnum(View view) {
                view.setEnabled(enabled);
            }
        });
        mAlarmSwitch.setChecked(enabled);
        mAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                final boolean enabled = b;
                Data.getInstance().setAlarmEnabled(enabled);
                Utils.enumView(mAlarmSettingPanel, new Utils.EnumViewListener() {
                    @Override
                    public void onEnum(View view) {
                        view.setEnabled(enabled);
                    }
                });
                if (enabled) { startAlarm();}
                else {cancelAlarm();}
            }
        });
        return;
    }

    private void initAlarmInterval(){

        final TextView intervalTextView = (TextView)findViewById(R.id.alarm_interval_textview);
        final View intervalPanel = findViewById(R.id.alarm_interval_panel);

        int interval = Data.getInstance().getAlarmInterval();
        intervalTextView.setText(String.format(getContext().getString(R.string.alarm_interval_format),
                interval/60, interval%60));

        intervalPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int interval = Data.getInstance().getAlarmInterval();
                IntervalPickerDialog dlg = new IntervalPickerDialog();
                dlg.init(new IntervalPickerDialog.OnSetListener() {
                    @Override
                    public void onSet(int hour, int minute) {
                        Data.getInstance().setAlarmInterval(hour*60+minute);
                        intervalTextView.setText(String.format(getContext().getString(R.string.alarm_interval_format),
                                hour, minute));
                        updateAlarm();
                    }
                }, interval/60, interval%60);
                dlg.show(getFragmentManager(), "...");
            }
        });
        return;
    }

    private void startAlarm(){

    }

    private void cancelAlarm(){

    }

    private void updateAlarm(){
        cancelAlarm();
        startAlarm();
    }

    private void updateLastFeedingInfo(){
        try{
            StringBuilder builder = new StringBuilder("");
            String infoStr = Data.getInstance().getLastFeedingInfo();
            String[] argv = infoStr.split("\\|");

            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(Long.parseLong(argv[0]));

            if (argv[1].equals("breast")) {
                mLastFeedingInfoTextView.setText(String.format(getString(R.string.last_breast_feeding_info_format),
                        time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE),
                        argv[2].equals("left") ? getString(R.string.left) : getString(R.string.right), argv[3].equals("0") ? "-" : argv[3]));
            } else {
                mLastFeedingInfoTextView.setText(String.format(getString(R.string.last_formula_feeding_info_format),
                        time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE),
                        argv[2].equals("0") ? "-" : argv[2]));
            }
        }catch (Exception ee) {
            mLastFeedingInfoTextView.setText(R.string.last_feeding_info_default);
        }
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
        Data.getInstance().setFeedingBaseTime(now);
        mTimer.setBase(SystemClock.elapsedRealtime());
        mTimer.start();;
    }

    private void onClickResetTimingTextView(){
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.reset_timing_check_dialog_content)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mStartTimingTextView.setVisibility(View.VISIBLE);
                        mResetTimingTextView.setVisibility(View.GONE);

                        Data.getInstance().setFeedingBaseTime(0);
                        mTimer.stop();
                        mTimer.setBase(SystemClock.elapsedRealtime());
                    }
                })
                .show();

        return;
    }

    private void onClickFeedButton(){
        FeedDialog feedDlg = new FeedDialog();
        feedDlg.setPositiveListener(new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mStartTimingTextView.setVisibility(View.GONE);
                mResetTimingTextView.setVisibility(View.VISIBLE);

                long now = Calendar.getInstance().getTimeInMillis();
                Data.getInstance().setFeedingBaseTime(now);
                mTimer.setBase(SystemClock.elapsedRealtime());
                mTimer.start();;

                updateLastFeedingInfo();
                return;
            }
        });
        feedDlg.show(getFragmentManager(), "...");
    }
}
