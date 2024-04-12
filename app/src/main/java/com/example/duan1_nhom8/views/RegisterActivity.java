package com.example.duan1_nhom8.views;

import com.example.duan1_nhom8.MainActivity;
import com.example.duan1_nhom8.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private EditText edtEmail, editPassword, editPassword2;
    private ImageView btnPasswordIcon, btnPasswordIcon2;
    private LinearLayout btnSignUp;
    private TextView tvBtnRegister;
    private String EMAIL_REGEX = "^[A-Za-z0-9]+[A-Za-z0-9]*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)$";
    private String SDT_PATTERN = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
    private boolean passwordShowIcon = false;
    private boolean passwordShowIcon2 = false;
    private String username, id, tentaikhoan, sdt, diachi, ngaysinh, password, url, user;
    private Integer check, count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = findViewById(R.id.edtEmail);
        editPassword = findViewById(R.id.editPassword);
        editPassword2 = findViewById(R.id.editPassword2);
        btnPasswordIcon = findViewById(R.id.btnPasswordIcon);
        btnPasswordIcon2 = findViewById(R.id.btnPasswordIcon2);
        tvBtnRegister = findViewById(R.id.tvBtnRegister);
        btnSignUp = findViewById(R.id.btnSignUp);

        TextView btnSignIn = findViewById(R.id.btnSignIn);

        // an hien pass
        btnPasswordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordShowIcon) {
                    passwordShowIcon = false;
                    //hien thi icon show pass
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnPasswordIcon.setImageResource(R.drawable.ic_password_show);
                } else {
                    passwordShowIcon = true;
                    editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnPasswordIcon.setImageResource(R.drawable.ic_password_hide);
                }
                editPassword.setSelection(editPassword.length());
            }
        });
        btnPasswordIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordShowIcon2) {
                    passwordShowIcon2 = false;
                    //hien thi icon show pass
                    editPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnPasswordIcon2.setImageResource(R.drawable.ic_password_show);
                } else {
                    passwordShowIcon2 = true;
                    editPassword2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnPasswordIcon2.setImageResource(R.drawable.ic_password_hide);
                }
                editPassword2.setSelection(editPassword2.length());
            }
        });
        //

        // lay data tu otpactivity
        check = getIntent().getIntExtra("check", 0);
        user = getIntent().getStringExtra("email");


        // thay doi text button
        if (check == 1) {
            edtEmail.setText(user);
            edtEmail.setEnabled(false);
            tvBtnRegister.setText("Đổi mật khẩu");
        }
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = editPassword.getText().toString();
                Intent intent = new Intent(RegisterActivity.this, ThemThongTinActivity.class);
                //check bang 1 doi mat khau, check bang 0 register
                if (check == 1) {
                    if (checkForm()) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("username", username);
                        item.put("password", editPassword2.getText().toString());
                        item.put("tentaikhoan", tentaikhoan);
                        item.put("ngaysinh", ngaysinh);
                        item.put("sdt", sdt);
                        item.put("diachi", diachi);
                        item.put("avatar", url);

                        if (checkForm() == true) {
                            if (editPassword2.getText().toString().equals(password)) {
                                db.collection("nguoidung")
                                        .document(id)
                                        .set(item)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                openThongBao("Đổi thành công", Gravity.CENTER);
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                openThongBao("Đổi không thành công", Gravity.CENTER);
                                            }
                                        });
                            } else {
                                openThongBao("Mật khẩu cũ không đúng", Gravity.CENTER);
                            }
                        }
                    }
                } else {
                    if (checkForm()) {
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        startActivity(intent);

                    }
                }
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    //check form register
    public boolean checkForm() {
        String email = edtEmail.getText().toString();
        String password = editPassword.getText().toString();
        String password2 = editPassword2.getText().toString();
        checkEmail(email);
        //email
        if (!email.matches(EMAIL_REGEX) || email.length() == 0) {
            openThongBao("Sai định dạng email", Gravity.CENTER);
            return false;
        }
        if (count != 0) {
            openThongBao("Email này đã đăng ký", Gravity.CENTER);
            return false;
        }

        //password
        if (password.length() < 4) {
            openThongBao("Password quá ngắn", Gravity.CENTER);
            return false;
        }

        // comfirm pass
        if (!password.equals(password2)) {
            openThongBao("Password không giống nhau", Gravity.CENTER);
            return false;
        }
        return true;
    }

    // neu check bang 1 chuyen sang doi mat khau
    @Override
    protected void onResume() {
        super.onResume();
        if (check == 1) {
            Reload();
        }
    }

    // load lai du dieu sau khi update
    public void Reload() {
        // lay id account
        db.collection("nguoidung")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (user.equals(document.getData().put("username", ""))) {
                                id = document.getId();
                                break;
                            }
                        }
                    }
                });

        // lay thong tin tu id
        db.collection("nguoidung")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(id)) {
                                    Map<String, Object> map = document.getData();
                                    tentaikhoan = map.get("tentaikhoan").toString();
                                    sdt = map.get("sdt").toString();
                                    diachi = map.get("diachi").toString();
                                    ngaysinh = map.get("ngaysinh").toString();
                                    username = map.get("username").toString();
                                    password = map.get("password").toString();
                                    url = map.get("avatar").toString();
                                    break;
                                }
                            }
                        }

                    }
                });
    }

    public void checkEmail(String email) {
        db.collection("nguoidung")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (email.equals(document.getData().put("username", ""))) {
                                count = 1;
                                break;
                            }
                        }
                    }
                });//
    }

    // dialog thong bao
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