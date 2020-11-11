package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.CreateGroupActivity;
import com.sinhvien.appchatsocketio.model.User;

import java.util.LinkedList;

public class SelectedMemberAdapter extends ArrayAdapter<User> {
    private Context context;
    private int layout;
    private LinkedList<User> users;

    public SelectedMemberAdapter(@NonNull Context context, int resource, @NonNull LinkedList<User> objects) {
        super(context, resource, objects);
        this.context = context;
        layout = resource;
        users = objects;
    }

    private void Init(final User user, View view) {
        TextView tvName;
        ImageButton imgBtnDelete;
        tvName = view.findViewById(R.id.tvMemberName);
        imgBtnDelete = view.findViewById(R.id.imgBtnDelete);
        tvName.setText(user.getDisplayName());
        imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CreateGroupActivity)context).RemoveUserFromUserSelection(user);
                ((CreateGroupActivity)context).SetUncheckFromSearchedUser(user);
            }
        });
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(layout, parent, false);
        User user = users.get(position);
        Init(user, convertView);
        return convertView;
    }
}
