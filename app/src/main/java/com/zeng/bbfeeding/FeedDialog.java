package com.zeng.bbfeeding;

import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Calendar;

/**
 * Created by xianganzeng on 2016/11/2.
 */

public class FeedDialog extends AlertDlg implements DialogInterface.OnClickListener{
    private RadioGroup mPatternRadioGroup;
    private View mBreastPanel;
    private View mFormulaPanel;

    private RadioGroup mBreastRadioGroup;
    private EditText mBreastIntervalEditText;
    private EditText mFormulaIntakeEditText;

    private DialogInterface.OnClickListener mPositiveListener;
    public void setPositiveListener(DialogInterface.OnClickListener positiveListener){
        mPositiveListener = positiveListener;
    }

    @Override
    protected void onCreateAlertDlg() {
        super.onCreateAlertDlg();
        setContentView(R.layout.dialog_feed);

        mPatternRadioGroup = (RadioGroup)findViewById(R.id.pattern_radiogroup);
        mBreastPanel = findViewById(R.id.breast_panel);
        mFormulaPanel = findViewById(R.id.formula_panel);
        mBreastRadioGroup = (RadioGroup)findViewById(R.id.breast_radiogroup);
        mBreastIntervalEditText = (EditText)findViewById(R.id.breast_interval_edittext);
        mFormulaIntakeEditText = (EditText)findViewById(R.id.formula_intake_edittext);

        mPatternRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.breast_radiobutton) {
                    mBreastPanel.setVisibility(View.VISIBLE);
                    mFormulaPanel.setVisibility(View.INVISIBLE);
                } else {
                    mBreastPanel.setVisibility(View.INVISIBLE);
                    mFormulaPanel.setVisibility(View.VISIBLE);
                }
            }
        });

        getBuilder().setNegativeButton(android.R.string.cancel, null);
        getBuilder().setPositiveButton(android.R.string.ok, this);

        setCanceledOnTouchOutside(false);
        setupDefault();
        return;
    }

    private void setupDefault(){
        try {
            String infoStr = Data.getInstance().getLastFeedingInfo();
            String[] argv = infoStr.split("\\|");

            if (argv[1].equals("breast")) {
                mPatternRadioGroup.check(R.id.breast_radiobutton);
                mBreastRadioGroup.check(argv[2].equals("left")?R.id.breast_right_radiobutton:R.id.breast_left_radiobutton);
            } else {
                mPatternRadioGroup.check(R.id.formula_radiobutton);
            }
        }catch (Exception ee){

        }
        return;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        StringBuilder builder = new StringBuilder("");
        builder.append(Calendar.getInstance().getTimeInMillis());

        if (mPatternRadioGroup.getCheckedRadioButtonId() == R.id.breast_radiobutton) {
            builder.append("|");
            builder.append("breast");

            builder.append("|");
            builder.append(mBreastRadioGroup.getCheckedRadioButtonId() == R.id.breast_left_radiobutton  ? "left" : "right");

            int interval = 0;
            try {
                interval = Integer.parseInt(mBreastIntervalEditText.getText().toString());
            } catch (Exception ee) {assert false : "";}
            builder.append("|");
            builder.append(interval);

        } else {
            builder.append("|");
            builder.append("formula");

            int intake = 0;
            try {
                intake = Integer.parseInt(mFormulaIntakeEditText.getText().toString());
            } catch (Exception ee) {assert false : "";}
            builder.append("|");
            builder.append(intake);
        }

        Data.getInstance().setLastFeedingInfo(builder.toString());
        mPositiveListener.onClick(dialogInterface, i);
        return;
    }
}
