package com.sinhvien.appchatsocketio.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.adapter.SearchUserToChatAdapter;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private ListView lvShowUser;
    private ArrayList<User> searchedUsers;
    private User user;
    private ArrayAdapter adapter;
    private Socket socket;

    public SearchFragment(User user) {
        this.user = user;
    }

    private void Init(View view) {
        searchedUsers = new ArrayList<>();
        adapter = new SearchUserToChatAdapter(getContext(), R.layout.row_search_user_to_chat, searchedUsers);
        searchView = view.findViewById(R.id.searchView);
        lvShowUser = view.findViewById(R.id.lvShowUser);
        lvShowUser.setAdapter(adapter);
        socket = ChatHelper.getInstace(getContext()).GetSocket();
        if(socket.connected()) {
            Toast.makeText(getContext(), "connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "disconnected", Toast.LENGTH_SHORT).show();
            socket.connect();
        }
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
                String displayName = object.getString("displayName");
                searchedUsers.add(new User(userId, displayName));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        adapter.notifyDataSetChanged();
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
                adapter.notifyDataSetChanged();
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
