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
import com.example.duan1_nhom8.models.KhuyenMai;
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

public class KhuyenMaiAdapter extends BaseAdapter {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<KhuyenMai> list;
    private Context context;
    private String idNguoiDung, idSach;
    private int count = 0;
    private Long soluong = Long.valueOf(0);
    private ArrayList<String> ArrayIdSach;

    public KhuyenMaiAdapter(Context context, ArrayList<KhuyenMai> list, String idNguoiDung) {
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
            view = View.inflate(_viewgroup.getContext(), R.layout.custom_khuyenmai, null);
            ImageView ivSach = view.findViewById(R.id.ivKhuyenMai);
            ViewHolder holder = new ViewHolder(ivSach);
            view.setTag(holder);
        }

        KhuyenMai khuyenMai = (KhuyenMai) getItem(_i);
        ViewHolder holder = (ViewHolder) view.getTag();
        Glide.with(view)
                .load(khuyenMai.getUrlKhuyenMai())
                .into(holder.ivSach);
        return view;
    }

    private static class ViewHolder {
        final ImageView ivSach;

        public ViewHolder(ImageView ivSach) {
            this.ivSach = ivSach;
        }
    }
}
