package com.zeng.bbfeeding;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Calendar;

/**
 * Created by xianganzeng on 2016/11/2.
 */

public class FeedDialog extends AppCompatDialogFragment implements DialogInterface.OnClickListener{
    private DialogInterface.OnClickListener mPositiveListener;
    public void setPositiveListener(DialogInterface.OnClickListener positiveListener){
        mPositiveListener = positiveListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_feed, null, false);
        initView(view);
        builder.setView(view);

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, this);

        return builder.create();
    }

    private RadioGroup mPatternRadioGroup;
    private View mBreastPanel;
    private View mFormulaPanel;

    private RadioGroup mBreastRadioGroup;
    private EditText mBreastIntervalEditText;
    private EditText mFormulaIntakeEditText;
    private void initView(View view){
        mPatternRadioGroup = (RadioGroup)view.findViewById(R.id.pattern_radiogroup);
        mBreastPanel = view.findViewById(R.id.breast_panel);
        mFormulaPanel = view.findViewById(R.id.formula_panel);
        mBreastRadioGroup = (RadioGroup)view.findViewById(R.id.breast_radiogroup);
        mBreastIntervalEditText = (EditText)view.findViewById(R.id.breast_interval_edittext);
        mFormulaIntakeEditText = (EditText)view.findViewById(R.id.formula_intake_edittext);

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
