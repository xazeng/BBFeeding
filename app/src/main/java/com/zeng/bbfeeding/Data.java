package com.zeng.bbfeeding;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;

/**
 * Created by xianganzeng on 2016/11/1.
 */

public class Data {
    private static Data sInstance;
    public static Data getInstance() {
        if (sInstance == null) {
            sInstance = new Data();
        }
        return sInstance;
    }

    private SharedPreferences.Editor mEditor;
    private static final String PREF_NAME = "data";
    public void init(Context context) {
        if (mEditor == null) {
            SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            mFeedingBaseTime = pref.getLong(FEEDING_BASE_TIME, 0L);
            mLastFeedingInfo = pref.getString(LAST_FEEDING_INFO, "");

            mAlarmEnabled = pref.getBoolean(ALARM_ENABLED, false);
            mAlarmInterval = pref.getInt(ALARM_INTERVAL, 2*60);
            mAlarmType = pref.getString(ALARM_TYPE, "both");
            mAlarmRingtone = Uri.parse(pref.getString(ALARM_RINGTONE, Settings.System.DEFAULT_ALARM_ALERT_URI.toString()));
            mAlarmVolume = pref.getInt(ALARM_VOLUME, 100);

            mEditor = pref.edit();
        }
        return;
    }

    public void save(){

        mEditor.apply();
        return;
    }

    private static final String FEEDING_BASE_TIME = "feed_base_time";
    private long mFeedingBaseTime;
    public long getFeedingBaseTime() {return mFeedingBaseTime;}
    public void setFeedingBaseTime(long t) {
        mFeedingBaseTime = t;
        mEditor.putLong(FEEDING_BASE_TIME, mFeedingBaseTime);
    }

    private static final String LAST_FEEDING_INFO = "last_feeding_info";
    private String mLastFeedingInfo;
    public String getLastFeedingInfo() {return mLastFeedingInfo;}
    public void setLastFeedingInfo(String info) {
        mLastFeedingInfo = info;
        mEditor.putString(LAST_FEEDING_INFO, mLastFeedingInfo);
    }

    private static final String ALARM_ENABLED = "alarm_enabled";
    private boolean mAlarmEnabled;
    public boolean getAlarmEnabled() {return mAlarmEnabled;}
    public void setAlarmEnabled(boolean b) {
        mAlarmEnabled = b;
        mEditor.putBoolean(ALARM_ENABLED, mAlarmEnabled);
    }

    private static final String ALARM_INTERVAL = "alarm_interval";
    private int mAlarmInterval;
    public int getAlarmInterval() {return mAlarmInterval;}
    public void setAlarmInterval(int interval){
        mAlarmInterval = interval;
        mEditor.putInt(ALARM_INTERVAL, mAlarmInterval);
    }

    private static final String ALARM_TYPE = "alarm_type";
    private String mAlarmType;
    public String getAlarmType() {return mAlarmType;}
    public void setAlarmType(String t) {
        mAlarmType = t;
        mEditor.putString(ALARM_TYPE, mAlarmType);
    }

    private static final String ALARM_RINGTONE = "alarm_ringtone";
    private Uri mAlarmRingtone;
    public Uri getAlarmRingtone() {return mAlarmRingtone;}
    public void setAlarmRingtone(Uri uri) {
        mAlarmRingtone = uri;
        mEditor.putString(ALARM_RINGTONE, uri.toString());
    }

    private static final String ALARM_VOLUME = "alarm_volume";
    private int mAlarmVolume;
    public int getAlarmVolume(){return mAlarmVolume;}
    public void setAlarmVolume(int vol) {
        mAlarmVolume = vol;
        mEditor.putInt(ALARM_VOLUME, mAlarmVolume);
    }
}
