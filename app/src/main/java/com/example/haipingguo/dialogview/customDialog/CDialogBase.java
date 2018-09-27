package com.example.haipingguo.dialogview.customDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class CDialogBase extends Dialog implements DialogInterface.OnShowListener {
    public CDialogBase(@NonNull Context context) {
        super(context);
    }

    public CDialogBase(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CDialogBase(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
    }

    @Override
    public void onShow(DialogInterface dialog) {

    }
}
