package com.zeng.bbfeeding;

import android.content.DialogInterface;
import android.widget.NumberPicker;

/**
 * Created by xianganzeng on 2016/11/8.
 */

public class IntervalPickerDialog extends AlertDlg{
    private static final int HOUR_FILTER = 7;
    private static final int MIN_FILTER = 60;

    private OnSetListener mOnSetListener;
    private int mInitHour;
    private int mInitMinute;
    public void init(OnSetListener listener, int hour, int minute) {
        mOnSetListener = listener;
        mInitHour = hour % HOUR_FILTER;
        mInitMinute = minute % MIN_FILTER;
    }

    NumberPicker mHourPicker;
    NumberPicker mMinPicker;

    @Override
    protected void onCreateAlertDlg() {
        super.onCreateAlertDlg();
        setContentView(R.layout.dialog_interval_picker);

        mHourPicker = (NumberPicker)findViewById(R.id.hour_picker);
        mHourPicker.setMinValue(0);
        mHourPicker.setMaxValue(HOUR_FILTER - 1);
        mHourPicker.setValue(mInitHour);

        mMinPicker = (NumberPicker)findViewById(R.id.minute_picker);
        mMinPicker.setMinValue(0);
        mMinPicker.setMaxValue(MIN_FILTER - 1);
        mMinPicker.setValue(mInitMinute);

        getBuilder().setNegativeButton(android.R.string.cancel, null);
        getBuilder().setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mOnSetListener != null) {
                    mOnSetListener.onSet(mHourPicker.getValue(), mMinPicker.getValue());
                }
            }
        });
        return;
    }

    public interface OnSetListener{
        void onSet(int hour, int minute);
    }
}
