package com.example.duan1_nhom8.views;

import com.example.duan1_nhom8.MainActivity;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.DanhSachSachAdapter;
import com.example.duan1_nhom8.models.Sach;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText edtEmail, editPassword;
    private boolean passwordShowIcon = false;
    private int check = 0, check1 = 0, check2 = 0;
    private String email;
    private String EMAIL_REGEX = "^[A-Za-z0-9]+[A-Za-z0-9]*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)$";
    private DialogLoading dialogLoading;
    private GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LinearLayout buttonLogin = findViewById(R.id.btnSignIn);
        TextView btnForgotPassword = findViewById(R.id.btnForgotPassword);
        TextView btnSignUp = findViewById(R.id.btnSignUp);
        ImageView btnPasswordIcon = findViewById(R.id.btnPasswordIcon);
        RelativeLayout btnSignInWithGoogle = findViewById(R.id.btnSignInWithGoogle);

        editPassword = findViewById(R.id.editPassword);
        edtEmail = findViewById(R.id.edtEmail);

        // doc login
        readResult();

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
                    // hien thi icon hide pass
                    passwordShowIcon = true;
                    editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnPasswordIcon.setImageResource(R.drawable.ic_password_hide);
                }
                editPassword.setSelection(editPassword.length());
            }
        });

        //đăng nhập
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtEmail.getText().toString();
                String password = editPassword.getText().toString();
                if (checkForm(username, password)) {
                    onClickSignIn();
                }
            }
        });

        //quên mk
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, OTPActivity.class));
                finish();
            }
        });

        //đăng ký
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });


        // google
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(LoginActivity.this, gso);
        btnSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleIntent = gsc.getSignInIntent();
                googleLaucher.launch(googleIntent);
            }
        });
    }

    // user password
    public void onClickSignIn() {
        String username = edtEmail.getText().toString();
        String password = editPassword.getText().toString();

        dialogLoading = new DialogLoading(LoginActivity.this, "Đang đăng nhập...");
        dialogLoading.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("nguoidung")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    check++;
                                    if (username.equals(document.getData().put("username", ""))) {
                                        //mật khẩu
                                        db.collection("nguoidung")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                        for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                            check1++;
                                                            if (password.equals(document1.getData().put("password", ""))) {
                                                                if (document1.getId().equals(document.getId())) {
                                                                    writeResult(document1.getId(), username, password);
                                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                                                    check2 = 1;
                                                                    finish();
                                                                    break;
                                                                }
                                                            }
                                                            if (check1 == task1.getResult().size()) {
                                                                openThongBao("Sai mật khẩu", Gravity.CENTER);
                                                                check1 = 0;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                });//

                                        check = 0;
                                        break;
                                    }
                                    //nếu check bằng độ dài mảng thì thông báo email chưa đăng ký
                                    if (check == task.getResult().size()) {
                                        openThongBao("Email chưa đăng ký", Gravity.CENTER);
                                        check = 0;
                                        break;
                                    }
                                }
                            }
                        });

                dialogLoading.closeDialog();
            }
        }, 3000);

    }
    //

    //check form
    public boolean checkForm(String email, String password) {
        //email
        if (email.length() == 0) {
            openThongBao("Không để trống email", Gravity.CENTER);
            return false;
        } else if (!email.matches(EMAIL_REGEX)) {
            openThongBao("Sai định dạng email", Gravity.CENTER);
            return false;
        }
        //password
        else if (password.length() == 0) {
            openThongBao("Không để trống mật khẩu", Gravity.CENTER);
            return false;
        }
        return true;
    }

    // google
    ActivityResultLauncher<Intent> googleLaucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        email = account.getEmail();
                        signinGoogle();
                    } catch (Exception e) {
                    }
                }
            }
    );
    //


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

    //ghi ghi nho dang nhap
    public void writeResult(String id, String username, String password) {
        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("idLoginIn", true);
        editor.putString("id", id);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    // doc ghi nho dang nhap
    public void readResult() {
        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        Boolean isLogin = preferences.getBoolean("idLoginIn", false);
        if (isLogin) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // dn va dk  google
    private void signinGoogle() {
        db.collection("nguoidung")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // neu email da dk thi vao man hinh chinh
                            if (email.equals(document.getData().put("username", ""))) {
                                writeResult(document.getId(), email, null);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            } else {
                                // chua dk thi chuyen qua nhap thong tin
                                Intent intent = new Intent(LoginActivity.this, ThemThongTinActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password", "");
                                startActivity(intent);
                            }
                        }
                    }
                });
    }
}
