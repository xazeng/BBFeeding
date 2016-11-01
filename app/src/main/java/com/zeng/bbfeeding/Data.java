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
        mFeedBaseTime = pref.getLong(FEED_BASE_TIME, 0L);

        mEditor = pref.edit();
        return;
    }

    public void save(){

        mEditor.commit();
        return;
    }

    private static final String FEED_BASE_TIME = "feed_base_time";
    private long mFeedBaseTime;
    public long getFeedBaseTime() {return mFeedBaseTime;}
    public void setFeedBaseTime(long t) {
        mFeedBaseTime = t;
        mEditor.putLong(FEED_BASE_TIME, mFeedBaseTime);
    }


}
