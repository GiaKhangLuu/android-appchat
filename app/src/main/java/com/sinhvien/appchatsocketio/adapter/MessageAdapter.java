package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.model.Message;

import java.util.ArrayList;

public class MessageAdapter extends
        RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_CENTER = 2;
    private Context context;
    private ArrayList<Message> messages;
    private String userId;

    public MessageAdapter(Context context, ArrayList<Message> messages, String userId) {
        this.context = context;
        this.messages = messages;
        this.userId = userId;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_left, parent, false);
            return new ViewHolder(view);
        }
        if(viewType == MSG_TYPE_CENTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_center, parent, false);
            return new ViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_right, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message mess = messages.get(position);
        if(holder.tvDisplayName != null) {
            holder.tvDisplayName.setText(mess.getDisplayName());
        }
        holder.tvMessage.setText(mess.getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getSenderId().isEmpty()) return MSG_TYPE_CENTER;
        if(messages.get(position).getSenderId().equals(userId)) return MSG_TYPE_RIGHT;
        return MSG_TYPE_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDisplayName;
        private TextView tvMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
