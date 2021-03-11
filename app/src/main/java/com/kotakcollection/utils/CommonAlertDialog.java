package com.kotakcollection.utils;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.kotakcollection.R;


public class CommonAlertDialog {
    private Activity mActivity;

    public CommonAlertDialog(Activity a) {
        this.mActivity = a;
    }

    @SuppressWarnings("InflateParams")
    public void build(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.alertDialog);
       // builder.setTitle(title);
        builder.setMessage(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }
}
