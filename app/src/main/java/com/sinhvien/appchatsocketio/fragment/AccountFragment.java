package com.sinhvien.appchatsocketio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.adapter.SettingAdapter;
import com.sinhvien.appchatsocketio.model.User;

import java.util.ArrayList;

public class AccountFragment extends Fragment {
    private TextView tvDisplayName;
    private User user;
    ListView lvSetting;
    Button btnSignOut;
    ArrayList<String> settingTitles;
    SettingAdapter settingAdapter;

    private void SetSettingTitles() {
        settingTitles = new ArrayList<>();
        settingTitles.add("Change Display Name");
        settingTitles.add("Change Phone Number");
        settingTitles.add("Change Password");
    }

    public AccountFragment(User user) {
        this.user = user;
    }

    private void Init(View view) {
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        tvDisplayName.setText(user.getDisplayName());
        lvSetting = view.findViewById(R.id.lvSetting);
        btnSignOut = view.findViewById(R.id.btnSignOut);
        SetSettingTitles();
        settingAdapter = new SettingAdapter(getContext(), R.layout.row_setting, settingTitles);
        lvSetting.setAdapter(settingAdapter);
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
