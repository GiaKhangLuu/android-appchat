package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.CreateGroupActivity;
import com.sinhvien.appchatsocketio.model.User;

import java.util.ArrayList;

public class SearchedUsersToAddAdapter extends
        RecyclerView.Adapter<SearchedUsersToAddAdapter.ViewHolder> {
    private Context context;
    private ArrayList<User> users;
    private SearchedUserListener listener;

    public interface SearchedUserListener {
        void AddToUserSelections(User user);
        void RemoveFromUserSelections(User user);
        int GetIndexOfUserInUserSelections(User user);
    }

    public SearchedUsersToAddAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        listener = (SearchedUserListener) context;
    }

    @NonNull
    @Override
    public SearchedUsersToAddAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.row_search_user_to_add, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedUsersToAddAdapter.ViewHolder holder, final int position) {
        final User user = users.get(position);
        holder.tvDisplayName.setText(user.getDisplayName());
        holder.chbChoose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    listener.AddToUserSelections(user);
                    return;
                }
                listener.RemoveFromUserSelections(user);
            }
        });
        if(listener.GetIndexOfUserInUserSelections(user) > -1) {
            holder.chbChoose.setChecked(true);
        }
        else {
            holder.chbChoose.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDisplayName;
        private CheckBox chbChoose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDisplayName = itemView.findViewById(R.id.tvDisplayName);
            chbChoose = itemView.findViewById(R.id.chbChoose);
        }
    }
}
