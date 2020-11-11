package com.sinhvien.appchatsocketio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.model.User;

public class AccountFragment extends Fragment {
    private TextView tvDisplayName;
    private User user;

    public AccountFragment(User user) {
        this.user = user;
    }

    private void Init(View view) {
        tvDisplayName = view.findViewById(R.id.tvName);
        tvDisplayName.setText(user.getDisplayName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
    }
}
