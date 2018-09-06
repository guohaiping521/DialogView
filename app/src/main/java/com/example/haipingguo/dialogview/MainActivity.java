package com.example.haipingguo.dialogview;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.haipingguo.dialogview.dialog.DialogAction;
import com.example.haipingguo.dialogview.dialog.MDialog;
import com.example.haipingguo.dialogview.dialog.StackingBehavior;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMdialog();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(MainActivity.this);
               // showMaterialDialog(MainActivity.this);
            }
        });
    }

    private void showMdialog() {
        MDialog.Builder builder = new MDialog.Builder(MainActivity.this);
        builder.title("弹窗dialog")
                .negativeText("取消")
                .positiveText("点击")
                .neutralText("1点击")
                .stackingBehavior(StackingBehavior.ADAPTIVE)
                .onNegative(new MDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(MainActivity.this, "取消了", Toast.LENGTH_SHORT).show();
                    }
                })
                .onPositive(new MDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(MainActivity.this, "点击了", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public void showMaterialDialog(Activity activity) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity);
        builder.title("title")
                .content("content")
                .negativeText("取消")
                .positiveText("点击")
                .neutralText("大家")
                .show();
    }

    public void showAlertDialog(Activity activity) {
       Dialog mDialog = new Dialog(activity);
        mDialog.setTitle("提示123");
        mDialog.show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置参数
        builder.setTitle("请做出选择")
                .setMessage("我美不美")
                .setPositiveButton("美", new DialogInterface.OnClickListener() {// 积极

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                    }
                }).setNegativeButton("不美", new DialogInterface.OnClickListener() {// 消极

            @Override
            public void onClick(DialogInterface dialog,
                                int which) {

            }
        }).setNeutralButton("不知道", new DialogInterface.OnClickListener() {// 中间级

            @Override
            public void onClick(DialogInterface dialog,
                                int which) {

            }
        });
        builder.create().show();
    }
}
