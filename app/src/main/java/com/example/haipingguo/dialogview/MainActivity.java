package com.example.haipingguo.dialogview;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.haipingguo.dialogview.customDialog.CDialog;
import com.example.haipingguo.dialogview.customDialog.CDialogAction;
import com.example.haipingguo.dialogview.dialog.DialogAction;
import com.example.haipingguo.dialogview.dialog.DialogUtils;
import com.example.haipingguo.dialogview.dialog.MDialog;
import com.example.haipingguo.dialogview.dialog.StackingBehavior;
import com.example.haipingguo.dialogview.responChainMode.MainPerson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.view.Window.FEATURE_NO_TITLE;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        /*findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showCdialog(MainActivity.this);
            }
        });*/
        final TextView textView = (TextView) this.findViewById(R.id.textview);

        final FrameLayout frameLayout = (FrameLayout) this.findViewById(R.id.frameLayout);
        frameLayout.setRight(frameLayout.getLeft() + 500);
        frameLayout.setBottom(frameLayout.getTop() + 200);
        addGhost(textView, frameLayout);

        this.findViewById(R.id.textview2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("Change To another");
            }
        });
    }

    private void addGhost(View view, ViewGroup viewGroup) {
        try {
            Class ghostViewClass = Class.forName("android.view.GhostView");
            Method addGhostMethod = ghostViewClass.getMethod("addGhost", View.class,
                    ViewGroup.class, Matrix.class);
            View ghostView = (View) addGhostMethod.invoke(null, view, viewGroup, null);
            ghostView.setBackgroundColor(Color.RED);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
