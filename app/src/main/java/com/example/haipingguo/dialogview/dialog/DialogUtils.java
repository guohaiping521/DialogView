package com.example.haipingguo.dialogview.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.haipingguo.dialogview.customDialog.CDialog;
import com.example.haipingguo.dialogview.customDialog.CDialogAction;

public class DialogUtils {
    public static void showCdialog(final Activity activity) {
        CDialog.Builder builder = new CDialog.Builder(activity);
        builder.title("弹窗dialog")
                .content("contentcontentcontentcontentcontentcontentcontentcontent")
                .negativeText("取消")
                .positiveText("点击")
                .neutralText("1点击")
                .onNegative(new CDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull CDialog dialog, @NonNull CDialogAction which) {
                        Toast.makeText(activity, "取消了", Toast.LENGTH_SHORT).show();
                    }
                })
                .onPositive(new CDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull CDialog dialog, @NonNull CDialogAction which) {
                        Toast.makeText(activity, "点击了", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public void showMdialog(final Activity activity) {
        MDialog.Builder builder = new MDialog.Builder(activity);
        builder.title("弹窗dialog")
                .negativeText("取消")
                .positiveText("点击")
                .neutralText("1点击")
                .onNegative(new MDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(activity, "取消了", Toast.LENGTH_SHORT).show();
                    }
                })
                .onPositive(new MDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(activity, "点击了", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

}
