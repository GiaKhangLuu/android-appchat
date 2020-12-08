package com.sinhvien.appchatsocketio.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.SignInActivity;
import com.sinhvien.appchatsocketio.activity.WelcomeActivity;
import com.sinhvien.appchatsocketio.adapter.SettingAdapter;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.model.User;

import java.util.ArrayList;

import io.socket.client.Socket;

public class AccountFragment extends Fragment {
    private TextView tvDisplayName;
    private User user;
    ListView lvSetting;
    Button btnSignOut;
    ArrayList<String> settingTitles;
    SettingAdapter settingAdapter;
    Socket socket;
    AccountFragmentListener listener;

    public interface AccountFragmentListener {
        void SignOut();
    }

    private void SetSettingTitles() {
        settingTitles = new ArrayList<>();
        settingTitles.add("Change Display Name");
        settingTitles.add("Change Phone Number");
        settingTitles.add("Change Password");
    }

    private void Init(View view) {
        socket = ChatHelper.getInstace(getContext()).GetSocket();
        user = (User) getArguments().getSerializable("User");
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        tvDisplayName.setText(user.getDisplayName());
        lvSetting = view.findViewById(R.id.lvSetting);
        btnSignOut = view.findViewById(R.id.btnSignOut);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("fragment", "destroy");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }
}
