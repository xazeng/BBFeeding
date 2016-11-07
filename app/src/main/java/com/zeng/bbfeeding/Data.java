package com.zeng.bbfeeding;

import android.content.Context;
import android.content.SharedPreferences;

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
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mFeedingBaseTime = pref.getLong(FEEDING_BASE_TIME, 0L);
        mLastFeedingInfo = pref.getString(LAST_FEEDING_INFO, "");

        mEditor = pref.edit();
        return;
    }

    public void save(){

        mEditor.commit();
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

}
