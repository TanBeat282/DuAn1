package com.example.duan1_nhom8.fragments;

import static android.content.Context.MODE_PRIVATE;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.duan1_nhom8.MainActivity;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.models.GioHang;
import com.example.duan1_nhom8.models.ThongTinCaNhan;
import com.example.duan1_nhom8.views.ChatActivity;
import com.example.duan1_nhom8.views.ChatOfAdminActivity;
import com.example.duan1_nhom8.views.ChiTietSachActivity;
import com.example.duan1_nhom8.views.DialogLoading;
import com.example.duan1_nhom8.views.GioHangActivity;
import com.example.duan1_nhom8.views.LoginActivity;
import com.example.duan1_nhom8.views.SanPhamMoiActivity;
import com.example.duan1_nhom8.views.ThongTinCaNhanActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CaNhanFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CaNhanViewModel mViewModel;
    private TextView txtTen, txtSDT, txtEmail, txtText;
    private CircleImageView ivAvatar;
    private String username, id, tentaikhoan, sdt, diachi, ngaysinh, password, url, ID;
    private EditText edtMatKhauCu, edtMatKhauMoi, edtNhapLaiMatKhauMoi;
    private Context mcontext;
    private ImageView ivChat, ivGioHang, ivIcon;
    private DialogLoading dialogLoading;
    private NotificationBadge badge;
    private LinearLayout linearDoiMatKhau, linearDangXuat;

    public static CaNhanFragment newInstance() {
        return new CaNhanFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ca_nhan, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CaNhanViewModel.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        readGioHang();
    }

    // get context
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext = context;
    }
    //

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearThongTinCaNhan = view.findViewById(R.id.linearThongTinCaNhan);
        linearDoiMatKhau = view.findViewById(R.id.linearDoiMatKhau);
        linearDangXuat = view.findViewById(R.id.linearDangXuat);
        txtText = view.findViewById(R.id.txtText);
        ivIcon = view.findViewById(R.id.ivIcon);
        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        ID = preferences.getString("id", "");
        RelativeLayout recyclerChat = view.findViewById(R.id.recyclerChat);
        if (ID.equals("1")) {
            recyclerChat.setVisibility(View.INVISIBLE);
        }
        badge = view.findViewById(R.id.badge);
        txtTen = view.findViewById(R.id.txtTenTaoKhoan);
        txtSDT = view.findViewById(R.id.txtSDT);
        txtEmail = view.findViewById(R.id.txtEmail);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        ivChat = view.findViewById(R.id.ivChat);
        ivGioHang = view.findViewById(R.id.ivGioHang);
        Reload();
        ivGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GioHangActivity.class));
            }
        });
        ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ID.equals("1")) {
                    startActivity(new Intent(getContext(), ChatOfAdminActivity.class));
                } else {
                    startActivity(new Intent(getContext(), ChatActivity.class));
                }
            }
        });

        linearThongTinCaNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ThongTinCaNhanActivity.class));
            }
        });

        linearDoiMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.equals("")) {
                    openLogout();
                } else {
                    openChangePass(Gravity.CENTER);
                }
            }
        });

        linearDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogout();
            }
        });
    }

    // dialog doi mat khau
    public void openChangePass(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_changepassword);
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

        LinearLayout linearChangePass = dialog.findViewById(R.id.linearChangePass);
        LinearLayout linearHuyChangePass = dialog.findViewById(R.id.linearHuyChangePass);
        edtMatKhauCu = dialog.findViewById(R.id.edtMatKhauCu);
        edtMatKhauMoi = dialog.findViewById(R.id.edtMatKhauMoi);
        edtNhapLaiMatKhauMoi = dialog.findViewById(R.id.edtNhapLaiMatKhauMoi);


        linearChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> item = new HashMap<>();
                item.put("username", username);
                item.put("password", edtNhapLaiMatKhauMoi.getText().toString());
                item.put("tentaikhoan", tentaikhoan);
                item.put("ngaysinh", ngaysinh);
                item.put("sdt", sdt);
                item.put("diachi", diachi);
                item.put("avatar", url);

                if (checkForm() == true) {
                    if (edtMatKhauCu.getText().toString().equals(password)) {
                        db.collection("nguoidung")
                                .document(id)
                                .set(item)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        openThongBao("Đổi thành công",g Gravity.CENTER);
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
                        openThongBao("Mật khẩu cũ không đúng", Gravity.CENTER);
                    }
                }
            }
        });

        linearHuyChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // logout
    public void openLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Bạn có muốn đăng xuất?");
        builder.setTitle("Thông báo");

        builder.setPositiveButton("ĐĂNG XUẤT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogLoading = new DialogLoading(getContext(), "Đang đăng xuất...");
                dialogLoading.showDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogLoading.closeDialog();
                        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove("idLoginIn");
                        editor.remove("id");
                        editor.remove("username");
                        editor.remove("password");
                        editor.apply();

                        if (ID.equals("1")) {
                            db.collection("tokenMessesing")
                                    .document("ADMIN")
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                }, 1500);
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

    // check form
    public boolean checkForm() {
        if (edtMatKhauCu.getText().toString().equals("")) {
            openThongBao("Chưa nhập mật khẩu cũ", Gravity.CENTER);
            return false;
        }
        if (edtMatKhauMoi.getText().toString().equals("")) {
            openThongBao("Chưa nhập mật khẩu mới", Gravity.CENTER);
            return false;
        }
        if (!edtNhapLaiMatKhauMoi.getText().toString().equals(edtMatKhauMoi.getText().toString())) {
            openThongBao("Mật khẩu mới không giống nhau", Gravity.CENTER);
            return false;
        }
        return true;
    }

    // load lai du lieu account khi co update
    public void Reload() {
        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        username = preferences.getString("username", "");
        id = preferences.getString("id", "");

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

                                    //neu password bang null nghia la dang dang nhap bang google ẩn linear doi mat khau
                                    if (password.equals("")) {
                                        linearDangXuat.setVisibility(View.INVISIBLE);
                                        ivIcon.setImageResource(R.drawable.ic_baseline_exit_to_app_24);
                                        txtText.setText("Đăng xuất");
                                    }
                                    txtTen.setText(tentaikhoan);
                                    txtSDT.setText(sdt);
                                    txtEmail.setText(username);
                                    loadImage(url);
                                    break;
                                }
                            }
                        }

                    }
                });
    }

    // load hinh anh account
    public void loadImage(String url) {
        Uri downloadUri = Uri.parse(url);
        Glide.with(getContext())
                .load(downloadUri)
                .into(ivAvatar);
    }


    // dialog thong bao
    public void openThongBao(String thongbao, int gravity) {

        if (mcontext != null) {
            final Dialog dialog = new Dialog(mcontext);
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

                                if (document.getId().equals(ID + idSach)) {
                                    GioHang gioHang = new GioHang(idSach, tensach, url, soluong, giaban);
                                    listGioHang.add(gioHang);
                                }
                            }
                            badge.setText(String.valueOf(listGioHang.size()));
                        }
                    }
                });
    }


}