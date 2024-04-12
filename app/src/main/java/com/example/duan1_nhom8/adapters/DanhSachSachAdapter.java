package com.example.duan1_nhom8.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.models.GioHang;
import com.example.duan1_nhom8.models.Sach;
import com.example.duan1_nhom8.views.AllSachActivity;
import com.example.duan1_nhom8.views.DialogLoading;
import com.example.duan1_nhom8.views.LoginActivity;
import com.example.duan1_nhom8.views.ThanhToanActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DanhSachSachAdapter extends BaseAdapter {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Sach> list;
    private Context context;
    private String idNguoiDung, idSach, nameImg, theloai, urlSach, tensach, tacgia, nxb, giaban;
    private int count = 0;
    private Long soluong = Long.valueOf(0);

    public DanhSachSachAdapter(Context context, ArrayList<Sach> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int _i, View _view, ViewGroup _viewgroup) {
        View view = _view;
        LinearLayout linearUpdateSach, linearDelSach;
        if (view == null) {
            view = View.inflate(_viewgroup.getContext(), R.layout.custom_item_sach_admin, null);
            ImageView ivSach = view.findViewById(R.id.ivSach);
            TextView txtTenSach = view.findViewById(R.id.txtTenSach);
            TextView txtTacGia = view.findViewById(R.id.txtTacGia);
            TextView txtTheLoai = view.findViewById(R.id.txtTheLoai);
            TextView txtNXB = view.findViewById(R.id.txtNXB);
            TextView txtGiaBan = view.findViewById(R.id.txtGiaBan);
            linearUpdateSach = view.findViewById(R.id.linearSuaSach);
            linearDelSach = view.findViewById(R.id.linearDelSach);
            ViewHolder holder = new ViewHolder(txtTenSach, txtTacGia, txtTheLoai, txtNXB, txtGiaBan, linearUpdateSach, linearDelSach, ivSach);
            view.setTag(holder);
        }

        Sach sach = (Sach) getItem(_i);
        ViewHolder holder = (ViewHolder) view.getTag();
        Glide.with(view)
                .load(sach.getUrl())
                .into(holder.ivSach);
        holder.txtTenSach.setText(sach.getTensach());
        holder.txtTacGia.setText(sach.getTacgia());
        holder.txtTheLoai.setText(sach.getLoaisach());
        holder.txtNXB.setText(sach.getNhaxuatban());
        holder.txtGiaBan.setText(sach.getGiaban());
        holder.linearUpdateSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nameImg = String.valueOf(Calendar.getInstance().getTimeInMillis());
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_item_addsach);
                Window window = dialog.getWindow();
                if (window == null) {
                    return;
                }
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.CENTER;
                window.setAttributes(windowAttributes);
                if (Gravity.CENTER == Gravity.CENTER) {
                    dialog.setCancelable(true);
                }
                dialog.show();
                ImageView ivSach = dialog.findViewById(R.id.ivSach);
                TextView txtMoiDung = dialog.findViewById(R.id.txtNoiDung);
                TextView txtTieuDe = dialog.findViewById(R.id.txtTieuDe);
                EditText edtTenSach = dialog.findViewById(R.id.edtTenSach);
                EditText edtTacGia = dialog.findViewById(R.id.edtTacGia);
                Spinner spnTheLoai = dialog.findViewById(R.id.spnTheLoai);
                EditText edtNXB = dialog.findViewById(R.id.edtNXB);
                EditText edtGiaBan = dialog.findViewById(R.id.edtGiaBan);

                LinearLayout linearThemSach = dialog.findViewById(R.id.linearThemSach);
                LinearLayout linearHuyThemSach = dialog.findViewById(R.id.linearHuyThemSach);
                String theLoai[] = {"Kinh tế", "Lịch sử", "Khoa học", "Tiếng anh", "Nấu ăn", "Thể thao", "Tiểu thuyết", "Động vật"};
                ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, theLoai);
                adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                spnTheLoai.setAdapter(adapter);
                spnTheLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        theloai = theLoai[arg2];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
                ivSach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                txtMoiDung.setText("SỬA SÁCH");
                txtTieuDe.setText("SỬA SÁCH");

                edtTacGia.setText(sach.getTacgia());
                edtTenSach.setText(sach.getTensach());
                edtNXB.setText(sach.getNhaxuatban());
                edtGiaBan.setText(sach.getGiaban());
                Glide.with(context)
                        .load(sach.getUrl())
                        .into(ivSach);

                linearThemSach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Map<String, Object> item = new HashMap<>();
                        item.put("tensach", edtTenSach.getText().toString());
                        item.put("tacgia", edtTacGia.getText().toString());
                        item.put("nxb", edtNXB.getText().toString());
                        item.put("loaisach", theloai);
                        item.put("giaban", edtGiaBan.getText().toString());
                        item.put("url", sach.getUrl());
                        db.collection("sach")
                                .document(sach.getId())
                                .set(item)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        Intent outIntent = new Intent("UPDATESACH");
                                        outIntent.putExtra("result", Integer.parseInt("1"));
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(outIntent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                });

                linearHuyThemSach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        holder.linearDelSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Bạn có muốn xóa sách này?");
                builder.setTitle("Thông báo");

                builder.setPositiveButton("XÓA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("sach")
                                .document(sach.getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent outIntent = new Intent("XOASACH");
                                        outIntent.putExtra("result", Integer.parseInt("1"));
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(outIntent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                });
                builder.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return view;
    }
    private static class ViewHolder {
        final TextView txtTenSach, txtTacGia, txtTheLoai, txtNXB, txtGiaBan;
        final LinearLayout linearUpdateSach, linearDelSach;
        final ImageView ivSach;

        public ViewHolder(TextView txtTenSach, TextView txtTacGia, TextView txtTheLoai, TextView txtNXB, TextView txtGiaBan, LinearLayout linearUpdateSach, LinearLayout linearDelSach, ImageView ivSach) {
            this.txtTenSach = txtTenSach;
            this.txtTacGia = txtTacGia;
            this.txtTheLoai = txtTheLoai;
            this.txtNXB = txtNXB;
            this.txtGiaBan = txtGiaBan;
            this.linearUpdateSach = linearUpdateSach;
            this.linearDelSach = linearDelSach;
            this.ivSach = ivSach;
        }
    }
}
