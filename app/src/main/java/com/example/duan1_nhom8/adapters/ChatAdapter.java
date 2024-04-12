package com.example.duan1_nhom8.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1_nhom8.R;
import com.example.duan1_nhom8.models.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Chat> list;
    private final String idnguoigui;
    private static final int TYPE_SEND = 1;
    private static final int TYPE_RECEIVE = 2;

    public ChatAdapter(Context context, List<Chat> list, String idnguoigui) {
        this.context = context;
        this.list = list;
        this.idnguoigui = idnguoigui;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_SEND) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_itemchat_admin, parent, false);
            return new SendMessViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.custom_itemchat_nguoidung, parent, false);
            return new ReceivedMessViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_SEND) {
            ((SendMessViewHolder) holder).txtNoiDung.setText(list.get(position).noidung);
            ((SendMessViewHolder) holder).txtDate.setText(list.get(position).date);
        } else {
            ((ReceivedMessViewHolder) holder).txtNoiDung.setText(list.get(position).noidung);
            ((ReceivedMessViewHolder) holder).txtDate.setText(list.get(position).date);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).idnguoigui.equals(idnguoigui)) {
            return TYPE_SEND;
        } else {
            return TYPE_RECEIVE;
        }
    }

    static class SendMessViewHolder extends RecyclerView.ViewHolder {
        TextView txtNoiDung, txtDate;

        public SendMessViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNoiDung = itemView.findViewById(R.id.txtNoiDung);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }

    static class ReceivedMessViewHolder extends RecyclerView.ViewHolder {
        TextView txtNoiDung, txtDate;

        public ReceivedMessViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNoiDung = itemView.findViewById(R.id.txtNoiDung1);
            txtDate = itemView.findViewById(R.id.txtDate1);
        }
    }
}
