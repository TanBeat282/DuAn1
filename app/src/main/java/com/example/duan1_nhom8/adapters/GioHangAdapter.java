package com.example.duan1_nhom8.adapters;

import android.content.Context;
import android.content.Intent;;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;

import com.example.duan1_nhom8.R;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.duan1_nhom8.models.GioHang;

import com.example.duan1_nhom8.views.ChatActivity;
import com.example.duan1_nhom8.views.GioHangActivity;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GioHangAdapter extends BaseAdapter {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;
    private ArrayList<GioHang> list;
    private String idNguoiDung, idSach, id, tensach, url;
    private Long giaban, soluong, tongThanhToan = Long.valueOf(0);
    private ArrayList<String> ArrayIdSach;

    public GioHangAdapter(Context context, ArrayList<GioHang> list, String id) {
        this.context = context;
        this.list = list;
        this.id = id;
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
        LinearLayout linearTang, linearGiam;
        if (view == null) {
            view = View.inflate(_viewgroup.getContext(), R.layout.custom_item_sach_giohang, null);
            ImageView ivSach = view.findViewById(R.id.ivSach);
            TextView txtTenSach = view.findViewById(R.id.txtTenSach);
            TextView txtGiaBan = view.findViewById(R.id.txtGiaBan);
            TextView txtSoLuong = view.findViewById(R.id.txtSoLuong);
            linearTang = view.findViewById(R.id.linearTang);
            linearGiam = view.findViewById(R.id.linearGiam);
            CheckBox cbGioHang = view.findViewById(R.id.cbGioHang);
            ViewHolder holder = new ViewHolder(ivSach, txtTenSach, txtGiaBan, txtSoLuong, linearTang, linearGiam, cbGioHang);
            view.setTag(holder);
        }

        GioHang gioHang = (GioHang) getItem(_i);
        ViewHolder holder = (ViewHolder) view.getTag();
        Glide.with(view)
                .load(gioHang.getUrl())
                .into(holder.ivSach);
        holder.txtTenSach.setText(gioHang.getTenSach());
        holder.txtGiaBan.setText(gioHang.getGiaBan().toString());
        holder.txtSoLuong.setText(gioHang.getSoLuong().toString());

// neu so luong bang 0 xoa item
        db.collection("giohang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                idSach = map.get("idsach").toString();
                                idNguoiDung = map.get("idnguoidung").toString();
                                tensach = map.get("tensach").toString();
                                url = map.get("url").toString();
                                giaban = (Long) map.get("giaban");
                                soluong = (Long) map.get("soluong");
                                if (soluong == 0) {
                                    db.collection("giohang")
                                            .document(id + idSach)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Intent outIntent = new Intent("THAYDOISOLUONG");
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
                            }
                        }
                    }

                });

        ArrayIdSach = new ArrayList<>();
        holder.cbGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cbGioHang.isChecked()) {
                    tongThanhToan = tongThanhToan + gioHang.getGiaBan() * gioHang.getSoLuong();
                    Intent outIntent = new Intent("TONGTHANHTOAN");
                    outIntent.putExtra("result", tongThanhToan);
                    ArrayIdSach.add(gioHang.getIdSach());
                    outIntent.putStringArrayListExtra("ArrayIdSach", ArrayIdSach);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(outIntent);
                } else {
                    tongThanhToan = tongThanhToan - gioHang.getGiaBan() * gioHang.getSoLuong();
                    Intent outIntent = new Intent("TONGTHANHTOAN");
                    outIntent.putExtra("result", tongThanhToan);
                    ArrayIdSach.remove(gioHang.getIdSach());
                    outIntent.putStringArrayListExtra("ArrayIdSach", ArrayIdSach);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(outIntent);
                }
            }
        });

        holder.linearTang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gioHang.getIdSach() != null) {
                    db.collection("giohang")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> map = document.getData();
                                            idSach = map.get("idsach").toString();
                                            if (document.getId().equals(id + gioHang.getIdSach())) {
                                                idNguoiDung = map.get("idnguoidung").toString();
                                                tensach = map.get("tensach").toString();
                                                url = map.get("url").toString();
                                                giaban = (Long) map.get("giaban");
                                                soluong = (Long) map.get("soluong");
                                                break;
                                            }
                                        }
                                        Map<String, Object> item = new HashMap<>();
                                        item.put("idnguoidung", idNguoiDung);
                                        item.put("idsach", idSach);
                                        item.put("tensach", tensach);
                                        item.put("giaban", giaban);
                                        item.put("url", url);
                                        item.put("soluong", soluong + 1);
                                        db.collection("giohang").document(id + String.valueOf(idSach)).set(item);

                                        Intent outIntent = new Intent("THAYDOISOLUONG");
                                        outIntent.putExtra("result", Integer.parseInt("1"));
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(outIntent);
                                    }

                                }
                            });
                }
            }
        });
        holder.linearGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("giohang")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> map = document.getData();
                                        idSach = map.get("idsach").toString();
                                        if (document.getId().equals(id + gioHang.getIdSach())) {
                                            idNguoiDung = map.get("idnguoidung").toString();
                                            tensach = map.get("tensach").toString();
                                            url = map.get("url").toString();
                                            giaban = (Long) map.get("giaban");
                                            soluong = (Long) map.get("soluong");
                                            break;
                                        }
                                    }
                                    if (soluong > 0) {
                                        Map<String, Object> item = new HashMap<>();
                                        item.put("idnguoidung", idNguoiDung);
                                        item.put("idsach", idSach);
                                        item.put("tensach", tensach);
                                        item.put("giaban", giaban);
                                        item.put("url", url);
                                        item.put("soluong", soluong - 1);
                                        db.collection("giohang").document(id + String.valueOf(idSach)).set(item);

                                        Intent outIntent = new Intent("THAYDOISOLUONG");
                                        outIntent.putExtra("result", Integer.parseInt("1"));
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(outIntent);
                                    }
                                }

                            }
                        });
            }
        });
        return view;
    }

    private static class ViewHolder {
        final ImageView ivSach;
        final TextView txtTenSach, txtGiaBan, txtSoLuong;
        final LinearLayout linearTang, linearGiam;
        final CheckBox cbGioHang;

        public ViewHolder(ImageView ivSach, TextView txtTenSach, TextView txtGiaBan, TextView txtSoLuong, LinearLayout linearTang, LinearLayout linearGiam, CheckBox cbGioHang) {
            this.ivSach = ivSach;
            this.txtTenSach = txtTenSach;
            this.txtGiaBan = txtGiaBan;
            this.txtSoLuong = txtSoLuong;
            this.linearTang = linearTang;
            this.linearGiam = linearGiam;
            this.cbGioHang = cbGioHang;
        }
    }
}
