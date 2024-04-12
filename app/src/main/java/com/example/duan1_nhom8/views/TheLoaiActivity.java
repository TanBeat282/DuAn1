package com.example.duan1_nhom8.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.SachAdapter;
import com.example.duan1_nhom8.models.Sach;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class TheLoaiActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView tvTheLoai;
    private ListView listviewTheLoai;
    private String id, idSach, loaisach;
    private ImageView ivBack;
    private ArrayList<Sach> list = new ArrayList<>();
    private DialogLoading dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_loai);
        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        id = preferences.getString("id", "");
        readSach();

        tvTheLoai = findViewById(R.id.txtTheLoai);
        listviewTheLoai = findViewById(R.id.listviewTheLoai);
        tvTheLoai.setText(getIntent().getStringExtra("theloai"));
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listviewTheLoai.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TheLoaiActivity.this, ChiTietSachActivity.class);
                intent.putExtra("id", list.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void readSach() {
        db.collection("sach")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                String id = document.getId();
                                String tensach = map.get("tensach").toString();
                                String tacgia = map.get("tacgia").toString();
                                loaisach = map.get("loaisach").toString();
                                String nhaxuatban = map.get("nxb").toString();
                                String giaban = map.get("giaban").toString();
                                String url = map.get("url").toString();

                                if (loaisach.equals(getIntent().getStringExtra("theloai"))) {
                                    Sach sach = new Sach(id, tensach, tacgia, loaisach, nhaxuatban, giaban, url);
                                    list.add(sach);
                                } else {

                                }
                            }
                            SachAdapter adapter = new SachAdapter(TheLoaiActivity.this, list, id);
                            listviewTheLoai.setAdapter(adapter);
                        }

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter loginFilter = new IntentFilter("THEMGIOHANG");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, loginFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = intent.getIntExtra("result", 0);
            if (count == 0) {
                Toast.makeText(TheLoaiActivity.this, "Thêm vào giỏ hàng không thành công", Toast.LENGTH_SHORT).show();
            } else {
                dialogLoading = new DialogLoading(TheLoaiActivity.this, "Đang thêm giỏ hàng...");
                dialogLoading.showDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogLoading.closeDialog();
                        Toast.makeText(TheLoaiActivity.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }
        }
    };
}