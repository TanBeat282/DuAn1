package com.example.duan1_nhom8.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.adapters.ListChatAdapter;
import com.example.duan1_nhom8.models.ListChatAdmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatOfAdminActivity extends AppCompatActivity {
    private RecyclerView recyclerChat;
    private ListChatAdapter listChatAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<ListChatAdmin> list;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_of_admin);
        recyclerChat = findViewById(R.id.recyclerChat);
        ivBack = findViewById(R.id.ivBack);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setHasFixedSize(true);
        list = new ArrayList<>();
        getListChat();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getListChat() {
        db.collection("chat_admin")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                ListChatAdmin listChatAdmin = new ListChatAdmin();
                                listChatAdmin.setTennguoidung(documentSnapshot.getString("tentaikhoan"));
                                listChatAdmin.setId(documentSnapshot.getString("id"));
                                list.add(listChatAdmin);
                            }
                            if (list.size() > 0) {
                                listChatAdapter = new ListChatAdapter(getApplicationContext(), list);
                                recyclerChat.setAdapter(listChatAdapter);
                            }
                        }
                    }
                });
    }
}