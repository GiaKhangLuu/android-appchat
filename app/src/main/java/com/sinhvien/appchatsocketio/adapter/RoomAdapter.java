package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.MainActivity;
import com.sinhvien.appchatsocketio.activity.MessageActivity;
import com.sinhvien.appchatsocketio.fragment.GroupFragment;
import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends
        RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private User user;
    private Context context;
    private ArrayList<Room> rooms;

    public RoomAdapter(Context context, ArrayList<Room> rooms, User user) {
        this.context = context;
        this.rooms = rooms;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.row_room, parent, false);
        return new RoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Room room = rooms.get(position);
        holder.tvRoomName.setText(room.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToMessageActivity(room);
            }
        });
    }

    private void MoveToMessageActivity(Room room) {
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra("User", user);
        intent.putExtra("Room", room);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName;
        ImageButton imgBtnLeave;

        public TextView getTvRoomName() {
            return tvRoomName;
        }

        public ImageButton getImgBtnLeave() {
            return imgBtnLeave;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            imgBtnLeave = itemView.findViewById(R.id.imgBtnLeave);
        }
    }


    /*private void Init(final Room room, View view) {
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
    }*/

}
