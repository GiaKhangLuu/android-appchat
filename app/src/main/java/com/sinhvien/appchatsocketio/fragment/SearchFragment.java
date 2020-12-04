package com.sinhvien.appchatsocketio.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.MessageActivity;
import com.sinhvien.appchatsocketio.adapter.SearchedUserToChatAdapter;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Socket;

public class SearchFragment extends Fragment {
    private ArrayList<User> searchedUsers;
    private User user;
    private SearchView searchView;
    private SearchedUserToChatAdapter searchedUserAdapter;
    private RecyclerView rvSearchedUser;
    private Socket socket;

    private void SetRecyclerView() {
        rvSearchedUser.setHasFixedSize(true);
        rvSearchedUser.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchedUser.setAdapter(searchedUserAdapter);
    }

    private void Init(View view) {
        socket = ChatHelper.getInstace(getContext()).GetSocket();
        searchView = view.findViewById(R.id.searchView);
        rvSearchedUser = view.findViewById(R.id.rvSearchedUser);
        user = (User) getArguments().getSerializable("User");
        searchedUsers = new ArrayList<>();
        searchedUserAdapter = new SearchedUserToChatAdapter(getContext(), user, searchedUsers);
        CheckSocketStatus();
        SetRecyclerView();
    }

    private void CheckSocketStatus() {
        if(!socket.connected()) {
            Toast.makeText(getContext(), "Connecting", Toast.LENGTH_SHORT).show();
            SetUpSocket();
        } else {
            Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void SetUpSocket() {
        socket.connect();
        socket.emit("setUpSocket", user.getIdUser());
        Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
    }

    private void FetchUsersByUserDisplayName(String displayName) {
        String url = getString(R.string.origin) + "/api/user/searchUsers";
        HashMap<String, String> params = new HashMap<>();
        params.put("displayName", displayName);
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        SetUsers(response);
                        Log.i("Search", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        requestQueue.add(request);
    }

    private void SetUsers(JSONArray jsonArray) {
        searchedUsers.clear();
        try {
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String userId = object.getString("_id");
                if(!userId.equals(user.getIdUser())) {
                    String displayName = object.getString("displayName");
                    searchedUsers.add(new User(userId, displayName));
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        searchedUserAdapter.notifyDataSetChanged();
    }

    SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            FetchUsersByUserDisplayName(query.trim());
            return false;
        }
        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.isEmpty()) {
                searchedUsers.clear();
                searchedUserAdapter.notifyDataSetChanged();
            }
            return false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
        searchView.setOnQueryTextListener(onQueryTextListener);
    }
}

