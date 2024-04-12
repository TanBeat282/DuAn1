package com.example.duan1_nhom8.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.GioHangAdapter;
import com.example.duan1_nhom8.adapters.SachAdapter;
import com.example.duan1_nhom8.models.GioHang;
import com.example.duan1_nhom8.models.Sach;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.ArrayList;
import java.util.Map;

public class GioHangActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idNguoiDung, idSach, id, url, tensach;
    private Long soluong, giaban;
    private ListView lvGioHang;
    private GioHangAdapter adapter;
    private ArrayList<GioHang> list;
    private ImageView ivBack;
    private TextView txtTongThanhToan;
    private LinearLayout txtXoa, linearDatHang;
    private ArrayList<String> ArrayIdSach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        lvGioHang = findViewById(R.id.lvGioHang);
        ivBack = findViewById(R.id.ivBack);
        txtTongThanhToan = findViewById(R.id.txtTongThanhToan);
        txtXoa = findViewById(R.id.txtXoa);
        linearDatHang = findViewById(R.id.linearDatHang);

        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        id = preferences.getString("id", "");
        getIdSach();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        linearDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ArrayIdSach == null){
                    openThongBao("Bạn chưa chọn sản phẩm nào", Gravity.CENTER);
                } else {
                    Intent intent = new Intent(GioHangActivity.this, ThanhToanActivity.class);
                    intent.putExtra("Check", false);
                    intent.putExtra("ArrayIdSach", ArrayIdSach);
                    startActivity(intent);
                }
            }
        });
        lvGioHang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GioHangActivity.this, ChiTietSachActivity.class);
                intent.putExtra("id", list.get(position).getIdSach());
                startActivity(intent);
            }
        });
    }

    public void onResume() {
        super.onResume();
        IntentFilter loginFilter = new IntentFilter("THAYDOISOLUONG");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, loginFilter);

        IntentFilter thanhtoanfilter = new IntentFilter("TONGTHANHTOAN");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(thanhtoanreceiver, thanhtoanfilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);

        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(thanhtoanreceiver);
    }

    private void getIdSach() {
        db.collection("giohang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                idNguoiDung = map.get("idnguoidung").toString();
                                idSach = map.get("idsach").toString();
                                soluong = (Long) map.get("soluong");
                                tensach = map.get("tensach").toString();
                                giaban = (Long) map.get("giaban");
                                url = map.get("url").toString();

                                if (document.getId().equals(id + idSach)) {
                                    GioHang gioHang = new GioHang(idSach, tensach, url, soluong, giaban);
                                    list.add(gioHang);
                                }
                            }
                            adapter = new GioHangAdapter(GioHangActivity.this, list, idNguoiDung);
                            lvGioHang.setAdapter(adapter);
                        }
                    }
                });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = intent.getIntExtra("result", 0);
            if (count != 0) {
                list.clear();
                if (list != null) {
                    getIdSach();
                }
            }
        }
    };
    private BroadcastReceiver thanhtoanreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Long tongThanhToan = intent.getLongExtra("result", 0);
            ArrayIdSach = (ArrayList<String>) intent.getSerializableExtra("ArrayIdSach");
            if (ArrayIdSach != null) {
                txtXoa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < ArrayIdSach.size(); i++) {
                            db.collection("giohang")
                                    .document(id + ArrayIdSach.get(i))
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            list.clear();
                                            getIdSach();
                                            txtTongThanhToan.setText("0");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                        Toast.makeText(GioHangActivity.this, "Xóa thành công ", Toast.LENGTH_LONG).show();

                    }
                });
            }
            if (tongThanhToan != 0) {
                txtTongThanhToan.setText(String.valueOf(tongThanhToan));
            } else {
                txtTongThanhToan.setText(String.valueOf(tongThanhToan));
            }

        }
    };
    public void openThongBao(String thongbao, int gravity) {
        final Dialog dialog = new Dialog(this);
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