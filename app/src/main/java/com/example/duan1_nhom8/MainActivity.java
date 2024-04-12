package com.example.duan1_nhom8;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.duan1_nhom8.fragments.AdminFragment;
import com.example.duan1_nhom8.fragments.CaNhanFragment;
import com.example.duan1_nhom8.fragments.DonHangFragment;
import com.example.duan1_nhom8.fragments.ThongBaoFragment;
import com.example.duan1_nhom8.fragments.TrangChuFragment;
import com.example.duan1_nhom8.services.FcmNotificationsSender;
import com.example.duan1_nhom8.views.ThemThongTinActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.local.IidStore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idNguoiDung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        idNguoiDung = preferences.getString("id", "");
        if (idNguoiDung.equals("1")) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            String tokenAD = task.getResult();
                            Map<String, Object> item = new HashMap<>();
                            item.put("tokenAD", tokenAD);
                            db.collection("tokenMessesing").document("ADMIN").set(item);

                        }
                    });
        }
        openKhuyenMai(Gravity.CENTER);
        MeowBottomNavigation meowBottomNavigation = findViewById(R.id.meow);
        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_home_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_baseline_shopping_basket_24));
        if (idNguoiDung.equals("1")) {
            meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_admin));
        } else {
            meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_bell_alarm));
        }
        meowBottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_baseline_account_circle_24));

        meowBottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment fragment;
                if (item.getId() == 4) {
                    fragment = new CaNhanFragment();
                } else if (item.getId() == 3) {
                    if (idNguoiDung.equals("1")) {
                        fragment = new AdminFragment();
                    } else {
                        fragment = new ThongBaoFragment();
                    }
                } else if (item.getId() == 2) {
                    fragment = new DonHangFragment();
                } else {
                    fragment = new TrangChuFragment();
                }
                loadFragment(fragment);
            }
        });
        meowBottomNavigation.show(1, true);
        meowBottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

            }
        });
        meowBottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });
//        meowBottomNavigation.setCount(3, "10");

    }


    // bat dau lay id
    @Override
    protected void onStart() {
        super.onStart();
    }
    //


    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, null)
                .commit();
    }

    public void openKhuyenMai(int gravity) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_khuyenmai2);
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
        ImageView imageView = dialog.findViewById(R.id.ivKhuyenMai2);
        Uri downloadUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/pj01-561e9.appspot.com/o/Banner%20khuyen%20mai%2Fbannerkhuyemai.jpg?alt=media&token=c46d66e7-de36-4c53-8d3b-d4861e9aeb43");
        Glide.with(MainActivity.this)
                .load(downloadUri)
                .into(imageView);
        dialog.show();
    }
}
