package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.model.Conversation;

import java.util.ArrayList;

public class ConversationAdapter extends ArrayAdapter<Conversation> {
    private Context context;
    private int layout;
    private ArrayList<Conversation> conversations;

    public ConversationAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Conversation> objects) {
        super(context, resource, objects);
        this.context = context;
        layout = resource;
        conversations = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(layout, parent, false);
        Conversation conversation = conversations.get(position);
        SetView(conversation, convertView);
        return convertView;
    }

    private void SetView(Conversation conversation, View view) {
        TextView tvName, tvMessge, tvTime;
        tvName = view.findViewById(R.id.tvName);
        tvMessge = view.findViewById(R.id.tvMessage);
        tvTime = view.findViewById(R.id.tvTime);
        tvName.setText(conversation.getName());
        tvMessge.setText(conversation.getMessage());
        tvTime.setText(conversation.getTime());
    }
}
