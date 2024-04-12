package com.example.duan1_nhom8.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.models.ListChatAdmin;
import com.example.duan1_nhom8.views.ChatActivity;

import java.util.List;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.MyViewHolder> {
    Context context;
    List<ListChatAdmin> list;


    public ListChatAdapter(Context context, List<ListChatAdmin> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_listmess, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {
        ListChatAdmin listChatAdmin = list.get(position);
        holder.txtTenTaiKhoan.setText(listChatAdmin.getTennguoidung());
        holder.txtId.setText(listChatAdmin.getId());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("id", listChatAdmin.getId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTenTaiKhoan, txtId, txtThongBao;
        ItemClickListener ItemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenTaiKhoan = itemView.findViewById(R.id.txtTenNguoiDung);
            txtId = itemView.findViewById(R.id.txtId);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(com.example.duan1_nhom8.adapters.ItemClickListener itemClickListener) {
            ItemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            ItemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
