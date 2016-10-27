package com.zeng.bbfeeding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xianganzeng on 2016/10/27.
 */

public class LikePage extends Fragment {
    public static LikePage newInstance(){
        return new LikePage();
    }

    private View mRootView = null;
    private View findViewById(int id) { return mRootView.findViewById(id); }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            // mRootView = inflater.inflate(R.layout.page_main, container, false);
        }
        return mRootView;
    }
}
