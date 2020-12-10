package com.sinhvien.appchatsocketio.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.ChangePasswordActivity;
import com.sinhvien.appchatsocketio.activity.ChangeProfileActivity;
import com.sinhvien.appchatsocketio.adapter.SettingAdapter;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.model.User;

import java.util.ArrayList;

import io.socket.client.Socket;

public class AccountFragment extends Fragment {
    private final int CHANGE_PROFILE = 0;
    private final int CHANGE_PASSWORD = 1;
    private final int SIGN_OUT = 2;

    private TextView tvDisplayName;
    private User user;
    ListView lvSetting;
    ArrayList<String> settingTitles;
    SettingAdapter settingAdapter;
    Socket socket;
    AccountFragmentListener listener;

    public interface AccountFragmentListener {
        void SignOut();
    }

    private void SetSettingTitles() {
        settingTitles = new ArrayList<>();
        settingTitles.add("Change Profile");
        settingTitles.add("Change Password");
        settingTitles.add("Sign Out");
    }

    private void Init(View view) {
        socket = ChatHelper.getInstace(getContext()).GetSocket();
        user = (User) getArguments().getSerializable("User");
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        tvDisplayName.setText(user.getDisplayName());
        lvSetting = view.findViewById(R.id.lvSetting);
        SetSettingTitles();
        settingAdapter = new SettingAdapter(getContext(), R.layout.row_setting, settingTitles);
        lvSetting.setAdapter(settingAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (AccountFragmentListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    private void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm")
                .setMessage("Do you wanna sign out ?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.SignOut();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.create().show();
    }

    private void ChangeProfile() {
        Intent intent = new Intent(getActivity(), ChangeProfileActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }

    private void ChangePassword() {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }

    private void UserSelection(int option) {
        switch (option) {
            case CHANGE_PROFILE:
                ChangeProfile();
                break;
            case CHANGE_PASSWORD:
                ChangePassword();
                break;
            case SIGN_OUT:
                ShowDialog();
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
        lvSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserSelection(position);
            }
        });
    }
}
