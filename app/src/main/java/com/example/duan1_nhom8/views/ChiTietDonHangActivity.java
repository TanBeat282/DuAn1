package com.example.duan1_nhom8.views;

import static java.util.Comparator.reverseOrder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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

import com.example.duan1_nhom8.Helper.AppInfo;
import com.example.duan1_nhom8.Helper.CreateOrder;
import com.example.duan1_nhom8.MainActivity;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.DangMuaAdapter;
import com.example.duan1_nhom8.adapters.ThanhToanAdapter;
import com.example.duan1_nhom8.models.DangMua;
import com.example.duan1_nhom8.models.DonHang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ChiTietDonHangActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idNguoiDung, idDonHang, tentaikhoan, diachi, sdt, idSach, tenSach, url, id, thoigianmua, result, donhang;
    private Long soLuong = Long.valueOf(0), giaBan = Long.valueOf(0), tongthanhtoan = Long.valueOf(0), tongtien = Long.valueOf(0), tongSanPham = Long.valueOf(0);
    private Boolean trangthaithanhtoan, trangthaidonhang, trangthaibihuy;
    private TextView txtTrangThaiDonHang, txtNgayNhan, txtTenNguoiDung, txtSDT, txtDiaChi, txtTongTien, txtNgayDatHang, txtTrangThaiThanhToan, txtTongThanhToan, txtTrangThai, txtHuy;
    private LinearLayout linearHuyDonHang, linearThanhToan, linearChat, linearBottom;
    private ListView lvDonHang;
    private ThanhToanAdapter thanhToanAdapter;
    private ArrayList<DonHang> list;
    private ImageView ivBack, ivHuy;
    private DialogLoading dialogLoading;
    private Integer count = 0;
    private Timestamp ngaymua;
    private Date javaDate;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang);

        //zalo
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //


        txtTrangThaiDonHang = findViewById(R.id.txtTrangThaiDonHang);
        txtNgayNhan = findViewById(R.id.txtNgayNhan);
        txtTenNguoiDung = findViewById(R.id.txtTenNguoiDung);
        txtSDT = findViewById(R.id.txtSDT);
        txtDiaChi = findViewById(R.id.txtDiaChi);
        txtTongTien = findViewById(R.id.txtTongTien);
        txtNgayDatHang = findViewById(R.id.txtNgayDatHang);
        txtTrangThaiThanhToan = findViewById(R.id.txtTrangThaiThanhToan);
        txtTongThanhToan = findViewById(R.id.txtTongThanhToan);
        linearHuyDonHang = findViewById(R.id.linearHuyDonHang);
        linearThanhToan = findViewById(R.id.linearThanhToan);
        txtTrangThai = findViewById(R.id.txtTrangThai);
        linearChat = findViewById(R.id.linearChat);
        lvDonHang = findViewById(R.id.lvDonHang);
        ivBack = findViewById(R.id.ivBack);
        ivHuy = findViewById(R.id.ivHuy);
        txtHuy = findViewById(R.id.txtHuy);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        idNguoiDung = preferences.getString("id", "");
        result = getIntent().getStringExtra("result");
        idDonHang = getIntent().getStringExtra("iddonhang");
        if (idDonHang != null) {
            getChiTietDoHang();
        }
        lvDonHang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChiTietDonHangActivity.this, ChiTietSachActivity.class);
                intent.putExtra("id", list.get(position).getIdsach());
                startActivity(intent);
            }
        });
        linearHuyDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                huyDonHang();
            }
        });
        linearThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trangthaithanhtoan == true) {
                    openThongBao("Bạn đã thanh toán", Gravity.CENTER);
                } else {
//                    thanhToan();
                }
            }
        });
        linearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idNguoiDung.equals("1")) {
                    startActivity(new Intent(ChiTietDonHangActivity.this, ChatOfAdminActivity.class));
                } else {
                    startActivity(new Intent(ChiTietDonHangActivity.this, ChatActivity.class));
                }
            }
        });
        getDonHang();
    }

    public void getDonHang() {
        if (result.equals("dangmua")) {
            donhang = "donhang";
        } else if (result.equals("damua")) {
            donhang = "donhangdamua";
        } else {
            donhang = "donhangbihuy";
        }
        if (donhang != null) {
            db.collection(donhang)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getId().equals(idDonHang)) {
                                        Map<String, Object> map = document.getData();
                                        tentaikhoan = map.get("tentaikhoan").toString();
                                        sdt = map.get("sdt").toString();
                                        diachi = map.get("diachi").toString();
                                        ngaymua = (Timestamp) map.get("thoigianmua");
                                        javaDate = ngaymua.toDate();
                                        tongthanhtoan = (Long) map.get("tongthanhtoan");
                                        trangthaithanhtoan = (Boolean) map.get("trangthaithanhtoan");
                                        trangthaidonhang = (Boolean) map.get("trangthaidonhang");
                                        trangthaibihuy = (Boolean) map.get("donhangbihuy");
                                        break;
                                    }
                                }
                                txtTenNguoiDung.setText(tentaikhoan);
                                txtSDT.setText(sdt);
                                txtDiaChi.setText(diachi);
                                txtTongThanhToan.setText(tongthanhtoan.toString());
                                if (trangthaibihuy == true) {
                                    txtTrangThaiDonHang.setText("Đơn hàng đã bị hủy");
                                } else {
                                    if (trangthaithanhtoan == true) {
                                        txtTrangThaiThanhToan.setText("Đã thanh toán");
                                        txtTrangThaiDonHang.setText("Đang giao hàng");
                                    } else {
                                        txtTrangThaiThanhToan.setText("Chờ thanh toán");
                                        txtTrangThaiDonHang.setText("Chờ thanh toán");
                                    }

                                    if (trangthaidonhang == true) {
                                        txtTrangThaiDonHang.setText("Đã giao hàng");
                                    }
                                    if (donhang.equals("donhangdamua") || donhang.equals("donhangbihuy")) {
                                        linearHuyDonHang.setVisibility(View.INVISIBLE);
                                        txtTrangThai.setText("Mua lại");
                                    } else {
                                        if (trangthaithanhtoan == true) {
                                            txtTrangThai.setText("Đã thanh toán");
                                        } else {
                                            txtTrangThai.setText("Chờ thanh toán");
                                        }
                                    }
                                    if (trangthaithanhtoan == true) {
                                        linearHuyDonHang.setBackgroundColor(R.color.primary);
                                        ivHuy.setImageResource(R.drawable.ic_check);
                                        txtHuy.setText("Đã nhận hàng?");
                                    }
                                }
                                tongtien = tongthanhtoan - 15000;
                                txtTongTien.setText(tongtien.toString());
                                txtNgayDatHang.setText(format_date(javaDate));
                            }

                        }
                    });
        }
    }

    private String format_date(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void getChiTietDoHang() {
        list = new ArrayList<>();
        db.collection("chitietdonhang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                id = map.get("iddonhang").toString();
                                idSach = map.get("idsach").toString();
                                soLuong = (Long) map.get("soluong");
                                tenSach = map.get("tensach").toString();
                                giaBan = (Long) map.get("giaban");
                                url = map.get("url").toString();

                                if (id.equals(idDonHang)) {
                                    DonHang donHang = new DonHang(idSach, tenSach, url, soLuong, giaBan);
                                    list.add(donHang);
                                    tongSanPham++;
                                }
                            }
                            thanhToanAdapter = new ThanhToanAdapter(ChiTietDonHangActivity.this, list);
                            lvDonHang.setAdapter(thanhToanAdapter);
                        }

                    }
                });
    }

    private void huyDonHang() {
        if (trangthaidonhang == true && trangthaithanhtoan == true) {
            openThongBao("Đơn hàng đã giao, không thể hủy!", Gravity.CENTER);

        } else if (trangthaidonhang == false && trangthaithanhtoan == true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChiTietDonHangActivity.this);
            builder.setMessage("Bạn chắc chắn đã nhận được hàng?");
            builder.setTitle("Thông báo");

            builder.setPositiveButton("XÁC NHẬN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("idnguoidung", idNguoiDung);
                    item.put("tentaikhoan", tentaikhoan);
                    item.put("sdt", sdt);
                    item.put("diachi", diachi);
                    item.put("trangthaidonhang", true);
                    item.put("tongsanpham", tongSanPham);
                    item.put("trangthaithanhtoan", trangthaithanhtoan);
                    item.put("donhangbihuy", false);
                    item.put("thoigianmua", ngaymua);
                    item.put("tongthanhtoan", tongthanhtoan);

                    db.collection("donhang").document(idDonHang).set(item)
                            .addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            count++;
                                        }
                                    }
                            );

                    //
                    dialogLoading = new DialogLoading(ChiTietDonHangActivity.this, "Đang xác nhận...");
                    dialogLoading.showDialog();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialogLoading.closeDialog();
                            if (count != 0) {
                                openThongBao("Xác nhận thành công", Gravity.CENTER);
                                finish();
                            } else {
                                openThongBao("Xác nhận không thành công", Gravity.CENTER);
                            }
                        }
                    }, 1000);
                }
            });
            builder.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChiTietDonHangActivity.this);
            builder.setMessage("Bạn có muốn hủy đơn hàng?");
            builder.setTitle("Thông báo");

            builder.setPositiveButton("HỦY ĐƠN HÀNG", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("idnguoidung", idNguoiDung);
                    item.put("tentaikhoan", tentaikhoan);
                    item.put("sdt", sdt);
                    item.put("diachi", diachi);
                    item.put("trangthaidonhang", trangthaidonhang);
                    item.put("tongsanpham", tongSanPham);
                    item.put("trangthaithanhtoan", trangthaithanhtoan);
                    item.put("donhangbihuy", true);
                    item.put("thoigianmua", ngaymua);
                    item.put("tongthanhtoan", tongthanhtoan);

                    db.collection("donhangbihuy").document(idDonHang).set(item)
                            .addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            db.collection("donhang")
                                                    .document(idDonHang)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            count++;
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                        }
                                    }
                            );

                    //
                    dialogLoading = new DialogLoading(ChiTietDonHangActivity.this, "Đang hủy đơn hàng...");
                    dialogLoading.showDialog();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialogLoading.closeDialog();
                            if (count != 0) {
                                openThongBao("Hủy đơn hàng thành công", Gravity.CENTER);
                                finish();
                            } else {
                                openThongBao("Hủy đơn hàng không thành công", Gravity.CENTER);
                            }
                        }
                    }, 2000);
                }
            });
            builder.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

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

    //thanh toan

//    private void thanhToan() {
//        CreateOrder orderApi = new CreateOrder();
//        try {
//            JSONObject data = orderApi.createOrder(String.valueOf(tongthanhtoan));
//            String code = data.getString("returncode");
//
//            if (code.equals("1")) {
//                String token = data.getString("zptranstoken");
//                ZaloPaySDK.getInstance().payOrder(ChiTietDonHangActivity.this, token, "demozpdk2://app2", new PayOrderListener() {
//                    @Override
//                    public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
//                        updateDonHang();
//                        Toast.makeText(ChiTietDonHangActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onPaymentCanceled(String zpTransToken, String appTransID) {
//                        finish();
//                        Toast.makeText(ChiTietDonHangActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
//                        finish();
//                        Toast.makeText(ChiTietDonHangActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void updateDonHang() {
        Map<String, Object> item = new HashMap<>();
        String idNguoidung = idNguoiDung;
        item.put("idnguoidung", idNguoidung);
        item.put("tentaikhoan", tentaikhoan);
        item.put("sdt", sdt);
        item.put("diachi", diachi);
        item.put("trangthaidonhang", false);
        item.put("tongsanpham", tongSanPham);
        item.put("trangthaithanhtoan", true);
        item.put("donhangbihuy", false);
        item.put("thoigianmua", ngaymua);
        item.put("tongthanhtoan", tongthanhtoan);
        db.collection("donhang").document(idDonHang).set(item)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                finish();
                                startActivity(new Intent(ChiTietDonHangActivity.this, MainActivity.class));
                            }
                        }
                );

    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        ZaloPaySDK.getInstance().onResult(intent);
//    }
}