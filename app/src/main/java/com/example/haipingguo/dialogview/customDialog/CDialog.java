package com.example.haipingguo.dialogview.customDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.haipingguo.dialogview.R;
import com.example.haipingguo.dialogview.dialog.DialogAction;

import static com.example.haipingguo.dialogview.customDialog.CDialogAction.NEGATIVE;
import static com.example.haipingguo.dialogview.customDialog.CDialogAction.POSITIVE;

public class CDialog extends CDialogBase implements View.OnClickListener {

    public Builder mBuilder;
    public View view;

    public CDialog(@NonNull Builder builder) {
        super(builder.mActivity);
        view = LayoutInflater.from(builder.mActivity).inflate(R.layout.cdialog_basic_view, null);
        mBuilder = builder;
        DialogInit.init(this);
    }

    public void show() {
        if (mBuilder.mActivity.isFinishing()) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(mBuilder.mActivity.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    CDialog.super.show();
                }
            });
        } else {
            super.show();
        }
    }

    @Override
    public void onClick(View v) {
        CDialogAction tag = (CDialogAction) v.getTag();
        switch (tag){
            case POSITIVE:
                mBuilder.mPositiveListener.onClick(this,POSITIVE);
                break;
            case NEGATIVE:
                mBuilder.mNegativeListener.onClick(this,NEGATIVE);
                break;
        }
    }

    public interface SingleButtonCallback {
        void onClick(CDialog dialog, CDialogAction which);
    }

    public static class Builder {
        private Activity mActivity;
        private String mTitle;
        private String mMessage;
        private String mPostionText;
        private String mNegativeText;
        private String mNeutralText;
        private CDialog.SingleButtonCallback mNegativeListener;
        private CDialog.SingleButtonCallback mPositiveListener;

        public Activity getActivity() {
            return mActivity;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getMessage() {
            return mMessage;
        }

        public String getPostionText() {
            return mPostionText;
        }

        public String getNegativeText() {
            return mNegativeText;
        }

        public String getNeutralText() {
            return mNeutralText;
        }

        public SingleButtonCallback getNegativeListener() {
            return mNegativeListener;
        }

        public SingleButtonCallback getPositiveListener() {
            return mPositiveListener;
        }

        public Builder(Activity activity) {
            this.mActivity = activity;
        }

        public Builder setActivity(Activity activity) {
            this.mActivity = activity;
            return this;
        }

        public Builder title(String mTitle) {
            this.mTitle = mTitle;
            return this;
        }

        public Builder content(String mMessage) {
            this.mMessage = mMessage;
            return this;
        }

        public Builder positiveText(String mPostionText) {
            this.mPostionText = mPostionText;
            return this;
        }

        public Builder negativeText(String mNegativeText) {
            this.mNegativeText = mNegativeText;
            return this;
        }

        public Builder neutralText(String mNeutralText) {
            this.mNeutralText = mNeutralText;
            return this;
        }

        public Builder onNegative(CDialog.SingleButtonCallback negativeListener) {
            mNegativeListener = negativeListener;
            return this;
        }

        public Builder onPositive(CDialog.SingleButtonCallback positiveListener) {
            mPositiveListener = positiveListener;
            return this;
        }


        public CDialog create() {
            return new CDialog(this);
        }

        public CDialog show() {
            CDialog cDialog = create();
            cDialog.show();
            return cDialog;
        }

        /*private CDialog initView() {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.cdialog_basic_view, null);


            Dialog dialog = new Dialog(mActivity);
            dialog.setContentView(view);
            dialog.show();
        }
*/
    }

}
