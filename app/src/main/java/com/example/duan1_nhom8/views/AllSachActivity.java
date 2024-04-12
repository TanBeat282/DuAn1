package com.example.duan1_nhom8.views;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.MainActivity;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.DanhSachNguoiDungAdapter;
import com.example.duan1_nhom8.adapters.DanhSachSachAdapter;
import com.example.duan1_nhom8.adapters.SachAdapter;
import com.example.duan1_nhom8.models.Sach;
import com.example.duan1_nhom8.models.ThongTinCaNhan;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AllSachActivity extends AppCompatActivity {
    private ListView lvAllSach;
    private String username, id, tentaikhoan, sdt, diachi, ngaysinh, password, url, ID;
    private String tensach, theloai, nxb, tacgia, giaban, urlSach, nameImg;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Sach> list = new ArrayList<>();
    private ArrayList<ThongTinCaNhan> thongTinCaNhans = new ArrayList<>();
    private EditText edtSeach, edtTenSach, edtTacGia, edtNXB, edtGiaBan;
    private Boolean result = true;
    private Spinner spnTheLoai;
    private LinearLayout linearThemSach;
    private ImageView ivSach;
    private Bitmap thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sach);
        lvAllSach = findViewById(R.id.lvAllSach);
        edtSeach = findViewById(R.id.edtTimkiem);
        linearThemSach = findViewById(R.id.linearThemSach);
        result = getIntent().getBooleanExtra("result", true);
        if (result == true) {
            readAllSach();
        } else {
            linearThemSach.setVisibility(View.INVISIBLE);
            readAllNguoiDung();
        }
        linearThemSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddSach(Gravity.CENTER);
            }
        });
        lvAllSach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (result == true) {
                    Intent intent = new Intent(AllSachActivity.this, ChiTietSachActivity.class);
                    intent.putExtra("id", list.get(position).getId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AllSachActivity.this, ProfileActivity.class);
                    intent.putExtra("idNguoiDung", thongTinCaNhans.get(position).getIdnguoidung());
                    startActivity(intent);
                }

            }
        });
    }

    public void dialogAddSach(int gravity) {
        // lay thoi gian lam ten anh sach
        nameImg = String.valueOf(Calendar.getInstance().getTimeInMillis());

        final Dialog dialog = new Dialog(AllSachActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_item_addsach);
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
        ivSach = dialog.findViewById(R.id.ivSach);
        edtTenSach = dialog.findViewById(R.id.edtTenSach);
        edtTacGia = dialog.findViewById(R.id.edtTacGia);
        spnTheLoai = dialog.findViewById(R.id.spnTheLoai);
        edtNXB = dialog.findViewById(R.id.edtNXB);
        edtGiaBan = dialog.findViewById(R.id.edtGiaBan);

        LinearLayout linearThemSach = dialog.findViewById(R.id.linearThemSach);
        LinearLayout linearHuyThemSach = dialog.findViewById(R.id.linearHuyThemSach);

        String theLoai[] = {"Kinh tế", "Lịch sử", "Khoa học", "Tiếng anh", "Nấu ăn", "Thể thao", "Tiểu thuyết", "Động vật"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, theLoai);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spnTheLoai.setAdapter(adapter);
        spnTheLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                theloai = theLoai[arg2];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        ivSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        linearThemSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> item = new HashMap<>();
                item.put("tensach", edtTenSach.getText().toString());
                item.put("tacgia", edtTacGia.getText().toString());
                item.put("nxb", edtNXB.getText().toString());
                item.put("loaisach", theloai);
                item.put("giaban", edtGiaBan.getText().toString());
                item.put("url", urlSach);
                if (checkForm()) {
                    db.collection("sach")
                            .add(item)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    openThongBao("Thêm sách thành công", Gravity.CENTER);
                                    dialog.dismiss();
                                    list.clear();
                                    readAllSach();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    openThongBao("Thêm sách không thành công", Gravity.CENTER);
                                }
                            });
                }
            }
        });

        linearHuyThemSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private boolean checkForm() {
        if (thumbnail == null) {
            openThongBao("Chưa chọn hình sách", Gravity.CENTER);
            return false;
        }
        if (edtTenSach.length() == 0) {
            openThongBao("Chưa nhập tên sách", Gravity.CENTER);
            return false;
        }
        if (edtTacGia.length() == 0) {
            openThongBao("Chưa nhập tác giả", Gravity.CENTER);
            return false;
        }
        if (edtNXB.length() == 0) {
            openThongBao("Chưa nhập nhà xuất bản", Gravity.CENTER);
            return false;
        }
        if (edtGiaBan.length() == 0) {
            openThongBao("Chưa nhập giá bán", Gravity.CENTER);
            return false;
        }
        if (spnTheLoai == null) {
            openThongBao("Chưa chọn thể loại sách", Gravity.CENTER);
            return false;
        }

        return true;
    }

    private void readAllSach() {
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
                            DanhSachSachAdapter adapter = new DanhSachSachAdapter(AllSachActivity.this, list);
                            lvAllSach.setAdapter(adapter);
                        }

                    }
                });
    }

    public void readAllNguoiDung() {
        db.collection("nguoidung")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getId().equals("1")) {
                                    Map<String, Object> map = document.getData();
                                    id = document.getId();
                                    tentaikhoan = map.get("tentaikhoan").toString();
                                    sdt = map.get("sdt").toString();
                                    diachi = map.get("diachi").toString();
                                    ngaysinh = map.get("ngaysinh").toString();
                                    username = map.get("username").toString();
                                    password = map.get("password").toString();
                                    url = map.get("avatar").toString();
                                    ThongTinCaNhan thongTinCaNhan = new ThongTinCaNhan(id, tentaikhoan, username, diachi, url, ngaysinh, sdt);
                                    thongTinCaNhans.add(thongTinCaNhan);
                                }
                            }
                            DanhSachNguoiDungAdapter adapter = new DanhSachNguoiDungAdapter(AllSachActivity.this, thongTinCaNhans);
                            lvAllSach.setAdapter(adapter);
                        }

                    }
                });
    }

    public void onResume() {
        super.onResume();
        IntentFilter updateFilter = new IntentFilter("UPDATESACH");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(upReceiver, updateFilter);

        IntentFilter deleltefFilter = new IntentFilter("XOASACH");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(delReceiver, deleltefFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(upReceiver);

        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(delReceiver);
    }

    private BroadcastReceiver upReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = intent.getIntExtra("result", 0);
            if (count != 0) {
                openThongBao("Cập nhật sách thành công", Gravity.CENTER);
                list.clear();
                readAllSach();
            } else {
                openThongBao("Cập nhật sách không thành công", Gravity.CENTER);
            }
        }
    };
    private BroadcastReceiver delReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = intent.getIntExtra("result", 0);
            if (count != 0) {
                openThongBao("Xóa sách thành công", Gravity.CENTER);
                list.clear();
                readAllSach();
            } else {
                openThongBao("Xóa sách không thành công", Gravity.CENTER);
            }
        }
    };

    //
    private void selectImage() {
        Boolean isAllowed = ActivityCompat.checkSelfPermission(AllSachActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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
                        thumbnail = (BitmapFactory.decodeFile(picturePath));
                        ivSach.setImageBitmap(thumbnail);
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
                .child("Sach/" + nameImg + ".jpg");
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
                    urlSach = String.valueOf(downloadUri);
                    Glide.with(AllSachActivity.this)
                            .load(downloadUri)
                            .into(ivSach);
                }
            }
        });
    }

    //

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