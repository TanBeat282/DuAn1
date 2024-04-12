package com.example.duan1_nhom8.views;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.MainActivity;
import com.example.duan1_nhom8.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ThemThongTinActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText edtTenTaiKhoan, edtNgaySinh, edtSDT, edtDiaChi;
    private LinearLayout btnHoanTat;
    private TextView tvEmailOrSDT;
    private String ngaysinh;
    private ImageView ivDate, addAvatar;
    private String NS_PATTERN = "^[A-Za-z0-9]+[A-Za-z0-9]*@[fpt]+(\\.[.edu.vn]+)$";
    private String AVATAR = "https://firebasestorage.googleapis.com/v0/b/pj01-561e9.appspot.com/o/Avatar%2Favatar%20mac%20dinh%2Favatar.jpg?alt=media&token=a351c2c7-223a-48d6-85b1-ada19eae0f02";
    private String email, matkhau, url;
    private DatePickerDialog datePickerDialog;
    private String patterndate = "dd-M-yyyy";
    //    private String patternhour = "dd-M-yyyy hh:mm:ss";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patterndate);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_thong_tin);

        tvEmailOrSDT = findViewById(R.id.tvEmailOrSDT);
        edtTenTaiKhoan = findViewById(R.id.edtTenTaiKhoan);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        edtSDT = findViewById(R.id.edtSDT);
        edtDiaChi = findViewById(R.id.edtDiaChi);
        btnHoanTat = findViewById(R.id.btnHoanTat);
        addAvatar = findViewById(R.id.addAvatar);
        ivDate = findViewById(R.id.ivDate);

        edtNgaySinh.setText(simpleDateFormat.format(new Date()));
        // lay email pass từ register
        email = getIntent().getStringExtra("email");
        if (getIntent().getStringExtra("password").equals("")) {
            matkhau = "";
        } else {
            matkhau = getIntent().getStringExtra("password");
        }
        tvEmailOrSDT.setText(email);
        addAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnHoanTat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClick();
            }
        });
        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(ThemThongTinActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edtNgaySinh.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        format(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void format(String date) {
        Date localTime = null;
        try {
            localTime = new SimpleDateFormat(" dd MMM yyyy ", Locale.getDefault()).parse(date);
            ngaysinh = String.valueOf(localTime.getTime());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    public void onSaveClick() {
        String username = email;
        String password = matkhau;
        String tentaikhoan = edtTenTaiKhoan.getText().toString();
        ngaysinh = edtNgaySinh.getText().toString();
        String sdt = edtSDT.getText().toString();
        String diachi = edtDiaChi.getText().toString();
        String avatar;
        if (url == null) {
            avatar = AVATAR;
        } else {
            avatar = url;
        }
        Map<String, Object> item = new HashMap<>();
        item.put("username", username);
        item.put("password", password);
        item.put("tentaikhoan", tentaikhoan);
        item.put("ngaysinh", ngaysinh);
        item.put("sdt", sdt);
        item.put("diachi", diachi);
        item.put("avatar", avatar);

        db.collection("nguoidung")
                .add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //thong bao dang ki thanh cong
                        openThongBao("Đăng kí thành công", Gravity.CENTER);

                        // get id nguoi dung vua moi dang ki
                        db.collection("nguoidung")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (email.equals(document.getData().put("username", ""))) {
                                                String id = document.getId();
                                                Intent intent = new Intent(ThemThongTinActivity.this, MainActivity.class);
                                                // ghi nho dang nhap
                                                writeResult(id, username, password);
                                                startActivity(intent);
                                                break;
                                            }
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        openThongBao("Đăng kí không thành công", Gravity.CENTER);
                    }
                });

    }

    //    public boolean checkFormThongTin() {
//        String tentaikhoan = edtTenTaiKhoan.getText().toString();
//        String ngaysinh = edtNgaySinh.getText().toString();
//        Integer sdt = Integer.valueOf(edtSDT.getText().toString());
//        String diachi= edtDiaChi.getText().toString();
//
//        //email
//        if ( tentaikhoan.length() <=1) {
//            Toast.makeText(ThemThongTinActivity.this, "Tên tài khoản quá ngắn", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        //password
//        if (ngaysinh.length() < 4) {
//            Toast.makeText(ThemThongTinActivity.this, "Password quá ngắn", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        // comfirm pass
//        if (!password.equals(password2)) {
//            Toast.makeText(ThemThongTinActivity.this, "Password không giống nhau", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }


    //chon hinh anh
    private void selectImage() {
        Boolean isAllowed = ActivityCompat.checkSelfPermission(ThemThongTinActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (isAllowed) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    launcher.launch(intent);
                }
                break;
            }
            default:
                break;
        }
    }


    // tra ve hinh anh
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Uri selectedImage = intent.getData();
                        String[] filePath = {MediaStore.Images.Media.DATA};
                        Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePath[0]);
                        String picturePath = c.getString(columnIndex);
                        c.close();
                        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                        upLoadImage(thumbnail);
                    }
                }
            }
    );

    // upload hinh anh  len firebase
    private void upLoadImage(Bitmap bitmap) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference imageReference = storageReference
                .child("Avatar/" + email + ".jpg");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        UploadTask uploadTas = imageReference.putBytes(bytes);
        uploadTas.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.isSuccessful()) return imageReference.getDownloadUrl();
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    url = String.valueOf(downloadUri);
                    ImageView imageView = findViewById(R.id.addAvatar);
                    Glide.with(ThemThongTinActivity.this)
                            .load(downloadUri)
                            .into(imageView);
                }
            }
        });
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

    public void writeResult(String id, String username, String password) {
        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("idLoginIn", true);
        editor.putString("id", id);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }
}