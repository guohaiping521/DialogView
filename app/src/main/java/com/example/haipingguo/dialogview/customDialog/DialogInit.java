package com.example.haipingguo.dialogview.customDialog;

import android.widget.Button;
import android.widget.TextView;

import com.example.haipingguo.dialogview.R;

public class DialogInit {

    public static void init(CDialog cDialog) {
        TextView titleTv = cDialog.view.findViewById(R.id.cd_title_tv);
        TextView messageTv = cDialog.view.findViewById(R.id.cd_message_tv);
        Button negativeBtn = cDialog.view.findViewById(R.id.cd_negative_btn);
        Button positiveBtn = cDialog.view.findViewById(R.id.cd_positive_btn);
        titleTv.setText(cDialog.mBuilder.getTitle());
        messageTv.setText(cDialog.mBuilder.getMessage());
        negativeBtn.setText(cDialog.mBuilder.getNegativeText());
        negativeBtn.setTag(CDialogAction.NEGATIVE);
        negativeBtn.setOnClickListener(cDialog);
        positiveBtn.setText(cDialog.mBuilder.getPostionText());
        positiveBtn.setTag(CDialogAction.POSITIVE);
        positiveBtn.setOnClickListener(cDialog);
        cDialog.setContentView(cDialog.view);

    }
}
