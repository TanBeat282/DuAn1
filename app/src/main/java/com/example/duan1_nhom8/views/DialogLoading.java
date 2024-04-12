package com.example.duan1_nhom8.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.example.duan1_nhom8.R;

public class DialogLoading {
    Context context;
    Dialog dialog;
    private TextView txtLoading;
    String thongbao;

    public DialogLoading(Context context, String thongbao) {
        this.context = context;
        this.thongbao = thongbao;
    }

    public void showDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_loading);
        txtLoading = dialog.findViewById(R.id.txtLoading);
        txtLoading.setText(thongbao);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.create();
        dialog.show();
    }

    public void closeDialog() {
        dialog.dismiss();
    }
}
