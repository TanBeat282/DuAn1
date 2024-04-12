package com.example.duan1_nhom8.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.KhuyenMaiAdapter;
import com.example.duan1_nhom8.adapters.SachAdapter;
import com.example.duan1_nhom8.models.KhuyenMai;
import com.example.duan1_nhom8.models.Sach;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class KhuyenMaiActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView lvKhuyenMai;
    private ArrayList<KhuyenMai> list = new ArrayList<>();
    private String urlKhuyeMai, idNguoiDung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khuyen_mai);
        ImageView imageViewBack = findViewById(R.id.ivBack);
        lvKhuyenMai = findViewById(R.id.lvKhuyenMai);

        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        idNguoiDung = preferences.getString("id", "");

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getKhuyenMai();
    }

    private void getKhuyenMai() {
        db.collection("khuyenmai")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                String id = document.getId();
                                urlKhuyeMai = map.get("urlkhuyenmai").toString();
                                KhuyenMai khuyenMai = new KhuyenMai(id, urlKhuyeMai);
                                list.add(khuyenMai);
                            }
                            KhuyenMaiAdapter adapter = new KhuyenMaiAdapter(KhuyenMaiActivity.this, list, idNguoiDung);
                            lvKhuyenMai.setAdapter(adapter);
                        }

                    }
                });
    }
}