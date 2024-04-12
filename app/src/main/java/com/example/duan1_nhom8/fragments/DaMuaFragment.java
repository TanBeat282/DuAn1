package com.example.duan1_nhom8.fragments;

import static android.content.Context.MODE_PRIVATE;

import static java.util.Comparator.reverseOrder;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.DaMuaAdapter;
import com.example.duan1_nhom8.adapters.DangMuaAdapter;
import com.example.duan1_nhom8.models.DangMua;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DaMuaFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idNguoiDung, ID;
    private DaMuaViewModel mViewModel;
    private String idDonHang, tentaikhoan, diachi, sdt, idSach, tenSach, url, id, thoigianmua, result, donhang;
    private Long soLuong = Long.valueOf(0), giaBan = Long.valueOf(0), tongthanhtoan = Long.valueOf(0), tongtien = Long.valueOf(0), tongSanPham = Long.valueOf(0);
    private Boolean trangthaithanhtoan, trangthaidonhang, trangthaibihuy;
    private Timestamp ngaymua;
    private ListView lvDaMua;
    private DaMuaAdapter adapter;

    public static DaMuaFragment newInstance() {
        return new DaMuaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_da_mua, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DaMuaViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        ID = preferences.getString("id", "");

        lvDaMua = view.findViewById(R.id.lvDaMua);
        readDonHang();
    }

    @Override
    public void onResume() {
        super.onResume();
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
                                    idDonHang = document.getId();
                                    if (idDonHang != null) {
                                        trangthaithanhtoan = (Boolean) map.get("trangthaithanhtoan");
                                        tongSanPham = (Long) map.get("tongsanpham");
                                        tongthanhtoan = (Long) map.get("tongthanhtoan");
                                        ngaymua = (Timestamp) map.get("thoigianmua");
                                        trangthaidonhang = (Boolean) map.get("trangthaidonhang");
                                        Date javaDate = ngaymua.toDate();
                                        DangMua dangMua = new DangMua(idDonHang, ID, trangthaithanhtoan, format_date(javaDate), tongSanPham, tongthanhtoan);
                                        dangMuas.add(dangMua);
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Collections.sort(dangMuas, reverseOrder());
                                }
                                adapter = new DaMuaAdapter(getContext(), dangMuas, idNguoiDung);
                                lvDaMua.setAdapter(adapter);
                            }
                        }
                    });
        } else {
            db.collection("donhangdamua")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<DangMua> dangMuas = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> map = document.getData();
                                    idDonHang = document.getId();
                                    if (idDonHang != null) {
                                        idNguoiDung = map.get("idnguoidung").toString();
                                        trangthaithanhtoan = (Boolean) map.get("trangthaithanhtoan");
                                        ngaymua = (Timestamp) map.get("thoigianmua");
                                        Date javaDate = ngaymua.toDate();
                                        tongSanPham = (Long) map.get("tongsanpham");
                                        tongthanhtoan = (Long) map.get("tongthanhtoan");
                                        trangthaidonhang = (Boolean) map.get("trangthaidonhang");
                                        if (idNguoiDung.equals(ID)) {
                                                DangMua dangMua = new DangMua(idDonHang, idNguoiDung, trangthaithanhtoan, format_date(javaDate), tongSanPham, tongthanhtoan);
                                                dangMuas.add(dangMua);
                                        }
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Collections.sort(dangMuas, reverseOrder());
                                }
                                adapter = new DaMuaAdapter(getContext(), dangMuas, idNguoiDung);
                                lvDaMua.setAdapter(adapter);
                            }
                        }
                    });
        }
    }

    private String format_date(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy - hh:mm a", Locale.getDefault()).format(date);
    }


    private void readDonHangDaMua() {
        Map<String, Object> item = new HashMap<>();
        item.put("idnguoidung", ID);
        item.put("tentaikhoan", tentaikhoan);
        item.put("sdt", sdt);
        item.put("diachi", diachi);
        item.put("trangthaidonhang", trangthaidonhang);
        item.put("tongsanpham", tongSanPham);
        item.put("trangthaithanhtoan", trangthaithanhtoan);
        item.put("donhangbihuy", false);
        item.put("thoigianmua", ngaymua);
        item.put("tongthanhtoan", tongthanhtoan);

        db.collection("donhangbihuy").document(idDonHang).set(item)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (trangthaidonhang == true) {
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
                );
    }

}