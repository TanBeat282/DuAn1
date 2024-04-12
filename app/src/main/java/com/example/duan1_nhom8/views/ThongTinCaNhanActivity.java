package com.example.duan1_nhom8.views;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThongTinCaNhanActivity extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout linearChangeTenNguoiDung, linearChangeNgaySinh, linearChangeSDT, linearChangeDiaChi;
    private TextView txtTen, txtSDT, txtEmail, txtTenNguoiDung, txtNgaySinh, txtDiaChi;
    private CircleImageView ivChangeAvatar;
    private String username = "", id = "", tentaikhoan = "", sdt, diachi, ngaysinh, password, avatar, changeAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_ca_nhan);
        ImageView imageViewBack = findViewById(R.id.ic_back);
        linearChangeTenNguoiDung = findViewById(R.id.linearChangeTenNguoiDung);
        linearChangeNgaySinh = findViewById(R.id.linearChangeNgaySinh);
        linearChangeSDT = findViewById(R.id.linearChangeSDT);
        linearChangeDiaChi = findViewById(R.id.linearChangeDiaChi);
        ivChangeAvatar = findViewById(R.id.ivChangeAvatar);

        txtTen = findViewById(R.id.txtTen);
        txtEmail = findViewById(R.id.txtEmail);
        txtTenNguoiDung = findViewById(R.id.txtTenNguoiDung);
        txtDiaChi = findViewById(R.id.txtDiaChi);
        txtSDT = findViewById(R.id.txtSDT);
        txtNgaySinh = findViewById(R.id.txtNgaySinh);

        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        id = preferences.getString("id", "");


        // lay thong tin
        Reload();
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        linearChangeTenNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeTenNguoiDung(Gravity.CENTER);
            }
        });
        linearChangeNgaySinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeNgaySinh(Gravity.CENTER);
            }
        });
        linearChangeSDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeSDT(Gravity.CENTER);
            }
        });
        linearChangeDiaChi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeDiaChi(Gravity.CENTER);
            }
        });


    }

    public void openChangeAvatar() {
        Map<String, Object> item = new HashMap<>();
        item.put("username", username);
        item.put("password", password);
        item.put("tentaikhoan", tentaikhoan);
        item.put("ngaysinh", ngaysinh);
        item.put("sdt", sdt);
        item.put("diachi", diachi);
        item.put("avatar", changeAvatar);

        db.collection("nguoidung")
                .document(id)
                .set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        deleteAvatar();
                        Reload();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        openThongBao("Đổi không thành công", Gravity.CENTER);
                    }
                });
    }

    public void openChangeTenNguoiDung(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_change_thongtincanhan);
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
        dialog.show();

        EditText edtChangeTenNguoiDung = dialog.findViewById(R.id.edtChangeTenNguoiDung);
        LinearLayout linearChangeTenNguoiDung = dialog.findViewById(R.id.linearChangeTenNguoiDung);
        LinearLayout linearHuyChangeTenNguoiDung = dialog.findViewById(R.id.linearHuyChangeTenNguoiDung);
        edtChangeTenNguoiDung.setText(tentaikhoan);
        TextView txtChange = dialog.findViewById(R.id.txtChange);
        txtChange.setText("Đổi tên người dùng");
        linearChangeTenNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> item = new HashMap<>();
                item.put("username", username);
                item.put("password", password);
                item.put("tentaikhoan", edtChangeTenNguoiDung.getText().toString());
                item.put("ngaysinh", ngaysinh);
                item.put("sdt", sdt);
                item.put("diachi", diachi);
                item.put("avatar", avatar);


                if (edtChangeTenNguoiDung.getText().toString().length() > 0) {
                    db.collection("nguoidung")
                            .document(id)
                            .set(item)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    openThongBao("Đổi thành công", Gravity.CENTER);
                                    Reload();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    openThongBao("Đổi không thành công", Gravity.CENTER);
                                }
                            });
                } else {
                    openThongBao("Chưa nhập tên người dùng", Gravity.CENTER);

                }
            }
        });
        linearHuyChangeTenNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                
            }
        });

    }

    public void openChangeNgaySinh(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_change_thongtincanhan);
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
        dialog.show();
        TextView txtChange = dialog.findViewById(R.id.txtChange);
        txtChange.setText("Đổi ngày sinh");
        EditText edtChangeTenNguoiDung = dialog.findViewById(R.id.edtChangeTenNguoiDung);
        LinearLayout linearChangeTenNguoiDung = dialog.findViewById(R.id.linearChangeTenNguoiDung);
        LinearLayout linearHuyChangeTenNguoiDung = dialog.findViewById(R.id.linearHuyChangeTenNguoiDung);
        edtChangeTenNguoiDung.setText(ngaysinh);

        linearChangeTenNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> item = new HashMap<>();
                item.put("username", username);
                item.put("password", password);
                item.put("tentaikhoan", tentaikhoan);
                item.put("ngaysinh", edtChangeTenNguoiDung.getText().toString());
                item.put("sdt", sdt);
                item.put("diachi", diachi);
                item.put("avatar", avatar);

                if (edtChangeTenNguoiDung.getText().toString().length() > 0) {
                    db.collection("nguoidung")
                            .document(id)
                            .set(item)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    openThongBao("Đổi thành công", Gravity.CENTER);
                                    Reload();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    openThongBao("Đổi không thành công", Gravity.CENTER);
                                }
                            });
                } else {
                    openThongBao("Chưa nhập ngày sinh", Gravity.CENTER);

                }
            }
        });
        linearHuyChangeTenNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void openChangeSDT(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_change_thongtincanhan);
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
        dialog.show();
        TextView txtChange = dialog.findViewById(R.id.txtChange);
        txtChange.setText("Đổi số điện thoại");
        EditText edtChangeTenNguoiDung = dialog.findViewById(R.id.edtChangeTenNguoiDung);
        LinearLayout linearChangeTenNguoiDung = dialog.findViewById(R.id.linearChangeTenNguoiDung);
        LinearLayout linearHuyChangeTenNguoiDung = dialog.findViewById(R.id.linearHuyChangeTenNguoiDung);
        edtChangeTenNguoiDung.setText(sdt);

        linearChangeTenNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> item = new HashMap<>();
                item.put("username", username);
                item.put("password", password);
                item.put("tentaikhoan", tentaikhoan);
                item.put("ngaysinh", ngaysinh);
                item.put("sdt", edtChangeTenNguoiDung.getText().toString());
                item.put("diachi", diachi);
                item.put("avatar", avatar);

                if (edtChangeTenNguoiDung.getText().toString().length() > 0) {
                    db.collection("nguoidung")
                            .document(id)
                            .set(item)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    openThongBao("Đổi thành công", Gravity.CENTER);
                                    Reload();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    openThongBao("Đổi không thành công", Gravity.CENTER);
                                }
                            });
                } else {
                    openThongBao("Chưa nhập số điện thoại", Gravity.CENTER);

                }
            }
        });
        linearHuyChangeTenNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void openChangeDiaChi(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_change_thongtincanhan);
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
        dialog.show();
        TextView txtChange = dialog.findViewById(R.id.txtChange);
        txtChange.setText("Đổi địa chỉ");
        EditText edtChangeTenNguoiDung = dialog.findViewById(R.id.edtChangeTenNguoiDung);
        LinearLayout linearChangeTenNguoiDung = dialog.findViewById(R.id.linearChangeTenNguoiDung);
        LinearLayout linearHuyChangeTenNguoiDung = dialog.findViewById(R.id.linearHuyChangeTenNguoiDung);
        edtChangeTenNguoiDung.setText(diachi);

        linearChangeTenNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> item = new HashMap<>();
                item.put("username", username);
                item.put("password", password);
                item.put("tentaikhoan", tentaikhoan);
                item.put("ngaysinh", ngaysinh);
                item.put("sdt", sdt);
                item.put("diachi", edtChangeTenNguoiDung.getText().toString());
                item.put("avatar", avatar);

                if (edtChangeTenNguoiDung.getText().toString().length() > 0) {
                    db.collection("nguoidung")
                            .document(id)
                            .set(item)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    openThongBao("Đổi thành công", Gravity.CENTER);
                                    Reload();
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    openThongBao("Đổi không thành công", Gravity.CENTER);
                                }
                            });
                } else {
                    openThongBao("Chưa nhập địa chỉ", Gravity.CENTER);

                }
            }
        });
        linearHuyChangeTenNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void Reload() {
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
                                    avatar = map.get("avatar").toString();

                                    txtTen.setText(tentaikhoan);
                                    txtEmail.setText(username);
                                    txtTenNguoiDung.setText(tentaikhoan);
                                    txtNgaySinh.setText(ngaysinh);
                                    txtSDT.setText(sdt);
                                    txtDiaChi.setText(diachi);
                                    loadImage(avatar);
                                    break;
                                }
                            }
                        }

                    }
                });
    }

    public void loadImage(String url) {
        Uri downloadUri = Uri.parse(url);
        Glide.with(this)
                .load(downloadUri)
                .into(ivChangeAvatar);
    }

    private void selectImage() {
        Boolean isAllowed = ActivityCompat.checkSelfPermission(ThongTinCaNhanActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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

    // upload bitmap len firebase
    private void upLoadImage(Bitmap bitmap) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference imageReference = storageReference
                .child("Avatar/" + username + ".jpg");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
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
                    changeAvatar = String.valueOf(downloadUri);
                    openChangeAvatar();
                }
            }
        });
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