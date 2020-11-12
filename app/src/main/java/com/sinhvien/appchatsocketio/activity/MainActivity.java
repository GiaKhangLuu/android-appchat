package com.sinhvien.appchatsocketio.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.fragment.AccountFragment;
import com.sinhvien.appchatsocketio.fragment.GroupFragment;
import com.sinhvien.appchatsocketio.fragment.ConversationsFragment;
import com.sinhvien.appchatsocketio.fragment.SearchFragment;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private User user;
    private Handler handler;
    private Socket socket;

    private void Init(){
        bottomNav = findViewById(R.id.bottomNav);
        handler = new Handler();
        socket = ChatHelper.getInstace(this).GetSocket();
        socket.connect();
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
                            fragment = new AccountFragment(user);
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
        getSupportFragmentManager().beginTransaction().replace(R.id.containerMain, fragment).commit();
    }

    private void SetUser(JSONObject obj)  {
        String id;
        String displayName;
        try {
            id = obj.getString("_id");
            displayName = obj.getString("displayName");
            user = new User(id, displayName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void FetchUserByUserId() {
        String url = getString(R.string.origin) + "/api/user/getUser";
        HashMap param = new HashMap();
        param.put("userId", "5f83147bd27b95f9d16bc3eb");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SetUser(response);
                        bottomNav.setOnNavigationItemSelectedListener(navItemSelectedListener);
                        LoadFragment(new ConversationsFragment());
                        //Log.i("User", user.getDisplayName());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("User", error.toString());
                    }
                });
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
    }

    /*private Runnable waitToFetchUser = new Runnable() {
        @Override
        public void run() {
            bottomNav.setOnNavigationItemSelectedListener(navItemSelectedListener);
            LoadFragment(new ConversationsFragment(user));
        }
    };*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        FetchUserByUserId();
        //handler.postDelayed(waitToFetchUser, 1000);
    }
}
