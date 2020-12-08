package com.sinhvien.appchatsocketio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.adapter.RoomAdapter;
import com.sinhvien.appchatsocketio.fragment.AccountFragment;
import com.sinhvien.appchatsocketio.fragment.GroupFragment;
import com.sinhvien.appchatsocketio.fragment.ConversationsFragment;
import com.sinhvien.appchatsocketio.fragment.SearchFragment;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.helper.LeaveGroupDialog;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity
        implements
        LeaveGroupDialog.LeaveGroupDialogListener,
        AccountFragment.AccountFragmentListener {
    private BottomNavigationView bottomNav;
    private User user;
    private Socket socket;

    private void Init(){
        bottomNav = findViewById(R.id.bottomNav);
        user = (User) getIntent().getSerializableExtra("User");
        socket = ChatHelper.getInstace(this).GetSocket();
        SetUpSocket();
    }

    private void SetUpSocket() {
        socket.connect();
        socket.emit("setUpSocket", user.getIdUser());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    switch (item.getItemId()) {
                        case R.id.itemMessage:
                            fragment = new ConversationsFragment();
                            LoadFragment(fragment);
                            break;
                        case R.id.itemSearch:
                            fragment = new SearchFragment();
                            LoadFragment(fragment);
                            break;
                        case R.id.itemAccount:
                            fragment = new AccountFragment();
                            LoadFragment(fragment);
                            break;
                        case R.id.itemGroup:
                            fragment = new GroupFragment();
                            LoadFragment(fragment);
                            break;
                    }
                    return true;
                }
            };

    private void LoadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("User", user);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerMain, fragment, "fragment")
                .commit();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        bottomNav.setOnNavigationItemSelectedListener(navItemSelectedListener);
        LoadFragment(new ConversationsFragment());
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the LeaveGroupDialog.LeaveGroupDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        // User touched the dialog's positive button
        String roomId = RoomAdapter.getRoomIdUserChoice();
        if(roomId != "") {
            ((GroupFragment) getSupportFragmentManager().findFragmentById(R.id.containerMain))
                    .LeaveRoom(roomId);
        }
    }

    @Override
    public void SignOut() {
        if(socket.connected()) {
            socket.disconnect();
            Toast.makeText(this, "Socket disconnected", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
}
