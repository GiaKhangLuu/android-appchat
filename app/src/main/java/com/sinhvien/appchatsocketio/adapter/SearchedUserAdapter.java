package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.CreateGroupActivity;
import com.sinhvien.appchatsocketio.model.Ultilities;
import com.sinhvien.appchatsocketio.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchedUserAdapter extends ArrayAdapter<User> {
    private Context context;
    private int layout;
    private ArrayList<User> users;
    TextView tvName;
    CheckBox chbChoose;

    public SearchedUserAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
        this.context = context;
        layout = resource;
        users = objects;
    }

    private void Init(final User user, View view) {
        tvName = view.findViewById(R.id.tvName);
        chbChoose = view.findViewById(R.id.chbChoose);
        tvName.setText(user.getDisplayName());
        if(((CreateGroupActivity)context).IndexOfUserInUserSelection(user) > -1) {
            chbChoose.setChecked(true);
        }
        chbChoose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ((CreateGroupActivity)context).AddUserToUserSelection(user);
                } else {
                    ((CreateGroupActivity)context).RemoveUserFromUserSelection(user);
                }
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
