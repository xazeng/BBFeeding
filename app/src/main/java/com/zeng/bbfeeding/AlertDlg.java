package com.zeng.bbfeeding;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by xianganzeng on 2016/11/8.
 */

public class AlertDlg extends AppCompatDialogFragment {
    private AlertDialog.Builder mBuilder;
    protected AlertDialog.Builder getBuilder() {
        if (mBuilder == null) {
            mBuilder = new AlertDialog.Builder(getContext());
        }
        return mBuilder;
    }

    private View mRootView;
    protected View findViewById(int id) { return mRootView.findViewById(id); }

    protected void setContentView(int layoutResID){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mRootView = inflater.inflate(layoutResID, null, false);
    }

    protected void onCreateAlertDlg(){}

    private boolean mCanceledOnTouchOutside;
    public void setCanceledOnTouchOutside(boolean cancel){
        mCanceledOnTouchOutside = cancel;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = getBuilder();
        onCreateAlertDlg();
        mBuilder = null;
        if (mRootView != null) { builder.setView(mRootView);}

        AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        return dlg;
    }
}
