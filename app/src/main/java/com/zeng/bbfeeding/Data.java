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
            mFeedingHistory = new StringBuilder(pref.getString(FEEDING_HISTORY, ""));
            mAdmobEnabled = pref.getBoolean(ADMOB_ENABLED, true);
            mPrivacyChecked = pref.getBoolean(PRIVACY_CHECKED, false);

            mEditor = pref.edit();
        }
        return;
    }

    public void save(){
        mEditor.putLong(FEEDING_BASE_TIME, mFeedingBaseTime);
        mEditor.putString(LAST_FEEDING_INFO, mLastFeedingInfo);
        mEditor.putBoolean(ALARM_ENABLED, mAlarmEnabled);
        mEditor.putInt(ALARM_INTERVAL, mAlarmInterval);
        mEditor.putString(ALARM_TYPE, mAlarmType);
        mEditor.putString(ALARM_RINGTONE, mAlarmRingtone.toString());
        mEditor.putInt(ALARM_VOLUME, mAlarmVolume);
        mEditor.putString(FEEDING_HISTORY, mFeedingHistory.toString());
        mEditor.putBoolean(ADMOB_ENABLED, mAdmobEnabled);
        mEditor.putBoolean(PRIVACY_CHECKED, mPrivacyChecked);

        mEditor.apply();
        return;
    }

    public static final int FEEDING_PATTERN_LEFT = 1;
    public static final int FEEDING_PATTERN_RIGHT = 2;
    public static final int FEEDING_PATTERN_FORMULA = 3;

    private static final String FEEDING_BASE_TIME = "feed_base_time";
    private long mFeedingBaseTime;
    public long getFeedingBaseTime() {return mFeedingBaseTime;}
    public void setFeedingBaseTime(long t) {
        mFeedingBaseTime = t;
    }

    private static final String LAST_FEEDING_INFO = "last_feeding_info";
    private String mLastFeedingInfo;
    public String getLastFeedingInfo() {return mLastFeedingInfo;}
    public void setLastFeedingInfo(String info) {
        mLastFeedingInfo = info;
    }

    private static final String ALARM_ENABLED = "alarm_enabled";
    private boolean mAlarmEnabled;
    public boolean getAlarmEnabled() {return mAlarmEnabled;}
    public void setAlarmEnabled(boolean b) {
        mAlarmEnabled = b;
    }

    private static final String ALARM_INTERVAL = "alarm_interval";
    private int mAlarmInterval;
    public int getAlarmInterval() {return mAlarmInterval;}
    public void setAlarmInterval(int interval){
        mAlarmInterval = interval;
    }

    private static final String ALARM_TYPE = "alarm_type";
    private String mAlarmType;
    public String getAlarmType() {return mAlarmType;}
    public void setAlarmType(String t) {
        mAlarmType = t;
    }

    private static final String ALARM_RINGTONE = "alarm_ringtone";
    private Uri mAlarmRingtone;
    public Uri getAlarmRingtone() {return mAlarmRingtone;}
    public void setAlarmRingtone(Uri uri) {
        mAlarmRingtone = uri;
    }

    private static final String ALARM_VOLUME = "alarm_volume";
    private int mAlarmVolume;
    public int getAlarmVolume(){return mAlarmVolume;}
    public void setAlarmVolume(int vol) {
        mAlarmVolume = vol;
    }

    private static final String FEEDING_HISTORY = "feeding_history";
    private StringBuilder mFeedingHistory;
    public String getFeedingHistory() {return mFeedingHistory.toString();}
    public void setFeedingHistory(String history) {
        mFeedingHistory = new StringBuilder(history);
    }
    public void addFeedingHistory(String history){
        mFeedingHistory.append(history);
        mFeedingHistory.append(",");
        mHistoryUpdated = true;
    }
    public boolean mHistoryUpdated = true;

    private static final String ADMOB_ENABLED = "admob_enabled";
    private boolean mAdmobEnabled;
    public boolean getAdmobEnabled() {return mAdmobEnabled;}
    public void setAdmobEnabled(boolean b) {mAdmobEnabled = b;}

    private static final String PRIVACY_CHECKED = "privacy_checked";
    private boolean mPrivacyChecked;
    public boolean getPrivacyChecked() {return mPrivacyChecked;}
    public void setPrivacyChecked(boolean b) {
        mPrivacyChecked = b;
    }

}
