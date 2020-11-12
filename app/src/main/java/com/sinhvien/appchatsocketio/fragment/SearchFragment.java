package com.sinhvien.appchatsocketio.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.MessageQueue;
import android.text.method.SingleLineTransformationMethod;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.MessageActivity;
import com.sinhvien.appchatsocketio.adapter.SearchUserToChatAdapter;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.Conversation;
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

    private void Init(View view) {
        searchView = view.findViewById(R.id.searchView);
        lvShowUser = view.findViewById(R.id.lvShowUser);
        user = (User) getArguments().getSerializable("User");
        searchedUsers = new ArrayList<>();
        adapter = new SearchUserToChatAdapter(getContext(), R.layout.row_search_user_to_chat, searchedUsers);
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

    private void MoveToMessageActivity(String roomId, String searchedUserDisplayName) {
        Conversation conversation = new Conversation();
        conversation.setRoomId(roomId);
        conversation.setName(searchedUserDisplayName);
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.putExtra("User", user);
        intent.putExtra("Conversation", conversation);
        startActivity(intent);
    }

    public void FetchSingleChat(final String searchedUserId, final String searchedUserDisplayName) {
        String url = getString(R.string.origin) + "/api/room/singleChat";
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", user.getIdUser());
        params.put("searchedUserId", searchedUserId);
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Case user and searchedUser used to chat => render old messages
                        if(response.length() != 0) {
                            try {
                                String roomId = response.getJSONObject(0).getString("_id");
                                MoveToMessageActivity(roomId, searchedUserDisplayName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            MoveToMessageActivity(null, searchedUserDisplayName);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue messageQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        messageQueue.add(request);
    }

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
