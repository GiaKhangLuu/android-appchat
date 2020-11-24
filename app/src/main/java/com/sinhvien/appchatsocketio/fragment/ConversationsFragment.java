package com.sinhvien.appchatsocketio.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.MessageActivity;
import com.sinhvien.appchatsocketio.adapter.ConversationAdapter;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.Conversation;
import com.sinhvien.appchatsocketio.model.Message;
import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConversationsFragment extends Fragment {
    private ListView lvMain;
    private User user;
    private ArrayList<Conversation> conversations;
    private ArrayAdapter<Conversation>  adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    private void Init(View view) {
        lvMain = view.findViewById(R.id.lvMessages);
        conversations = new ArrayList<>();
        adapter = new ConversationAdapter(getContext(), R.layout.item_conversation, conversations);
        lvMain.setAdapter(adapter);
        lvMain.setVisibility(View.INVISIBLE);
        FetchConversations();
        // Wait to load all conversation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lvMain.setVisibility(View.VISIBLE);
            }
        }, 300);
    }

    private void FetchConversations() {
        String url = getString(R.string.origin) + "/api/message/conversations";
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", user.getIdUser());
        CustomJsonArrayRequest req = new CustomJsonArrayRequest(Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Conversation", response.toString());
                        SetConversations(response);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Conversation", error.toString());
                    }
                });
        VolleySingleton.getInstance(getContext()).getRequestQueue().add(req);
    }

    private void SetConversations(JSONArray arr) {
        for(int i = 0; i < arr.length(); i++) {
            try {
                JSONObject obj = arr.getJSONObject(i);
                Conversation cv = new Conversation();
                conversations.add(cv);
                cv.setRoomId(obj.getString("roomId"));
                // If roomName = "" => set roomName = displayName of your friend
                if(obj.getString("name").isEmpty()) {
                    FetchMemberDisplayName(cv.getRoomId(), user.getIdUser(), cv);
                } else {
                    cv.setName(obj.getString("name"));
                }
                cv.setMessage(obj.getString("content"));
                cv.setTime(obj.getString("time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // In case single rooms, must fetch partner display name to set name of the conversation
    private void FetchMemberDisplayName(String roomId, String userId, final Conversation conversation) {
        String url = getString(R.string.origin) + "/api/room/memberDisplayName";
        HashMap<String, String> params = new HashMap<>();
        params.put("roomId", roomId);
        params.put("userId", userId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            conversation.setName(response.getString("displayName"));
                            adapter.notifyDataSetChanged();
                            Log.i("Displayname", response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Conversation conversation = adapter.getItem(position);
            Room room = new Room();
            room.setName(conversation.getName());
            room.setIdRoom(conversation.getRoomId());
            Intent intent = new Intent(getContext(), MessageActivity.class);
            intent.putExtra("User", user);
            intent.putExtra("Room", room);
            startActivity(intent);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = (User) getArguments().getSerializable("User");
        Init(view);
        lvMain.setOnItemClickListener(onItemClickListener);
    }
}
