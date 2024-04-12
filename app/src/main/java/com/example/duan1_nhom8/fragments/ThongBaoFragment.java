package com.example.duan1_nhom8.fragments;

import static android.content.Context.MODE_PRIVATE;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.models.GioHang;
import com.example.duan1_nhom8.views.ChatActivity;
import com.example.duan1_nhom8.views.ChatOfAdminActivity;
import com.example.duan1_nhom8.views.GioHangActivity;
import com.example.duan1_nhom8.views.KhuyenMaiActivity;
import com.example.duan1_nhom8.views.SanPhamMoiActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.Map;

public class ThongBaoFragment extends Fragment {
    private ImageView ivChat, ivGioHang;
    private ThongBaoViewModel mViewModel;
    private NotificationBadge badge;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String id;

    public static ThongBaoFragment newInstance() {
        return new ThongBaoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thong_bao, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ThongBaoViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        id = preferences.getString("id", "");


        LinearLayout linearKhuyenMai = view.findViewById(R.id.linearKhuyenMai);
        LinearLayout linearSanPhamMoi = view.findViewById(R.id.linearSanPhamMoi);
        ivChat = view.findViewById(R.id.ivChat);
        ivGioHang = view.findViewById(R.id.ivGioHang);
        badge = view.findViewById(R.id.badge);

        ivGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GioHangActivity.class));
            }
        });
        ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.equals("1")) {
                    startActivity(new Intent(getContext(), ChatOfAdminActivity.class));
                } else {
                    startActivity(new Intent(getContext(), ChatActivity.class));
                }
            }
        });

        linearKhuyenMai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), KhuyenMaiActivity.class));
            }
        });

        linearSanPhamMoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SanPhamMoiActivity.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        readGioHang();
    }

    private void readGioHang() {
        db.collection("giohang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<GioHang> listGioHang = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                String idSach = map.get("idsach").toString();
                                Long soluong = (Long) map.get("soluong");
                                String tensach = map.get("tensach").toString();
                                Long giaban = (Long) map.get("giaban");
                                String url = map.get("url").toString();

                                if (document.getId().equals(id + idSach)) {
                                    GioHang gioHang = new GioHang(idSach, tensach, url, soluong, giaban);
                                    listGioHang.add(gioHang);
                                }
                            }
                            badge.setText(String.valueOf(listGioHang.size()));
                        }
                    }
                });
    }
}