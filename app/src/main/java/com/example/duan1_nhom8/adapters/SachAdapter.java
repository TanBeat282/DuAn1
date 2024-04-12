package com.example.duan1_nhom8.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.R;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.duan1_nhom8.models.Sach;
import com.example.duan1_nhom8.views.ThanhToanActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SachAdapter extends BaseAdapter {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Sach> list;
    private Context context;
    private String idNguoiDung, idSach;
    private int count = 0;
    private Long soluong = Long.valueOf(0);
    private ArrayList<String> ArrayIdSach;

    public SachAdapter(Context context, ArrayList<Sach> list, String idNguoiDung) {
        this.context = context;
        this.list = list;
        this.idNguoiDung = idNguoiDung;
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
        LinearLayout linearThemGioHang, linearMua;
        if (view == null) {
            view = View.inflate(_viewgroup.getContext(), R.layout.custom_item_sach, null);
            ImageView ivSach = view.findViewById(R.id.ivSach);
            TextView txtTenSach = view.findViewById(R.id.txtTenSach);
            TextView txtTacGia = view.findViewById(R.id.txtTacGia);
            TextView txtTheLoai = view.findViewById(R.id.txtTheLoai);
            TextView txtNXB = view.findViewById(R.id.txtNXB);
            TextView txtGiaBan = view.findViewById(R.id.txtGiaBan);
            linearThemGioHang = view.findViewById(R.id.linearThemGioHang);
            linearMua = view.findViewById(R.id.linearMua);
            ViewHolder holder = new ViewHolder(txtTenSach, txtTacGia, txtTheLoai, txtNXB, txtGiaBan, linearThemGioHang, linearMua, ivSach);
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
        if (idNguoiDung.equals("1")) {
            holder.linearThemGioHang.setVisibility(View.INVISIBLE);
            holder.linearMua.setVisibility(View.INVISIBLE);
        }
        holder.linearThemGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sach.getId() != null) {
                    db.collection("giohang")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> map = document.getData();
                                            if (document.getId().equals(idNguoiDung + sach.getId())) {
                                                soluong = (Long) map.get("soluong");
                                                break;
                                            }
                                        }
                                        if (soluong != null) {
                                            Map<String, Object> item = new HashMap<>();
                                            item.put("idnguoidung", idNguoiDung);
                                            item.put("idsach", sach.getId());
                                            item.put("tensach", sach.getTensach());
                                            item.put("giaban", Long.parseLong(sach.getGiaban()));
                                            item.put("url", sach.getUrl());
                                            item.put("soluong", soluong + 1);
                                            db.collection("giohang").document(idNguoiDung + String.valueOf(sach.getId())).set(item);
                                            count = 1;
                                            soluong = Long.valueOf(0);
                                        } else {
                                            Map<String, Object> item = new HashMap<>();
                                            item.put("idnguoidung", idNguoiDung);
                                            item.put("idsach", sach.getId());
                                            item.put("tensach", sach.getTensach());
                                            item.put("giaban", Long.parseLong(sach.getGiaban()));
                                            item.put("url", sach.getUrl());
                                            item.put("soluong", 1);
                                            db.collection("giohang").document(idNguoiDung + String.valueOf(sach.getId())).set(item);
                                            count = 1;
                                            soluong = Long.valueOf(0);
                                        }
                                        Intent outIntent = new Intent("THEMGIOHANG");
                                        outIntent.putExtra("result", count);
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(outIntent);
                                    }

                                }
                            });
                }


            }
        });
        holder.linearMua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayIdSach = new ArrayList<>();
                ArrayIdSach.add(sach.getId());
                Intent intent = new Intent(context, ThanhToanActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Check", true);
                intent.putExtra("ArrayIdSach", ArrayIdSach);
                context.startActivity(intent);
            }
        });
        return view;
    }

    private static class ViewHolder {
        final TextView txtTenSach, txtTacGia, txtTheLoai, txtNXB, txtGiaBan;
        final LinearLayout linearThemGioHang, linearMua;
        final ImageView ivSach;

        public ViewHolder(TextView txtTenSach, TextView txtTacGia, TextView txtTheLoai, TextView txtNXB, TextView txtGiaBan, LinearLayout linearThemGioHang, LinearLayout linearMua, ImageView ivSach) {
            this.txtTenSach = txtTenSach;
            this.txtTacGia = txtTacGia;
            this.txtTheLoai = txtTheLoai;
            this.txtNXB = txtNXB;
            this.txtGiaBan = txtGiaBan;
            this.linearThemGioHang = linearThemGioHang;
            this.linearMua = linearMua;
            this.ivSach = ivSach;
        }
    }
}
