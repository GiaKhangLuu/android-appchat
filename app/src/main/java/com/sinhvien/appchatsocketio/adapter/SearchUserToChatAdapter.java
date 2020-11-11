package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchUserToChatAdapter extends ArrayAdapter<User> {
    Context context;
    int layout;
    ArrayList<User> users;
    TextView tvUserDisplayName;
    Button btnChat;

    public SearchUserToChatAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
        this.context = context;
        layout = resource;
        users = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(layout, parent, false);
        tvUserDisplayName = convertView.findViewById(R.id.tvDisplayName);
        btnChat = convertView.findViewById(R.id.btnSendMessage);
        tvUserDisplayName.setText(users.get(position).getDisplayName());
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, users.get(position).getIdUser(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}
