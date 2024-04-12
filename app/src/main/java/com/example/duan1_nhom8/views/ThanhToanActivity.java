package com.example.duan1_nhom8.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.duan1_nhom8.adapters.ThanhToanAdapter;
import com.example.duan1_nhom8.models.DonHang;
import com.example.duan1_nhom8.services.FcmNotificationsSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import vn.zalopay.sdk.Environment;
//import vn.zalopay.sdk.ZaloPayError;
//import vn.zalopay.sdk.ZaloPaySDK;
//import vn.zalopay.sdk.listeners.PayOrderListener;

public class ThanhToanActivity extends AppCompatActivity {
    private String idNguoiDung, tentaikhoan, diachi, sdt, tenSach, url, idDonHang, id;
    private Long soLuong, giaBan;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView txtTen, txtSDT, txtDiaChi, txtSoLuong, txtTongTien, txtTongThanhToan;
    private ImageView ivBack;
    private ArrayList<String> ArrayIdSach;
    private ArrayList<DonHang> list = new ArrayList<>();
    private ThanhToanAdapter thanhToanAdapter;
    private ListView lvDonHang;
    private Long tongThanhToan = Long.valueOf(0), phivienchuyen = Long.valueOf(15000), tongSanPham = Long.valueOf(0);
    private Date thoigianmua;
    private LinearLayout linearZaloPay;
    private int i = 0, checkDonHang = 0;
    private DialogLoading dialogLoading;
    private Boolean trangthaidonhang, trangthaithanhtoan, check = false;
    private Date ngaymua;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        //zalo
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);
        //

        txtTen = findViewById(R.id.txtTenNguoiDung);
        txtSDT = findViewById(R.id.txtSDT);
        txtDiaChi = findViewById(R.id.txtDiaChi);
        lvDonHang = findViewById(R.id.lvDonHang);
        txtSoLuong = findViewById(R.id.txtSoLuong);
        txtTongTien = findViewById(R.id.txtTongTien);
        txtTongThanhToan = findViewById(R.id.txtTongThanhToan);
        ivBack = findViewById(R.id.ivBack);
        linearZaloPay = findViewById(R.id.linearZaloPay);

        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        idNguoiDung = preferences.getString("id", "");
        ArrayIdSach = new ArrayList<>();
        ArrayIdSach = (ArrayList<String>) getIntent().getSerializableExtra("ArrayIdSach");
        check = getIntent().getBooleanExtra("Check", false);
        getInfor();
        if (ArrayIdSach != null) {
            getSach();
        }
        if (idNguoiDung != null) {
            idDonHang = String.valueOf(Calendar
                    .getInstance()
                    .getTimeInMillis());
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        linearZaloPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taoDonHang();
            }
        });

        lvDonHang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ThanhToanActivity.this, ChiTietSachActivity.class);
                intent.putExtra("id", list.get(position).getIdsach());
                startActivity(intent);
            }
        });
        gettoken();
    }

    private void gettoken() {
        db.collection("tokenMessesing")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                token = map.get("tokenAD").toString();
                            }
                        }
                    }
                });
    }

    private void sendNotifycation() {
        String tieude = "Book Store", noidung = "Bạn có đơn hàng mới";
        if (token != null) {
            FirebaseMessaging.getInstance().subscribeToTopic("all");
            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token, tieude, noidung, ThanhToanActivity.this, ThanhToanActivity.this);
            notificationsSender.SendNotifications();
        }
    }

    //tao don hang tong
    private void taoDonHang() {
        ngaymua = new Date();
        tongSanPham = Long.valueOf(ArrayIdSach.size());
        Map<String, Object> item = new HashMap<>();
        String idNguoidung = idNguoiDung;
        item.put("idnguoidung", idNguoidung);
        item.put("tentaikhoan", tentaikhoan);
        item.put("sdt", sdt);
        item.put("diachi", diachi);
        item.put("trangthaidonhang", false);
        item.put("tongsanpham", tongSanPham);
        item.put("trangthaithanhtoan", false);
        item.put("donhangbihuy", false);
        item.put("thoigianmua", ngaymua);
        tongThanhToan = tongThanhToan + phivienchuyen;
        item.put("tongthanhtoan", tongThanhToan);

        db.collection("donhang").document(idDonHang).set(item);
        chiTietDonHang();

    }

    //tao chi tiet don hang
    private void chiTietDonHang() {
        if (check==true){
            for (i = 0; i < ArrayIdSach.size(); i++) {
                String idSach = ArrayIdSach.get(i);
                db.collection("sach")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> map = document.getData();
                                        soLuong = (Long) map.get("soluong");
                                        tenSach = map.get("tensach").toString();
                                        giaBan = Long.valueOf(map.get("giaban").toString());
                                        url = map.get("url").toString();
                                        if (document.getId().equals(idNguoiDung + idSach)) {
                                            Map<String, Object> item = new HashMap<>();
                                            item.put("iddonhang", idDonHang);
                                            item.put("idsach", idSach);
                                            item.put("tensach", tenSach);
                                            item.put("soluong", soLuong);
                                            item.put("giaban", giaBan);
                                            item.put("url", url);
                                            db.collection("chitietdonhang")
                                                    .add(item)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            dialogLoading = new DialogLoading(ThanhToanActivity.this, "Đang đặt hàng...");
                                                            dialogLoading.showDialog();
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    dialogLoading.closeDialog();
                                                                    sendNotifycation();
//                                                                    thanhToan();
                                                                }
                                                            }, 2000);
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
                        });
            }
        }else {
            for (i = 0; i < ArrayIdSach.size(); i++) {
                String idSach = ArrayIdSach.get(i);
                db.collection("giohang")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> map = document.getData();
                                        soLuong = (Long) map.get("soluong");
                                        tenSach = map.get("tensach").toString();
                                        giaBan = (Long) map.get("giaban");
                                        url = map.get("url").toString();
                                        if (document.getId().equals(idNguoiDung + idSach)) {
                                            Map<String, Object> item = new HashMap<>();
                                            item.put("iddonhang", idDonHang);
                                            item.put("idsach", idSach);
                                            item.put("tensach", tenSach);
                                            item.put("soluong", soLuong);
                                            item.put("giaban", giaBan);
                                            item.put("url", url);
                                            db.collection("chitietdonhang")
                                                    .add(item)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            dialogLoading = new DialogLoading(ThanhToanActivity.this, "Đang đặt hàng...");
                                                            dialogLoading.showDialog();
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    dialogLoading.closeDialog();
                                                                    sendNotifycation();
//                                                                    thanhToan();
                                                                }
                                                            }, 2000);
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
                        });
            }
        }
    }

    //get thong tin nguoi dung
    public void getInfor() {
        db.collection("nguoidung")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(idNguoiDung)) {
                                    Map<String, Object> map = document.getData();
                                    tentaikhoan = map.get("tentaikhoan").toString();
                                    sdt = map.get("sdt").toString();
                                    diachi = map.get("diachi").toString();
                                    txtTen.setText(tentaikhoan);
                                    txtSDT.setText(sdt);
                                    txtDiaChi.setText(diachi);
                                    break;
                                }
                            }
                        }

                    }
                });
    }

    //get data sach theo arraylist
    private void getSach() {
        if (check == true) {
            String soluong = String.valueOf(ArrayIdSach.size());
            for (i = 0; i < ArrayIdSach.size(); i++) {
                String idSach = ArrayIdSach.get(i);
                db.collection("sach")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> map = document.getData();
                                        id = document.getId();
                                        soLuong = Long.valueOf(1);
                                        tenSach = map.get("tensach").toString();
                                        giaBan = Long.valueOf(10000);
                                        url = map.get("url").toString();
                                        if (document.getId().equals(idSach)) {
                                            tongThanhToan = tongThanhToan + giaBan * soLuong;
                                            DonHang donHang = new DonHang(id, tenSach, url, soLuong, giaBan);
                                            list.add(donHang);
                                        }

                                    }
                                    thanhToanAdapter = new ThanhToanAdapter(ThanhToanActivity.this, list);
                                    lvDonHang.setAdapter(thanhToanAdapter);
                                    txtSoLuong.setText(soluong);
                                    txtTongTien.setText(tongThanhToan.toString());
                                    txtTongThanhToan.setText(String.valueOf(tongThanhToan + phivienchuyen));
                                }
                            }
                        });
            }
        } else {
            String soluong = String.valueOf(ArrayIdSach.size());
            for (i = 0; i < ArrayIdSach.size(); i++) {
                String idSach = ArrayIdSach.get(i);
                db.collection("giohang")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> map = document.getData();
                                        id = map.get("tensach").toString();
                                        soLuong = (Long) map.get("soluong");
                                        tenSach = map.get("tensach").toString();
                                        giaBan = (Long) map.get("giaban");
                                        url = map.get("url").toString();
                                        if (document.getId().equals(idNguoiDung + idSach)) {
                                            tongThanhToan = tongThanhToan + giaBan * soLuong;
                                            DonHang donHang = new DonHang(id, tenSach, url, soLuong, giaBan);
                                            list.add(donHang);
                                        }

                                    }
                                    thanhToanAdapter = new ThanhToanAdapter(ThanhToanActivity.this, list);
                                    lvDonHang.setAdapter(thanhToanAdapter);
                                    txtSoLuong.setText(soluong);
                                    txtTongTien.setText(tongThanhToan.toString());
                                    txtTongThanhToan.setText(String.valueOf(tongThanhToan + phivienchuyen));
                                }
                            }
                        });
            }
        }
    }

    //sau khi thanh toan thanh cong se set trangthaithanhtoan bang true
    private void getDonHang() {
        db.collection("donhang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                String ID = document.getId();
                                idNguoiDung = map.get("idnguoidung").toString();
                                tentaikhoan = map.get("tentaikhoan").toString();
                                sdt = map.get("sdt").toString();
                                diachi = map.get("diachi").toString();
                                trangthaidonhang = (Boolean) map.get("trangthaidonhang");
                                trangthaithanhtoan = (Boolean) map.get("trangthaithanhtoan");
                                tongSanPham = (Long) map.get("tongsanpham");
                                tongThanhToan = (Long) map.get("tongthanhtoan");

                                if (ID.equals(idDonHang)) {
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
                                    item.put("tongthanhtoan", tongThanhToan);

                                    db.collection("donhang").document(idDonHang).set(item)
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            finish();
                                                            startActivity(new Intent(ThanhToanActivity.this, MainActivity.class));
                                                        }
                                                    }
                                            );
                                }
                            }
                        }
                    }
                });
    }

    //thanh toan
//    private void thanhToan() {
//        CreateOrder orderApi = new CreateOrder();
//        try {
//            JSONObject data = orderApi.createOrder(String.valueOf(tongThanhToan));
//            String code = data.getString("returncode");
//
//            if (code.equals("1")) {
//
//                String token = data.getString("zptranstoken");
//
//                ZaloPaySDK.getInstance().payOrder(ThanhToanActivity.this, token, "demozpdk://app", new PayOrderListener() {
//                    @Override
//                    public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
//                        getDonHang();
//                        Toast.makeText(ThanhToanActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onPaymentCanceled(String zpTransToken, String appTransID) {
//                        Toast.makeText(ThanhToanActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//
//                    @Override
//                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
//                        Toast.makeText(ThanhToanActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        ZaloPaySDK.getInstance().onResult(intent);
//    }

}