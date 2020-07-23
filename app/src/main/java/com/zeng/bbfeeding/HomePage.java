package com.zeng.bbfeeding;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView mLastFeedingInfoTextView;

    @Override
    protected void onCreatePage(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.page_home);
        mStartTimingTextView= (TextView)findViewById(R.id.start_timing_textview);
        mResetTimingTextView = (TextView)findViewById(R.id.reset_timing_textview);
        mTimer = (Chronometer)findViewById(R.id.feed_timer);
        mFeedButton = (Button)findViewById(R.id.feed_button);
        mLastFeedingInfoTextView = (TextView)findViewById(R.id.last_feeding_info_textview);

        initTimingPanel();
        initAlarmPanel();

        if (!Data.getInstance().getPrivacyChecked()) {
            PrivacyDialog privacyDlg = new PrivacyDialog();
            privacyDlg.show(getFragmentManager(), "...");
        }

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
        initAlarmType();
        initAlarmRingtone();
        initAlarmVolume();
    }

    private void initAlarmSwitch(){
        final SwitchCompat alarmSwitch = (SwitchCompat) findViewById(R.id.alarm_switch);
        final View alarmSettingPanel = findViewById(R.id.alarm_setting_panel);

        final boolean enabled = Data.getInstance().getAlarmEnabled();
        Utils.enumView(alarmSettingPanel, new Utils.EnumViewListener() {
            @Override
            public void onEnum(View view) {
                view.setEnabled(enabled);
            }
        });
        alarmSwitch.setChecked(enabled);
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                final boolean enabled = b;
                Data.getInstance().setAlarmEnabled(enabled);
                Utils.enumView(alarmSettingPanel, new Utils.EnumViewListener() {
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
                        if (!(hour == 0 && minute == 0)) {
                            Data.getInstance().setAlarmInterval(hour*60+minute);
                            intervalTextView.setText(String.format(getContext().getString(R.string.alarm_interval_format),
                                    hour, minute));
                            restartAlarm();
                        }
                    }
                }, interval/60, interval%60);
                dlg.show(getFragmentManager(), "...");
            }
        });
        return;
    }

    private void initAlarmType(){
        final TextView typeTextView = (TextView)findViewById(R.id.alarm_type_textview);
        final View typePanel = findViewById(R.id.alarm_type_panel);

        String alarmType = Data.getInstance().getAlarmType();
        int textResId = R.string.alarm_type_both;
        if (alarmType.equals("ring")) textResId = R.string.alarm_type_ring;
        else if (alarmType.equals("vibra")) textResId = R.string.alarm_type_vibra;
        typeTextView.setText(textResId);

        typePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alarmType = Data.getInstance().getAlarmType();
                int checkedIdx = 0;
                if (alarmType.equals("vibra")) {checkedIdx = 1;}
                else if (alarmType.equals("both")) {checkedIdx = 2;}
                new AlertDialog.Builder(getContext())
                        .setSingleChoiceItems(new String[]{
                                getString(R.string.alarm_type_ring),
                                getString(R.string.alarm_type_vibra),
                                getString(R.string.alarm_type_both)
                        }, checkedIdx, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String alarmType = "both";
                                int textResId = R.string.alarm_type_both;
                                if (which == 0) {alarmType = "ring"; textResId = R.string.alarm_type_ring;}
                                else if (which == 1) {alarmType = "vibra"; textResId = R.string.alarm_type_vibra;}
                                Data.getInstance().setAlarmType(alarmType);
                                typeTextView.setText(textResId);

                                dialog.dismiss();
                            }
                        }).setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
    }

    private void initAlarmRingtone(){
        final TextView ringtoneTextView = (TextView)findViewById(R.id.alarm_ringtone_textview);
        final View ringtonePanel = findViewById(R.id.alarm_ringtone_panel);

        ringtoneTextView.setText(RingtoneManager.getRingtone(getContext(), Data.getInstance().getAlarmRingtone()).getTitle(getContext()));

        ringtonePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                        Data.getInstance().getAlarmRingtone());

                // android:launchMode="singleInstance" won't work.
                // https://developer.android.com/guide/topics/manifest/activity-element.html#lmode
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                Data.getInstance().setAlarmRingtone(uri);
                final TextView ringtoneTextView = (TextView)findViewById(R.id.alarm_ringtone_textview);
                ringtoneTextView.setText(RingtoneManager.getRingtone(getContext(), uri).getTitle(getContext()));
            }
        }
        return;
    }

    private void initAlarmVolume(){
        final SeekBar volumeBar = (SeekBar)findViewById(R.id.alarm_volumn_seekbar);

        volumeBar.setProgress(Data.getInstance().getAlarmVolume());
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Data.getInstance().setAlarmVolume(volumeBar.getProgress());
            }
        });
    }

    private void startAlarm(){
        if (Data.getInstance().getAlarmEnabled()) {
            long past = Calendar.getInstance().getTimeInMillis() - Data.getInstance().getFeedingBaseTime();
            long interval = Data.getInstance().getAlarmInterval() * 60 * 1000;
            if (past < interval) {
                Intent intent = new Intent(getContext(), AlarmActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (interval - past), pi);
                // alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, pi); Toast.makeText(getContext(), "start alarm", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cancelAlarm(){
        Intent intent = new Intent(getContext(), AlarmActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
    }

    private void restartAlarm(){
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

            int pattern = Integer.parseInt(argv[1]);
            if (pattern != Data.FEEDING_PATTERN_FORMULA) {
                mLastFeedingInfoTextView.setText(String.format(getString(R.string.last_breast_feeding_info_format),
                        time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE),
                        pattern == Data.FEEDING_PATTERN_LEFT ? getString(R.string.str_left) : getString(R.string.str_right), argv[2].equals("0") ? "-" : argv[2]));
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

        startAlarm();
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

                        cancelAlarm();
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
                mTimer.start();

                restartAlarm();

                updateLastFeedingInfo();
                Data.getInstance().save();
                return;
            }
        });
        feedDlg.show(getFragmentManager(), "...");
    }
}
