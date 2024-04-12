package com.example.duan1_nhom8.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.models.DangMua;
import com.example.duan1_nhom8.views.ChiTietDonHangActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

;

public class DaMuaAdapter extends BaseAdapter {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;
    private ArrayList<DangMua> list;
    private String idNguoiDung, idSach, tensach, url;
    private Long trangthai = Long.valueOf(0), tongTien = Long.valueOf(0);
    private ArrayList<String> ArrayIdSach;

    public DaMuaAdapter(Context context, ArrayList<DangMua> list, String idNguoiDung) {
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

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int _i, View _view, ViewGroup _viewgroup) {
        View view = _view;
        LinearLayout linearChiTiet;
        if (view == null) {
            view = View.inflate(_viewgroup.getContext(), R.layout.custom_item_dangmuahang, null);
            TextView txtMaDonHang = view.findViewById(R.id.txtMaDonHang);
            TextView txtTrangThai = view.findViewById(R.id.txtTrangThai);
            TextView txtNgayDat = view.findViewById(R.id.txtNgayDat);
            TextView txtTongSanPham = view.findViewById(R.id.txtTongSanPham);
            TextView txtTongTien = view.findViewById(R.id.txtTongTien);
            linearChiTiet = view.findViewById(R.id.linearChiTiet);
            ViewHolder holder = new ViewHolder(txtMaDonHang, txtTrangThai, txtNgayDat, txtTongSanPham, txtTongTien, linearChiTiet);
            view.setTag(holder);
        }

        DangMua dangMua = (DangMua) getItem(_i);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.txtMaDonHang.setText(dangMua.getIddonhang());
        if (dangMua.getTrangthai() == true) {
            holder.txtTrangThai.setTextColor(R.color.green);
            holder.txtTrangThai.setText("Đã thanh toán");
        } else {
            holder.txtTrangThai.setTextColor(R.color.red);
            holder.txtTrangThai.setText("Chưa thanh toán");
        }
        holder.txtNgayDat.setText(dangMua.getNgaymua());
        holder.txtTongSanPham.setText(dangMua.getTongsanpham().toString());
        holder.txtTongTien.setText(dangMua.getTongtien().toString());
        holder.linearChiTiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChiTietDonHangActivity.class);
                intent.putExtra("result", "damua");
                intent.putExtra("iddonhang", dangMua.getIddonhang());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return view;
    }

    private static class ViewHolder {
        final TextView txtMaDonHang, txtTrangThai, txtNgayDat, txtTongSanPham, txtTongTien;
        final LinearLayout linearChiTiet;

        public ViewHolder(TextView txtMaDonHang, TextView txtTrangThai, TextView txtNgayDat, TextView txtTongSanPham, TextView txtTongTien, LinearLayout linearChiTiet) {
            this.txtMaDonHang = txtMaDonHang;
            this.txtTrangThai = txtTrangThai;
            this.txtNgayDat = txtNgayDat;
            this.txtTongSanPham = txtTongSanPham;
            this.txtTongTien = txtTongTien;
            this.linearChiTiet = linearChiTiet;
        }
    }
}
