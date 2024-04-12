package com.example.duan1_nhom8.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.R;
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

public class DonHangAdapter extends BaseAdapter {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Sach> list;
    private Context context;
    private String idNguoiDung, idSach;
    private int count = 0;
    private Long soluong = Long.valueOf(0);

    public DonHangAdapter(Context context, ArrayList<Sach> list, String idNguoiDung) {
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
        if (view == null) {
            view = View.inflate(_viewgroup.getContext(), R.layout.custom_item_sach, null);
            ImageView ivSach = view.findViewById(R.id.ivSach);
            TextView txtTenSach = view.findViewById(R.id.txtTenSach);
            TextView txtTacGia = view.findViewById(R.id.txtTacGia);
            TextView txtTheLoai = view.findViewById(R.id.txtTheLoai);
            TextView txtNXB = view.findViewById(R.id.txtNXB);
            TextView txtGiaBan = view.findViewById(R.id.txtGiaBan);
            ViewHolder holder = new ViewHolder(txtTenSach, txtTacGia, txtTheLoai, txtNXB, txtGiaBan, ivSach);
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
        return view;
    }

    private static class ViewHolder {
        final TextView txtTenSach, txtTacGia, txtTheLoai, txtNXB, txtGiaBan;
        final ImageView ivSach;

        public ViewHolder(TextView txtTenSach, TextView txtTacGia, TextView txtTheLoai, TextView txtNXB, TextView txtGiaBan, ImageView ivSach) {
            this.txtTenSach = txtTenSach;
            this.txtTacGia = txtTacGia;
            this.txtTheLoai = txtTheLoai;
            this.txtNXB = txtNXB;
            this.txtGiaBan = txtGiaBan;
            this.ivSach = ivSach;
        }
    }
}
