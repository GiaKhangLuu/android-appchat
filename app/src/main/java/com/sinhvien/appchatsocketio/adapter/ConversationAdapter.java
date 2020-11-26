package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.MessageActivity;
import com.sinhvien.appchatsocketio.model.Conversation;
import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

import java.util.ArrayList;

public class ConversationAdapter extends
        RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private Context context;
    private User user;
    private ArrayList<Conversation> conversations;

    public ConversationAdapter(Context context, ArrayList<Conversation> conversations, User user) {
        this.context = context;
        this.conversations = conversations;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Conversation conv = conversations.get(position);
        holder.tvName.setText(conv.getName());
        holder.tvMessage.setText(conv.getMessage());
        holder.tvTime.setText(conv.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Room room = new Room();
                room.setName(conv.getName());
                room.setIdRoom(conv.getRoomId());
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("User", user);
                intent.putExtra("Room", room);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView  tvName, tvMessage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDisplayName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
