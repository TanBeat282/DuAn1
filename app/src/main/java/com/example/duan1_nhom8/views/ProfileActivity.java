package com.example.duan1_nhom8.views;

import static java.util.Comparator.reverseOrder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.DangMuaAdapter;
import com.example.duan1_nhom8.adapters.DanhSachNguoiDungAdapter;
import com.example.duan1_nhom8.models.DangMua;
import com.example.duan1_nhom8.models.ThongTinCaNhan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView txtTenNguoiDung, txtIdNguoiDung, txtEmail, txtNgaySinh, txtSDT, txtDiaChi, txtDangMua, txtDaMua, txtDaHuy;
    private CircleImageView ivAvatar;
    private int count = 0;
    private String idNguoiDung, tentaikhoan, sdt, diachi, ngaysinh, username, password, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ivAvatar = findViewById(R.id.ivAvatar);
        txtTenNguoiDung = findViewById(R.id.txtTenNguoiDung);
        txtIdNguoiDung = findViewById(R.id.txtIdNguoiDung);
        txtEmail = findViewById(R.id.txtEmail);
        txtNgaySinh = findViewById(R.id.txtNgaySinh);
        txtSDT = findViewById(R.id.txtSDT);
        txtDiaChi = findViewById(R.id.txtDiaChi);
        txtDangMua = findViewById(R.id.txtDangMua);
        txtDaMua = findViewById(R.id.txtDaMua);
        txtDaHuy = findViewById(R.id.txtDaHuy);

        idNguoiDung = getIntent().getStringExtra("idNguoiDung");
        readProfileNguoiDung();
        getSizeDangMua();
        getSizeBiHuy();
    }

    public void readProfileNguoiDung() {
        db.collection("nguoidung")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getId().equals("1")) {
                                    Map<String, Object> map = document.getData();
                                    if (document.getId().equals(idNguoiDung)) {
                                        tentaikhoan = map.get("tentaikhoan").toString();
                                        sdt = map.get("sdt").toString();
                                        username = map.get("username").toString();
                                        diachi = map.get("diachi").toString();
                                        ngaysinh = map.get("ngaysinh").toString();
                                        url = map.get("avatar").toString();
                                        break;
                                    }

                                }
                            }
                            txtTenNguoiDung.setText(tentaikhoan);
                            txtIdNguoiDung.setText(idNguoiDung);
                            txtEmail.setText(username);
                            txtNgaySinh.setText(ngaysinh);
                            txtSDT.setText(sdt);
                            txtDiaChi.setText(diachi);
                            loadImage(url);
                        }

                    }
                });
    }

    private void getSizeDangMua() {
        db.collection("donhang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                String idDonHang = document.getId();
                                if (idDonHang != null) {
                                    String id = map.get("idnguoidung").toString();
                                    if (id.equals(idNguoiDung)) {
                                        count++;
                                    }
                                }
                            }
                            txtDangMua.setText(String.valueOf(count));

                        }
                    }
                });
    }
    private void getSizeDaMua() {
        db.collection("donhang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                String idDonHang = document.getId();
                                if (idDonHang != null) {
                                    String id = map.get("idnguoidung").toString();
                                    if (id.equals(idNguoiDung)) {
                                        count++;
                                    }
                                }
                            }
                            txtDangMua.setText(String.valueOf(count));

                        }
                    }
                });
    }
    private void getSizeBiHuy() {
        db.collection("donhangbihuy")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                String idDonHang = document.getId();
                                if (idDonHang != null) {
                                    String id = map.get("idnguoidung").toString();
                                    if (id.equals(idNguoiDung)) {
                                        count++;
                                    }
                                }
                            }
                            txtDaHuy.setText(String.valueOf(count));
                        }
                    }
                });
    }

    public void loadImage(String url) {
        Uri downloadUri = Uri.parse(url);
        Glide.with(ProfileActivity.this)
                .load(downloadUri)
                .into(ivAvatar);
    }
}