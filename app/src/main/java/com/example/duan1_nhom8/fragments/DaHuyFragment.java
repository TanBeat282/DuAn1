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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.BiHuyAdapter;
import com.example.duan1_nhom8.adapters.DangMuaAdapter;
import com.example.duan1_nhom8.models.DangMua;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DaHuyFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String username, id, tentaikhoan, sdt, diachi, ngaysinh, password, url, ID;
    private ListView lvDangMua;
    private BiHuyAdapter adapter;
    private DaHuyViewModel mViewModel;

    public static DaHuyFragment newInstance() {
        return new DaHuyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_da_huy, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DaHuyViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        ID = preferences.getString("id", "");
        lvDangMua = view.findViewById(R.id.lvSach);
    }

    @Override
    public void onResume() {
        super.onResume();
        readDonHang();
    }

    private void readDonHang() {
        if (ID.equals("1")) {
            db.collection("donhangbihuy")
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
                                        String idNguoiDung = map.get("idnguoidung").toString();
                                        Boolean trangthaithanhtoan = (Boolean) map.get("trangthaithanhtoan");
                                        Long tongsanpham = (Long) map.get("tongsanpham");
                                        Long tongthanhtoan = (Long) map.get("tongthanhtoan");
                                        Timestamp timestamp = (Timestamp) map.get("thoigianmua");
                                        Date javaDate = timestamp.toDate();
                                        DangMua dangMua = new DangMua(idDonHang, idNguoiDung, trangthaithanhtoan, format_date(javaDate), tongsanpham, tongthanhtoan);
                                        dangMuas.add(dangMua);
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Collections.sort(dangMuas, reverseOrder());
                                }
                                adapter = new BiHuyAdapter(getContext(), dangMuas, ID);
                                lvDangMua.setAdapter(adapter);
                            }
                        }
                    });
        } else {
            db.collection("donhangbihuy")
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
                                        String idNguoiDung = map.get("idnguoidung").toString();
                                        Boolean trangthaithanhtoan = (Boolean) map.get("trangthaithanhtoan");
                                        Timestamp timestamp = (Timestamp) map.get("thoigianmua");
                                        Date javaDate = timestamp.toDate();
                                        Long tongsanpham = (Long) map.get("tongsanpham");
                                        Long tongthanhtoan = (Long) map.get("tongthanhtoan");
                                        if (idNguoiDung.equals(ID)) {
                                            DangMua dangMua = new DangMua(idDonHang, idNguoiDung, trangthaithanhtoan, format_date(javaDate), tongsanpham, tongthanhtoan);
                                            dangMuas.add(dangMua);
                                        }
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Collections.sort(dangMuas, reverseOrder());
                                }
                                adapter = new BiHuyAdapter(getContext(), dangMuas, ID);
                                lvDangMua.setAdapter(adapter);
                            }
                        }
                    });
        }
    }

    private String format_date(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}