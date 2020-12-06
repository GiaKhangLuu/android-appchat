package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.CreateGroupActivity;
import com.sinhvien.appchatsocketio.model.User;

import java.util.LinkedList;

public class UserSelectionsAdapter extends
        RecyclerView.Adapter<UserSelectionsAdapter.ViewHolder> {
    private Context context;
    private LinkedList<User> users;
    private UserSelectionsListener listener;

    public interface UserSelectionsListener {
        void SetUncheckFromSearchedUser(User user);
    }

    public UserSelectionsAdapter(Context context, LinkedList<User> users) {
        this.context = context;
        this.users = users;
        this.listener = (CreateGroupActivity) context;
    }

    @NonNull
    @Override
    public UserSelectionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.row_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSelectionsAdapter.ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.tvName.setText(user.getDisplayName());
        holder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.SetUncheckFromSearchedUser(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        ImageButton imgBtnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberName);
            imgBtnDelete = itemView.findViewById(R.id.imgBtnDelete);
        }
    }
}
