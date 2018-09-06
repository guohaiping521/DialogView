package com.example.haipingguo.dialogview.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.haipingguo.dialogview.R;

import static android.os.Looper.getMainLooper;


public class WaitingDialog extends AlertDialog {

    private TextView content;
    private LinearLayout parent;
    @ColorInt
    private int contentColor = -1;
    @DrawableRes
    private int backgroundColor = -1;

    public WaitingDialog(Context context) {
        super(context, R.style.waiting_dialog_theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_dialog);
        parent = (LinearLayout) findViewById(R.id.parent);
        content = ((TextView) findViewById(R.id.content));
    }

    public void setMessage(CharSequence message) {
        if (!TextUtils.isEmpty(message)) {
            content.setText(message);
        }
    }

    private void setContentColor() {
        if (contentColor != -1) {
            content.setTextColor(contentColor);
        }
    }

    private void setBackgroundColor() {
        if (backgroundColor != -1) {
            parent.setBackgroundResource(backgroundColor);
        }
    }

    @Override
    public void show() {
        if (Looper.myLooper() != getMainLooper()) {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    WaitingDialog.super.show();
                }
            });
        } else {
            super.show();
        }
    }

    public static WaitingDialog show(Activity context, CharSequence message) {
        return show(context, message, false);
    }

    public static WaitingDialog show(Activity context, CharSequence message, OnCancelListener cancelListener) {
        return show(context, message, false, cancelListener);
    }

    public static WaitingDialog show(Activity context, CharSequence message, boolean cancelable) {
        return show(context, message, cancelable, null);
    }

    public static WaitingDialog show(Activity context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
        return show(context, message, cancelable, false, cancelListener);
    }

    public static WaitingDialog show(Activity context, CharSequence message, boolean cancelable, boolean isCanceledOnTouchOutside, OnCancelListener cancelListener) {
        if (context.isFinishing())
            return null;
        WaitingDialog dialog = new WaitingDialog(context);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        dialog.setMessage(message);
        dialog.setBackgroundColor();
        dialog.setContentColor();
        return dialog;
    }

    public void setContentColor(@ColorInt int color) {
        this.contentColor = color;
    }

    public void setBackgroundColor(@DrawableRes int resid) {
        this.backgroundColor = resid;
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // 当Activity 意外关闭，然后调用 dialog 的 dismiss() 会抛出异常，导致 crash
        }
    }
}
