package com.sinhvien.appchatsocketio.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.adapter.MessageAdapter;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.Message;
import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MessageActivity extends AppCompatActivity {
    // View
    private Toolbar toolbarTitle;
    private RecyclerView rvMessage;
    private EditText edtMessage;
    private ImageButton btnSend;
    // Data
    private Room room;
    private User user;
    private ArrayList<Message> messages;
    private MessageAdapter adapter;
    private Socket socket;

    private void Init(){
        Mapping();
        SetData();
        SetActionBar();
        CheckSocketStatus();
    }

    private void Mapping() {
        toolbarTitle = findViewById(R.id.toolbarTitle);
        rvMessage = findViewById(R.id.rvMessage);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSendMessage);
    }

    private void SetActionBar() {
        setSupportActionBar(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(room.getName());
    }

    private void SetData() {
        socket = ChatHelper.getInstace(this).GetSocket();
        user = (User) getIntent().getSerializableExtra("User");
        room = (Room) getIntent().getSerializableExtra("Room");
        messages = new ArrayList<>();
        adapter = new MessageAdapter(this, messages, user.getIdUser());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); // Set the rv focus on last ele of list
        rvMessage.setHasFixedSize(true); // Improve performance of rv
        rvMessage.setLayoutManager(linearLayoutManager);
        rvMessage.setAdapter(adapter);
    }

    private void CheckSocketStatus() {
        if(!socket.connected()) {
            Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();
            SetUpSocket();
        }
    }

    private void SetUpSocket() {
        socket.connect();
        socket.emit("setUpSocket", user.getIdUser());
    }

    private void RenderMessage(JSONArray jsonArray) {
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                Message mess = new Message();
                mess.setSenderId(object.getString("senderId"));
                mess.setDisplayName(object.getString("displayName"));
                mess.setTime(object.getString("time"));
                mess.setMessage(object.getString("content"));
                messages.add(mess);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void FetchMessagesInRoom() {
        String url = getString(R.string.origin) + "/api/message/messages";
        HashMap<String, String> params = new HashMap<>();
        params.put("roomId", room.getIdRoom());
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Messages", response.toString());
                        RenderMessage(response);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Messages", error.toString());
                    }
                }
        );
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
    }

    Toolbar.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void SendMessage() {
        String content = edtMessage.getText().toString();
        if(!content.trim().isEmpty()) {
            String senderId = user.getIdUser();
            String roomId = room.getIdRoom();
            Date time = Calendar.getInstance().getTime();
            JSONObject object = SetData(senderId, roomId, content, time);
            CheckSocketStatus();
            socket.emit("user_send_message", object);
            edtMessage.setText("");
        }
    }

    private JSONObject SetData(String senderId, String roomId, String content, Date time) {
        HashMap hashMap = new HashMap();
        hashMap.put("senderId", senderId);
        hashMap.put("roomId", roomId);
        hashMap.put("content", content);
        hashMap.put("time", time);
        JSONObject obj = new JSONObject(hashMap);
        return obj;
    }

    private Emitter.Listener OnNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.i("newmessage", data.toString());
                        AppendNewMessage(data);
                    } catch (Exception ex) {
                        Log.i("newmessage", ex.toString());
                    }
                }
            });
        }
    };

    private void AppendNewMessage(JSONObject data) {
        try {
            String roomId = data.getString("roomId");
            String content = data.getString("content");
            String displayName = data.getString("displayName");
            String senderId = data.getString("senderId");
            String time = data.getString("time");
            if(IsSameRoom(roomId)) {
                messages.add(new Message(senderId, displayName, content, time));
                adapter.notifyItemInserted(messages.size());
                rvMessage.smoothScrollToPosition(messages.size()); // Update rv focus on last item
            }
        } catch (JSONException ex) {
            Log.i("New message", ex.getMessage());
        }
    }

    private boolean IsSameRoom(String idRoomOfMsg) {
        if(room.getIdRoom().equals(idRoomOfMsg)) return true;
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Init();
        FetchMessagesInRoom();
        toolbarTitle.setNavigationOnClickListener(toolBarOnClickListener);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
        socket.on("new_message", OnNewMessage);
    }
}
