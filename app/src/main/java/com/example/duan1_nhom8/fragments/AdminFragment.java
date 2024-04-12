package com.example.duan1_nhom8.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.views.AllSachActivity;
import com.example.duan1_nhom8.views.KhuyenMaiActivity;

public class AdminFragment extends Fragment {

    private AdminViewModel mViewModel;
    private LinearLayout linearAllSach, linearDanhSachNguoiDung, linearKhuyeMai, LinerDoanhThu;

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearAllSach = view.findViewById(R.id.linearAllSach);
        linearDanhSachNguoiDung = view.findViewById(R.id.linearDanhSachNguoiDung);
        linearKhuyeMai = view.findViewById(R.id.linearKhuyeMai);
        LinerDoanhThu = view.findViewById(R.id.LinerDoanhThu);
        linearAllSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllSachActivity.class);
                intent.putExtra("result", true);
                startActivity(intent);
            }
        });
        linearDanhSachNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllSachActivity.class);
                intent.putExtra("result", false);
                startActivity(intent);
            }
        });
        linearKhuyeMai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), KhuyenMaiActivity.class));
            }
        });
        LinerDoanhThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DoanhThuFragment.class));
            }
        });
    }
}