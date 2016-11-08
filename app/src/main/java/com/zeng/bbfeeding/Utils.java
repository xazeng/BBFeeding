package com.zeng.bbfeeding;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xianganzeng on 2016/11/8.
 */

public class Utils {
    public interface EnumViewListener {
        public void onEnum(View view);
    }
    public static void enumView(View view, EnumViewListener listener) {
        if (view instanceof ViewGroup) {
            for (int i=0; i<((ViewGroup) view).getChildCount(); ++i) {
                enumView(((ViewGroup) view).getChildAt(i), listener);
            }
        }
        else{
            listener.onEnum(view);
        }
    }
}
