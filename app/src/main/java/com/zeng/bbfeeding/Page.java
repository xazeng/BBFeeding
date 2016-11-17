package com.zeng.bbfeeding;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xianganzeng on 2016/10/28.
 */

public abstract class Page extends Fragment {
    private static String TAG = "PAGE";
    private View mRootView;
    protected View findViewById(int id) { return mRootView.findViewById(id); }

    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    protected void setContentView(int layoutResID){
        mRootView = mInflater.inflate(layoutResID, mContainer, false);
    }

    protected abstract void onCreatePage(@Nullable Bundle savedInstanceState);
    protected void onShowPage(){}
    protected void onHidePage(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Log.e(TAG, this.getClass().getName() + ".onAttach");

        if (mRootView == null) {
            mInflater = inflater;
            mContainer = container;

            onCreatePage(savedInstanceState);

            mInflater = null;
            mContainer = null;
        }
        return mRootView;
    }


    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, this.getClass().getName() + ".onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, this.getClass().getName() + ".onCreate");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, this.getClass().getName() + ".onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, this.getClass().getName() + ".onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, this.getClass().getName() + ".onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, this.getClass().getName() + ".onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, this.getClass().getName() + ".onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, this.getClass().getName() + ".onDestoryView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, this.getClass().getName() + ".onDestory");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, this.getClass().getName() + ".onDetach");
    }
    */
}
