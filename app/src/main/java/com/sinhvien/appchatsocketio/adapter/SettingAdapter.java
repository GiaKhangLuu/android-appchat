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

import java.util.ArrayList;
import java.util.List;

public class SettingAdapter extends ArrayAdapter<String> {
    Context context;
    int layout;
    ArrayList<String> settingTitles;

    public SettingAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.context = context;
        layout = resource;
        settingTitles = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(layout, parent, false);
        TextView tvTitle;
        ImageButton imgBtn;
        tvTitle = convertView.findViewById(R.id.tvTitle);
        imgBtn = convertView.findViewById(R.id.imgBtnChoose);
        tvTitle.setText(settingTitles.get(position));
        return convertView;
    }
}
