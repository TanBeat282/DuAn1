package com.example.duan1_nhom8.fragments;

import static android.content.Context.MODE_PRIVATE;

import static java.util.Comparator.*;

import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.DangMuaAdapter;
import com.example.duan1_nhom8.adapters.GioHangAdapter;
import com.example.duan1_nhom8.models.DangMua;
import com.example.duan1_nhom8.models.GioHang;
import com.example.duan1_nhom8.views.DialogLoading;
import com.example.duan1_nhom8.views.GioHangActivity;
import com.example.duan1_nhom8.views.ThanhToanActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DangMuaFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DangMuaViewModel mViewModel;
    private String username, id, tentaikhoan, sdt, diachi, ngaysinh, password, url, ID;
    private ListView lvDangMua;
    private DangMuaAdapter adapter;
    private Boolean trangthaithanhtoan;

    public static DangMuaFragment newInstance() {
        return new DangMuaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dang_mua, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DangMuaViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        ID = preferences.getString("id", "");
        lvDangMua = view.findViewById(R.id.lvDangMua);

    }

    @Override
    public void onResume() {
        super.onResume();
        readDonHang();
    }

    private void readDonHang() {
        if (ID.equals("1")) {
            db.collection("donhang")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<DangMua> dangMuas = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> map = document.getData();
                                    String idDonHang = document.getId();
                                    if (idDonHang != null) {
                                        tentaikhoan = map.get("tentaikhoan").toString();
                                        sdt = map.get("sdt").toString();
                                        diachi = map.get("diachi").toString();
                                        String idNguoiDung = map.get("idnguoidung").toString();
                                        Boolean trangthaithanhtoan = (Boolean) map.get("trangthaithanhtoan");
                                        Long tongsanpham = (Long) map.get("tongsanpham");
                                        Long tongthanhtoan = (Long) map.get("tongthanhtoan");
                                        Timestamp timestamp = (Timestamp) map.get("thoigianmua");
                                        Boolean trangthaidonhang = (Boolean) map.get("trangthaidonhang");
                                        Date javaDate = timestamp.toDate();
                                        if (trangthaidonhang != true) {
                                            DangMua dangMua = new DangMua(idDonHang, idNguoiDung, trangthaithanhtoan, format_date(javaDate), tongsanpham, tongthanhtoan);
                                            dangMuas.add(dangMua);
                                        } else {
                                            Map<String, Object> item = new HashMap<>();
                                            String idNguoidung = idNguoiDung;
                                            item.put("idnguoidung", idNguoidung);
                                            item.put("tentaikhoan", tentaikhoan);
                                            item.put("sdt", sdt);
                                            item.put("diachi", diachi);
                                            item.put("trangthaidonhang", trangthaidonhang);
                                            item.put("tongsanpham", tongsanpham);
                                            item.put("trangthaithanhtoan", trangthaithanhtoan);
                                            item.put("donhangbihuy", false);
                                            item.put("thoigianmua", timestamp);
                                            item.put("tongthanhtoan", tongthanhtoan);

                                            db.collection("donhangdamua").document(idDonHang).set(item);
                                            db.collection("donhang")
                                                    .document(idDonHang)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Collections.sort(dangMuas, reverseOrder());
                                }
                                adapter = new DangMuaAdapter(getContext(), dangMuas, ID);
                                lvDangMua.setAdapter(adapter);
                            }
                        }
                    });
        } else {
            db.collection("donhang")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<DangMua> dangMuas = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> map = document.getData();
                                    String idDonHang = document.getId();
                                    if (idDonHang != null) {
                                        tentaikhoan = map.get("tentaikhoan").toString();
                                        sdt = map.get("sdt").toString();
                                        diachi = map.get("diachi").toString();
                                        String idNguoiDung = map.get("idnguoidung").toString();
                                        trangthaithanhtoan = (Boolean) map.get("trangthaithanhtoan");
                                        Timestamp timestamp = (Timestamp) map.get("thoigianmua");
                                        Date javaDate = timestamp.toDate();
                                        Long tongsanpham = (Long) map.get("tongsanpham");
                                        Long tongthanhtoan = (Long) map.get("tongthanhtoan");
                                        Boolean trangthaidonhang = (Boolean) map.get("trangthaidonhang");
                                        if (idNguoiDung.equals(ID)) {
                                            if (trangthaidonhang != true) {
                                                DangMua dangMua = new DangMua(idDonHang, idNguoiDung, trangthaithanhtoan, format_date(javaDate), tongsanpham, tongthanhtoan);
                                                dangMuas.add(dangMua);
                                            } else {
                                                Map<String, Object> item = new HashMap<>();
                                                String idNguoidung = idNguoiDung;
                                                item.put("idnguoidung", idNguoidung);
                                                item.put("tentaikhoan", tentaikhoan);
                                                item.put("sdt", sdt);
                                                item.put("diachi", diachi);
                                                item.put("trangthaidonhang", trangthaidonhang);
                                                item.put("tongsanpham", tongsanpham);
                                                item.put("trangthaithanhtoan", trangthaithanhtoan);
                                                item.put("donhangbihuy", false);
                                                item.put("thoigianmua", timestamp);
                                                item.put("tongthanhtoan", tongthanhtoan);

                                                db.collection("donhangdamua").document(idDonHang).set(item);
                                                db.collection("donhang")
                                                        .document(idDonHang)
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Collections.sort(dangMuas, reverseOrder());
                                }
                                adapter = new DangMuaAdapter(getContext(), dangMuas, ID);
                                lvDangMua.setAdapter(adapter);
                            }
                        }
                    });
        }
    }

    private String format_date(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    public void openThongBao(String thongbao, int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_thongbao);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if (Gravity.CENTER == gravity) {
            dialog.setCancelable(true);
        }

        TextView tvThongBao = dialog.findViewById(R.id.tvThongBao);
        LinearLayout linearXacNhan = dialog.findViewById(R.id.linearXacNhan);
        tvThongBao.setText(thongbao);
        dialog.show();
        linearXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

}