package com.zeng.bbfeeding;

import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

public class PrivacyDialog extends AlertDlg{
    @Override
    protected void onCreateAlertDlg() {
        super.onCreateAlertDlg();
        setContentView(R.layout.dialog_privacy);

        TextView tv = (TextView)findViewById(R.id.privacy_text_view);
        tv.setText(Html.fromHtml(getString(R.string.privacy_text)));
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        getBuilder().setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });
        getBuilder().setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Data.getInstance().setPrivacyChecked(true);
            }
        });

        setCancelable(false);
        return;
    }
}
