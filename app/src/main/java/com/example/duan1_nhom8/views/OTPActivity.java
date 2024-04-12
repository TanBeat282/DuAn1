package com.example.duan1_nhom8.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.duan1_nhom8.MainActivity;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.services.OtpIntentService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class OTPActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout btnVeryfy;
    private EditText edtEmail, edtotp1, edtotp2, edtotp3, edtotp4;
    private TextView btnResendCode, btnSigIn, tvBtn;
    private int opt1, opt2, opt3, opt4, check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);
        edtEmail = findViewById(R.id.edtEmail);
        edtotp1 = findViewById(R.id.edtotp1);
        edtotp2 = findViewById(R.id.edtotp2);
        edtotp3 = findViewById(R.id.edtotp3);
        edtotp4 = findViewById(R.id.edtotp4);
        btnResendCode = findViewById(R.id.btnResendCode);
        btnSigIn = findViewById(R.id.tvSignIn);
        tvBtn = findViewById(R.id.tvBtn);
        btnVeryfy = findViewById(R.id.btnVeryfy);
        check();
        btnVeryfy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTPActivity.this, OtpIntentService.class);
                if (isMyServiceRunning(OtpIntentService.class)) {
                    Integer so1 = Integer.valueOf(edtotp1.getText().toString());
                    Integer so2 = Integer.valueOf(edtotp2.getText().toString());
                    Integer so3 = Integer.valueOf(edtotp3.getText().toString());
                    Integer so4 = Integer.valueOf(edtotp4.getText().toString());
                    if (so1 == opt1 && so2 == opt2 && so3 == opt3 && so4 == opt4) {
                        Intent intent1 = new Intent(OTPActivity.this, RegisterActivity.class);
                        intent1.putExtra("email", edtEmail.getText().toString());
                        intent1.putExtra("check", 1);
                        startActivity(intent1);
                        stopService(intent);
                    } else {
                        openThongBao("OTP không đúng", Gravity.CENTER);
                    }
                } else {
                    db.collection("nguoidung")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        check++;
                                        if (edtEmail.getText().toString().equals(document.getData().put("username", ""))) {
                                            check = 0;
                                            break;
                                        }
                                    }
                                    if (check == task.getResult().size()) {
                                        openThongBao("Email chưa đăng ký", Gravity.CENTER);
                                        check = 0;
                                    } else {
                                        OTP();
                                        intent.putExtra("opt1", opt1);
                                        intent.putExtra("opt2", opt2);
                                        intent.putExtra("opt3", opt3);
                                        intent.putExtra("opt4", opt4);
                                        tvBtn.setText("Xác minh");
                                        startService(intent);
                                        openThongBao("Đã gửi OTP", Gravity.CENTER);
                                    }
                                }
                            });

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        check();
    }


    // check services chạy hay chưa
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void check() {
        if (isMyServiceRunning(OtpIntentService.class)) {
            tvBtn.setText("Xác minh");
        } else {
            tvBtn.setText("Gửi OTP");
        }
    }


    public void OTP() {
        final int min = 0;
        final int max = 9;
        opt1 = new Random().nextInt((max - min) + 1) + min;
        opt2 = new Random().nextInt((max - min) + 1) + min;
        opt3 = new Random().nextInt((max - min) + 1) + min;
        opt4 = new Random().nextInt((max - min) + 1) + min;
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
}