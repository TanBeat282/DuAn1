package com.example.duan1_nhom8.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.duan1_nhom8.MainActivity;
import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.ChatAdapter;
import com.example.duan1_nhom8.adapters.SachAdapter;
import com.example.duan1_nhom8.models.Chat;
import com.example.duan1_nhom8.models.Sach;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.remote.WatchChange;

import org.checkerframework.checker.units.qual.C;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String id, idnguoigui, idnguoinhan, date, noidung, tentaikhoan, idUser;
    private RecyclerView recyclerChat;
    private List<Chat> list;
    private EditText edtNhapChat;
    private ImageView ivSend, ivBack;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //lay id nguoi dung chat voi admin
        idUser = getIntent().getStringExtra("id");

        // get id nguoi dung
        SharedPreferences preferences = getSharedPreferences("LOGIN_STATUS", MODE_PRIVATE);
        id = preferences.getString("id", "");
        getTenTaiKhoan();

        recyclerChat = findViewById(R.id.recyclerChat);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new ChatAdapter(getApplicationContext(), list, String.valueOf(id));
        recyclerChat.setAdapter(adapter);

        edtNhapChat = findViewById(R.id.edtNhapChat);
        ivSend = findViewById(R.id.ivSend);
        ivBack = findViewById(R.id.ivBack);
        readMess();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMess();
            }
        });
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = list.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    Chat chat = new Chat();
                    chat.idnguoigui = documentChange.getDocument().getString("idnguoigui");
                    chat.idnguoinhan = documentChange.getDocument().getString("idnguoinhan");
                    chat.dateObj = documentChange.getDocument().getDate("date");
                    chat.noidung = documentChange.getDocument().getString("noidung");
                    chat.date = format_date(documentChange.getDocument().getDate("date"));
                    list.add(chat);
                }
            }
            Collections.sort(list, (obj1, obj2) -> obj1.dateObj.compareTo(obj2.dateObj));
            if (count == 0) {
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyItemRangeInserted(list.size(), list.size());
                recyclerChat.smoothScrollToPosition(list.size() - 1);
            }
        }

    });

    private String format_date(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void readMess() {
       if (id.equals("1")){
           db.collection("chat_user")
                   .whereEqualTo("idnguoigui", String.valueOf(id))
                   .whereEqualTo("idnguoinhan", String.valueOf(idUser))
                   .addSnapshotListener(eventListener);

           db.collection("chat_user")
                   .whereEqualTo("idnguoigui", String.valueOf(idUser))
                   .whereEqualTo("idnguoinhan", String.valueOf(id))
                   .addSnapshotListener(eventListener);
       }else {
           db.collection("chat_user")
                   .whereEqualTo("idnguoigui", String.valueOf(id))
                   .whereEqualTo("idnguoinhan", String.valueOf(1))
                   .addSnapshotListener(eventListener);

           db.collection("chat_user")
                   .whereEqualTo("idnguoigui", String.valueOf(1))
                   .whereEqualTo("idnguoinhan", String.valueOf(id))
                   .addSnapshotListener(eventListener);
       }
    }

    private void sendMess() {
        noidung = edtNhapChat.getText().toString();
        if (noidung.length() == 0) {
            //thongbao
        } else {
           if (id.equals("1")){

               HashMap<String, Object> item = new HashMap<>();
               item.put("idnguoigui", id);
               item.put("idnguoinhan", idUser);
               item.put("date", new Date());
               item.put("noidung", edtNhapChat.getText().toString());

               db.collection("chat_user")
                       .add(item)
                       .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                           @Override
                           public void onSuccess(DocumentReference documentReference) {
                               edtNhapChat.setText("");
                               if (id.equals("1")) {

                               } else {
                                   loadUserMess();
                               }
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {

                           }
                       });
            }else {

               HashMap<String, Object> item = new HashMap<>();
               item.put("idnguoigui", id);
               item.put("idnguoinhan", "1");
               item.put("date", new Date());
               item.put("noidung", edtNhapChat.getText().toString());

               db.collection("chat_user")
                       .add(item)
                       .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                           @Override
                           public void onSuccess(DocumentReference documentReference) {
                               edtNhapChat.setText("");
                               if (id.equals("1")) {

                               } else {
                                   loadUserMess();
                               }
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

    private void loadUserMess() {
        HashMap<String, Object> item = new HashMap<>();
        item.put("id", id);
        item.put("tentaikhoan", tentaikhoan);
        db.collection("chat_admin").document(String.valueOf(id)).set(item);

    }

    private void getTenTaiKhoan() {
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
                                    break;
                                }
                            }
                        }

                    }
                });
    }
}