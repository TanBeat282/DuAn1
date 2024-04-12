package com.example.duan1_nhom8.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.models.Sach;
import com.example.duan1_nhom8.models.ThongTinCaNhan;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DanhSachNguoiDungAdapter extends BaseAdapter {
    private ArrayList<ThongTinCaNhan> list;
    private Context context;
    public DanhSachNguoiDungAdapter(Context context, ArrayList<ThongTinCaNhan> list) {
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
            view = View.inflate(_viewgroup.getContext(), R.layout.custom_item_nguoidung, null);
            CircleImageView ivAvatar = view.findViewById(R.id.ivAvatar);
            TextView txtIdNguoiDung = view.findViewById(R.id.txtIdNguoiDung);
            TextView txtTenNguoiDung = view.findViewById(R.id.txtTenNguoiDung);
            TextView txtNgaySinh = view.findViewById(R.id.txtNgaySinh);
            TextView txtEmail = view.findViewById(R.id.txtEmail);
            TextView txtSDT = view.findViewById(R.id.txtSDT);
            TextView txtDiaChi = view.findViewById(R.id.txtDiaChi);
            ViewHolder holder = new ViewHolder(ivAvatar, txtIdNguoiDung, txtNgaySinh, txtTenNguoiDung, txtEmail, txtSDT, txtDiaChi);
            view.setTag(holder);
        }

        ThongTinCaNhan thongTinCaNhan = (ThongTinCaNhan) getItem(_i);
        ViewHolder holder = (ViewHolder) view.getTag();
        Glide.with(view)
                .load(thongTinCaNhan.getUrl())
                .into(holder.ivAvatar);
        holder.txtIdNguoiDung.setText(thongTinCaNhan.getIdnguoidung());
        holder.txtNgaySinh.setText(thongTinCaNhan.getTentaikhoan());
        holder.txtTenNguoiDung.setText(thongTinCaNhan.getNgaysinh());
        holder.txtEmail.setText(thongTinCaNhan.getEmail());
        holder.txtSDT.setText(thongTinCaNhan.getSdt());
        holder.txtDiaChi.setText(thongTinCaNhan.getDiachi());
        return view;
    }

    private static class ViewHolder {
        final CircleImageView ivAvatar;
        final TextView txtIdNguoiDung, txtTenNguoiDung, txtNgaySinh, txtEmail, txtSDT, txtDiaChi;

        public ViewHolder(CircleImageView ivAvatar, TextView txtIdNguoiDung, TextView txtTenNguoiDung, TextView txtNgaySinh, TextView txtEmail, TextView txtSDT, TextView txtDiaChi) {
            this.ivAvatar = ivAvatar;
            this.txtIdNguoiDung = txtIdNguoiDung;
            this.txtTenNguoiDung = txtTenNguoiDung;
            this.txtNgaySinh = txtNgaySinh;
            this.txtEmail = txtEmail;
            this.txtSDT = txtSDT;
            this.txtDiaChi = txtDiaChi;
        }
    }
}
