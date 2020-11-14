package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.MainActivity;
import com.sinhvien.appchatsocketio.fragment.GroupFragment;
import com.sinhvien.appchatsocketio.model.Room;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends ArrayAdapter<Room> {
    private Context context;
    private int layout;
    private ArrayList<Room> rooms;

    public RoomAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Room> objects) {
        super(context, resource, objects);
        this.context = context;
        layout = resource;
        rooms = objects;
    }

    private void Init(final Room room, View view) {
        TextView tvRoomName;
        ImageButton imgBtnLeave;
        tvRoomName = view.findViewById(R.id.tvRoomName);
        imgBtnLeave = view.findViewById(R.id.imgBtnLeave);
        tvRoomName.setText(room.getName());
        imgBtnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupFragment fragment = ((GroupFragment) ((MainActivity) context).getSupportFragmentManager().
                        findFragmentById(R.id.containerMain));
                fragment.LeaveRoom(room);
            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(layout, parent, false);
        Room room = rooms.get(position);
        Init(room, convertView);
        return convertView;
    }
}
