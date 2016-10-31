package com.zeng.bbfeeding;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xianganzeng on 2016/10/28.
 */

public abstract class Page extends Fragment {
    private View mRootView;
    protected View findViewById(int id) { return mRootView.findViewById(id); }

    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    protected void setContentView(int layoutResID){
        mRootView = mInflater.inflate(layoutResID, mContainer, false);
    }

    protected abstract void onCreatePage(@Nullable Bundle savedInstanceState);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mInflater = inflater;
            mContainer = container;

            onCreatePage(savedInstanceState);

            mInflater = null;
            mContainer = null;
        }
        return mRootView;
    }
}
