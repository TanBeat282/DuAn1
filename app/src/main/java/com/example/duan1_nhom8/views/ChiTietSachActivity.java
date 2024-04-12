package com.example.duan1_nhom8.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.models.ListChatAdmin;
import com.example.duan1_nhom8.models.Sach;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChiTietSachActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idSach, tensach, tacgia, loaisach, nhaxuatban, giaban, url, id;
    private ImageView ivSach, ivBack;
    private LinearLayout ivChat, ivGioHang, ivMua;
    private TextView txtTenSach1, txtTenSach, txtTacGia1, txtTacGia, txtTheLoai, txtGiaBan, txtNXB, txtTomTat, txtNgonNgu;
    private ArrayList<Sach> list;
    private int count = 0;
    private Long soluong = Long.valueOf(0);
    private DialogLoading dialogLoading;
    private ArrayList<String> ArrayIdSach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_sach);

        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        id = preferences.getString("id", "");

        idSach = getIntent().getStringExtra("id");
        if (idSach != null) {
            Reload();
        }

        txtTenSach1 = findViewById(R.id.txtTenSach1);
        txtTacGia1 = findViewById(R.id.txtTacGia1);
        txtTenSach = findViewById(R.id.txtTenSach);
        txtTacGia = findViewById(R.id.txtTacGia);
        txtTheLoai = findViewById(R.id.txtTheLoai);
        txtGiaBan = findViewById(R.id.txtGiaBan);
        txtNXB = findViewById(R.id.txtNXB);
        txtTomTat = findViewById(R.id.txtTomTat);
        txtNgonNgu = findViewById(R.id.txtNgonNgu);

        ivSach = findViewById(R.id.ivSach);
        ivBack = findViewById(R.id.ivBack);
        ivChat = findViewById(R.id.ivChat);
        ivGioHang = findViewById(R.id.ivGioHang);
        ivMua = findViewById(R.id.ivMua);
        // neu admin thi ẩn linear mua chat them gio hang
        if (id.equals("1")) {
            findViewById(R.id.linearbottom).setVisibility(View.INVISIBLE);
        }
        ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.equals("1")) {
                    Intent intent = new Intent(ChiTietSachActivity.this, ChatOfAdminActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ChiTietSachActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
            }
        });
        ivGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ChiTietSachActivity.this, GioHangActivity.class);
//                startActivity(intent);
                themGioHang();
            }
        });
        ivMua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChiTietSachActivity.this, ThanhToanActivity.class);
                ArrayIdSach = new ArrayList<>();
                ArrayIdSach.add(idSach);
                intent.putExtra("Check", true);
                intent.putExtra("ArrayIdSach", ArrayIdSach);
                startActivity(intent);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void Reload() {
        db.collection("sach")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(idSach)) {
                                    Map<String, Object> map = document.getData();
                                    tensach = map.get("tensach").toString();
                                    tacgia = map.get("tacgia").toString();
                                    loaisach = map.get("loaisach").toString();
                                    nhaxuatban = map.get("nxb").toString();
                                    giaban = map.get("giaban").toString();
                                    url = map.get("url").toString();
                                    break;
                                }
                            }
                            txtTenSach1.setText(tensach);
                            txtTacGia1.setText(tacgia);
                            txtTenSach.setText(tensach);
                            txtTacGia.setText(tacgia);
                            txtTheLoai.setText(loaisach);
                            txtGiaBan.setText(giaban);
                            txtNXB.setText(nhaxuatban);
                            txtTomTat.setText("");
                            if (loaisach.equals("Tiếng anh")) {
                                txtNgonNgu.setText("Tiếng anh");
                            } else {
                                txtNgonNgu.setText("Tiếng việt");
                            }
                            loadImage(url);
                        }

                    }
                });
    }

    public void loadImage(String url) {
        Uri downloadUri = Uri.parse(url);
        Glide.with(this)
                .load(downloadUri)
                .into(ivSach);
    }

    private void themGioHang() {
        if (idSach != null) {
            db.collection("giohang")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> map = document.getData();
                                    if (document.getId().equals(id + idSach)) {
                                        soluong = (Long) map.get("soluong");
                                        break;
                                    }
                                }
                                if (soluong != null) {
                                    Map<String, Object> item = new HashMap<>();
                                    item.put("idnguoidung", id);
                                    item.put("idsach", idSach);
                                    item.put("tensach", tensach);
                                    item.put("giaban", Long.parseLong(giaban));
                                    item.put("url", url);
                                    item.put("soluong", soluong + 1);
                                    db.collection("giohang").document(id + String.valueOf(idSach)).set(item);
                                    soluong = Long.valueOf(0);
                                    count = 1;
                                } else {
                                    Map<String, Object> item = new HashMap<>();
                                    item.put("idnguoidung", id);
                                    item.put("idsach", idSach);
                                    item.put("tensach", tensach);
                                    item.put("giaban", Long.parseLong(giaban));
                                    item.put("url", url);
                                    item.put("soluong", 1);
                                    db.collection("giohang").document(id + String.valueOf(idSach)).set(item);
                                    soluong = Long.valueOf(0);
                                    count = 1;
                                }
                                if (count == 0) {
                                    Toast.makeText(ChiTietSachActivity.this, "Thêm vào giỏ hàng không thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialogLoading = new DialogLoading(ChiTietSachActivity.this, "Đang thêm giỏ hàng...");
                                    dialogLoading.showDialog();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialogLoading.closeDialog();
                                            Toast.makeText(ChiTietSachActivity.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    }, 1000);
                                }
                            }

                        }
                    });
        }
    }
}