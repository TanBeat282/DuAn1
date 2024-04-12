package com.example.duan1_nhom8.fragments;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.duan1_nhom8.MainActivity;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.GioHangAdapter;
import com.example.duan1_nhom8.adapters.SachAdapter;
import com.example.duan1_nhom8.models.GioHang;
import com.example.duan1_nhom8.models.Sach;
import com.example.duan1_nhom8.services.FcmNotificationsSender;
import com.example.duan1_nhom8.views.ChatActivity;
import com.example.duan1_nhom8.views.ChatOfAdminActivity;
import com.example.duan1_nhom8.views.ChiTietSachActivity;
import com.example.duan1_nhom8.views.DialogLoading;
import com.example.duan1_nhom8.views.GioHangActivity;
import com.example.duan1_nhom8.views.ThanhToanActivity;
import com.example.duan1_nhom8.views.TheLoaiActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrangChuFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout linearKinhTe, linearLichSu, linearKhoaHoc, linearTiengAnh, linearNauAn, linearTheThao, linearTieuThuyet, linearDongVat;
    private LinearLayout linearBanChay, linearSachMoi, linearTatCaSach;
    private ListView lvSach;
    private ImageView imageView2, ivChat, ivGioHang;
    private TrangChuViewModel mViewModel;
    private String id;
    private ArrayList<Sach> list = new ArrayList<>();
    private DialogLoading dialogLoading;
    private NotificationBadge badge;
    private ImageSlider imageSlider;
    private String token;

    public static TrangChuFragment newInstance() {
        return new TrangChuFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trang_chu, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TrangChuViewModel.class);
        // TODO: Use the ViewModel
    }

    // chay  bán chạy load firebase
    @Override
    public void onResume() {
        super.onResume();

        readGioHang();
//        loadImage();

        IntentFilter loginFilter = new IntentFilter("THEMGIOHANG");
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(receiver, loginFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(receiver);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        id = preferences.getString("id", "");

        lvSach = view.findViewById(R.id.lvSach);
//        imageView2 = view.findViewById(R.id.ivBanner);
        ivChat = view.findViewById(R.id.ivChat);
        ivGioHang = view.findViewById(R.id.ivGioHang);
        badge = view.findViewById(R.id.badge);
        RelativeLayout recyclerChat = view.findViewById(R.id.recyclerChat);
        if (id.equals("1")) {
            recyclerChat.setVisibility(View.INVISIBLE);
        }
        linearKinhTe = view.findViewById(R.id.linearKinhTe);
        linearLichSu = view.findViewById(R.id.linearLichSu);
        linearKhoaHoc = view.findViewById(R.id.linearKhoaHoc);
        linearTiengAnh = view.findViewById(R.id.linearTiengAnh);
        linearNauAn = view.findViewById(R.id.linearNauAn);
        linearTieuThuyet = view.findViewById(R.id.linearTieuThuyet);
        linearTheThao = view.findViewById(R.id.linearTheThao);
        linearDongVat = view.findViewById(R.id.linearDongVat);
        //
        linearBanChay = view.findViewById(R.id.linearBanChay);
        linearSachMoi = view.findViewById(R.id.linearSachMoi);
        linearTatCaSach = view.findViewById(R.id.linearTatCaSach);
        //
        imageSlider = view.findViewById(R.id.image_slider);
        ArrayList<SlideModel> models = new ArrayList<SlideModel>();
        models.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/pj01-561e9.appspot.com/o/Banner%20khuyen%20mai%2FMua-s%C3%A1ch-Tiki-Online-1.jpg?alt=media&token=9c809fb2-6513-411f-a88b-1ac60c127f05", ScaleTypes.FIT));
        models.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/pj01-561e9.appspot.com/o/Banner%20khuyen%20mai%2Fbannerkhuyemai.jpg?alt=media&token=c46d66e7-de36-4c53-8d3b-d4861e9aeb43", ScaleTypes.FIT));
        models.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/pj01-561e9.appspot.com/o/Banner%20khuyen%20mai%2Fdai-tiec-sach-hay-giam-gia-den-50-tiki-banner.jpg?alt=media&token=a2821050-3be7-4322-836c-b8b755c8ad8a", ScaleTypes.FIT));
        models.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/pj01-561e9.appspot.com/o/Banner%20khuyen%20mai%2Fthang-3-sach-tre-giam-gia-den-49-tiki-banner.jpg?alt=media&token=848d39aa-f5c1-4581-b303-5d7fbc247cf0", ScaleTypes.FIT));
        models.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/pj01-561e9.appspot.com/o/Banner%20khuyen%20mai%2Fvinabook-khuyen-mai-giam-gia-hot-1024x462.jpg?alt=media&token=2d11a8f1-2d75-4cd2-bff9-1b6b56979d64", ScaleTypes.FIT));
        imageSlider.setImageList(models, ScaleTypes.FIT);

        readBanChay();

        lvSach.setClickable(true);
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
        ivGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GioHangActivity.class));
            }
        });
        lvSach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ChiTietSachActivity.class);
                intent.putExtra("id", list.get(position).getId());
                startActivity(intent);
            }
        });
        linearKinhTe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TheLoaiActivity.class);
                intent.putExtra("theloai", "Kinh tế");
                startActivity(intent);
            }
        });
        linearLichSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TheLoaiActivity.class);
                intent.putExtra("theloai", "Lịch sử");
                startActivity(intent);
            }
        });
        linearKhoaHoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TheLoaiActivity.class);
                intent.putExtra("theloai", "Khoa học");
                startActivity(intent);
            }
        });
        linearTiengAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TheLoaiActivity.class);
                intent.putExtra("theloai", "Tiếng anh");
                startActivity(intent);
            }
        });
        linearNauAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TheLoaiActivity.class);
                intent.putExtra("theloai", "Nấu ăn");
                startActivity(intent);
            }
        });
        linearTieuThuyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TheLoaiActivity.class);
                intent.putExtra("theloai", "Tiểu thuyết");
                startActivity(intent);
            }
        });
        linearTheThao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TheLoaiActivity.class);
                intent.putExtra("theloai", "Thể thao");
                startActivity(intent);
            }
        });
        linearDongVat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TheLoaiActivity.class);
                intent.putExtra("theloai", "Động vật");
                startActivity(intent);
            }
        });
        linearBanChay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                readBanChay();

            }
        });
        linearSachMoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                readSachMoi();
            }
        });
        linearTatCaSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                readTatCaSach();
            }
        });
    }

    private void readBanChay() {
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
                                String loaisach = map.get("loaisach").toString();
                                String nhaxuatban = map.get("nxb").toString();
                                String giaban = map.get("giaban").toString();
                                String url = map.get("url").toString();
                                Sach sach = new Sach(id, tensach, tacgia, loaisach, nhaxuatban, giaban, url);
                                list.add(sach);
                            }
                            SachAdapter adapter = new SachAdapter(getContext(), list, id);
                            lvSach.setAdapter(adapter);
                        }

                    }
                });
    }

    private void readSachMoi() {
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
                                String loaisach = map.get("loaisach").toString();
                                String nhaxuatban = map.get("nxb").toString();
                                String giaban = map.get("giaban").toString();
                                String url = map.get("url").toString();
                                Sach sach = new Sach(id, tensach, tacgia, loaisach, nhaxuatban, giaban, url);
                                list.add(sach);
                            }
                            SachAdapter adapter = new SachAdapter(getContext(), list, id);
                            lvSach.setAdapter(adapter);
                        }

                    }
                });
    }

    private void readTatCaSach() {
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
                                String loaisach = map.get("loaisach").toString();
                                String nhaxuatban = map.get("nxb").toString();
                                String giaban = map.get("giaban").toString();
                                String url = map.get("url").toString();
                                Sach sach = new Sach(id, tensach, tacgia, loaisach, nhaxuatban, giaban, url);
                                list.add(sach);
                            }
                            SachAdapter adapter = new SachAdapter(getContext(), list, id);
                            lvSach.setAdapter(adapter);
                        }

                    }
                });
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

//    public void loadImage() {
//        Uri downloadUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/pj01-561e9.appspot.com/o/Banner%20khuyen%20mai%2Fbannerkhuyemai.jpg?alt=media&token=c46d66e7-de36-4c53-8d3b-d4861e9aeb43");
//        Glide.with(getContext())
//                .load(downloadUri)
//                .into(imageView2);
//    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = intent.getIntExtra("result", 0);
            if (count == 0) {
                Toast.makeText(getContext(), "Thêm vào giỏ hàng không thành công", Toast.LENGTH_SHORT).show();
            } else {
                dialogLoading = new DialogLoading(getContext(), "Đang thêm giỏ hàng...");
                dialogLoading.showDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogLoading.closeDialog();
                        Toast.makeText(getContext(), "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                        readGioHang();
                    }
                }, 1000);
            }
        }
    };
}