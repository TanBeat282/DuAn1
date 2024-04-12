package com.example.duan1_nhom8.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.models.DonHang;
import com.example.duan1_nhom8.models.Sach;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ThanhToanAdapter extends BaseAdapter {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;
    private ArrayList<DonHang> list;
    private String idNguoiDung, idSach;

    public ThanhToanAdapter(Context context, ArrayList<DonHang> list) {
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
        if (view == null) {
            view = View.inflate(_viewgroup.getContext(), R.layout.custom_item_sach_donhang, null);
            ImageView ivSach = view.findViewById(R.id.ivSach);
            TextView txtTenSach = view.findViewById(R.id.txtTenSach);
            TextView txtGiaBan = view.findViewById(R.id.txtGiaBan);
            TextView txtSoLuong = view.findViewById(R.id.txtSoLuong);
            ThanhToanAdapter.ViewHolder holder = new ThanhToanAdapter.ViewHolder(txtTenSach, txtGiaBan, txtSoLuong, ivSach);
            view.setTag(holder);
        }
        DonHang donHang = (DonHang) getItem(_i);
        ThanhToanAdapter.ViewHolder holder = (ThanhToanAdapter.ViewHolder) view.getTag();
        Glide.with(view)
                .load(donHang.getUrl())
                .into(holder.ivSach);
        holder.txtTenSach.setText(donHang.getTensach());
        holder.txtGiaBan.setText(donHang.getGiaban().toString());
        holder.txtSoLuong.setText(donHang.getSoluong().toString());
        return view;
    }

    private static class ViewHolder {
        final TextView txtTenSach, txtGiaBan, txtSoLuong;
        final ImageView ivSach;

        public ViewHolder(TextView txtTenSach, TextView txtGiaBan, TextView txtSoLuong, ImageView ivSach) {
            this.txtTenSach = txtTenSach;
            this.txtGiaBan = txtGiaBan;
            this.txtSoLuong = txtSoLuong;
            this.ivSach = ivSach;
        }
    }

}
