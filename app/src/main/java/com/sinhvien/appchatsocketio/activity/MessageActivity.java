package com.sinhvien.appchatsocketio.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.adapter.MessageAdapter;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.NotificationHelper;
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
import java.util.LinkedList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MessageActivity extends AppCompatActivity {
    // View
    private Toolbar toolbarTitle;
    private RecyclerView rvMessage;
    private EditText edtMessage;
    private ImageButton btnSend;
    private LinearLayout layoutTyping;
    private TextView tvUsersTyping;
    // Data
    private Room room;
    private User user;
    private ArrayList<Message> messages;
    private LinkedList<String> usersTypings;
    // searchedUserId is used to create single if the user and searched user havent chatted yet
    private String searchedUserId;
    private MessageAdapter adapter;
    private Socket socket;
    // Listener
    private ShowNotiInMessageActivityListener showNotiListener;
    private TextChangedListener textChangedListener;
    private TypingListener typingListener;
    private StopTypingListener stopTypingListener;
    private NewMessageListener newMessageListener;

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
        tvUsersTyping = findViewById(R.id.tvUsersTyping);
        layoutTyping = findViewById(R.id.layoutTyping);
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
        searchedUserId = getIntent().getStringExtra("IdChosenUser");
        messages = new ArrayList<>();
        adapter = new MessageAdapter(this, messages, user.getIdUser());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); // Set the rv focus on last ele of list
        rvMessage.setHasFixedSize(true); // Improve performance of rv
        rvMessage.setLayoutManager(linearLayoutManager);
        rvMessage.setAdapter(adapter);
        usersTypings = new LinkedList<>();
        showNotiListener = new ShowNotiInMessageActivityListener();
        textChangedListener = new TextChangedListener();
        typingListener = new TypingListener();
        stopTypingListener = new StopTypingListener();
        newMessageListener = new NewMessageListener();
    }

    private void CheckSocketStatus() {
        if(!socket.connected()) {
            Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();
            SetUpSocket();
        }
    }

    private void SetUpSocket() {
        socket.connect();
        socket.emit(ChatHelper.EMIT_SETUP_SOCKET, user.getIdUser());
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

    private void RenderMessage(JSONArray jsonArray) {
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                Message mess = new Message();
                // Set senderID = "" to render the message by view type
                if(IsNotiMessage(object)) {
                    mess.setSenderId("");
                    mess.setDisplayName("");
                }
                else {
                    mess.setSenderId(object.getString("senderId"));
                    mess.setDisplayName(object.getString("displayName"));
                }
                mess.setTime(object.getString("time"));
                mess.setMessage(object.getString("content"));
                messages.add(mess);
                Log.i("messages", mess.getMessage());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Check whether the message is from server notification
    private boolean IsNotiMessage(JSONObject jsonObject) {
        if(jsonObject.isNull("senderId")) return true;
        return false;
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
            CheckSocketStatus();
            String senderId = user.getIdUser();
            String roomId = room.getIdRoom();
            Date time = Calendar.getInstance().getTime();
            JSONObject message = SetData(senderId, roomId, content, time);
            // Occure when user and searched user havent chatted yet => create room
            if(room.getIdRoom() == null && searchedUserId != null)  {
                CreateRoomThenSendMessage(message);
            }
            // Occure when user and searched have chatted or this is multiple members room
            else {
                EmitSendMessage(message);
            }
        }
    }

    private void EmitJoinNewRoom(String[] members, String roomId) {
        HashMap hashMap = new HashMap();
        hashMap.put("members", members);
        hashMap.put("roomId", roomId);
        JSONObject newRoom = new JSONObject(hashMap);
        socket.emit(ChatHelper.EMIT_CREATE_NEW_ROOM, newRoom);
    }

    private void EmitSendMessage(JSONObject message) {
        socket.emit(ChatHelper.EMIT_SEND_MESSAGE, message);
        edtMessage.setText("");
    }

    private void CreateRoomThenSendMessage(final JSONObject message) {
        String url = getString(R.string.origin) + "/api/room/createRoom";
        final String[] members = new String[] { user.getIdUser(), searchedUserId };
        HashMap hashMap = new HashMap();
        hashMap.put("name", "");
        hashMap.put("members", members);
        JSONObject newRoom = new JSONObject(hashMap);
        // Insert new room to db
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                newRoom,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Update roomId and searched UserId after create room
                            room.setIdRoom(response.getString("_id"));
                            searchedUserId = null;
                            // Create new room on socket server
                            EmitJoinNewRoom(members, room.getIdRoom());
                            message.put("roomId", room.getIdRoom());
                            EmitSendMessage(message);
                        } catch (Exception ex) {
                            Log.i("exception", ex.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("exception", error.toString());
                    }
                }
        );
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
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

    private void AppendNewMessage(JSONObject data) {
        try {
            String roomId = data.getString("roomId");
            String content = data.getString("content");
            String displayName = "";
            String senderId = "";
            if(!IsNotiMessage(data)) {
                displayName = data.getString("displayName");
                senderId = data.getString("senderId");
            }
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
        edtMessage.addTextChangedListener(textChangedListener);
        // Off event ON_SHOW_NOTIFICATION to reset the old and update new
        socket.off(ChatHelper.ON_SHOW_NOTIFICATION);
        socket.off(ChatHelper.ON_UPDATE_CONVERSATION);
        socket.on(ChatHelper.ON_SHOW_NOTI_IN_MSG_ACTIVITY, showNotiListener);
        socket.on(ChatHelper.ON_NEW_MESSAGE, newMessageListener);
        socket.on(ChatHelper.ON_TYPING, typingListener);
        socket.on(ChatHelper.ON_STOP_TYPING, stopTypingListener);
    }

    private class NewMessageListener implements Emitter.Listener {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        AppendNewMessage(data);
                    } catch (Exception ex) {
                        Log.i("newmessage", ex.toString());
                    }
                }
            });
        }
    }

    private class ShowNotiInMessageActivityListener implements Emitter.Listener {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                String roomName = data.getString("name");
                String content = data.getString("content");
                String roomId = data.getString("roomId");
                if(roomId.equals(room.getIdRoom())) return;
                Room room = new Room();
                room.setIdRoom(roomId);
                room.setName(roomName);
                NotificationHelper notificationHelper = new NotificationHelper(
                        MessageActivity.this,
                        user,
                        room
                );
                notificationHelper.SendNoti(content);
            } catch (JSONException ex) {
                Toast.makeText(MessageActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class TextChangedListener implements TextWatcher {
        boolean typingFlag = false;

        private void HandleTyping() {
            if(!typingFlag) {
                JSONObject data = SetData();
                socket.emit(ChatHelper.EMIT_TYPING, data);
                typingFlag = true;
            }
        }

        private void HandleStopTyping() {
            JSONObject data = SetData();
            socket.emit(ChatHelper.EMIT_STOP_TYPING, data);
            typingFlag = false;
        }

        private JSONObject SetData() {
            HashMap hashMap = new HashMap();
            hashMap.put("roomId", room.getIdRoom());
            hashMap.put("userName", user.getDisplayName());
            return new JSONObject(hashMap);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!s.toString().isEmpty()) {
                HandleTyping();
                return;
            }
            HandleStopTyping();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class TypingListener implements Emitter.Listener {

        private String SetTypingText() {
            StringBuffer stringBuffer = new StringBuffer();
            String typingText;
            for(String user : usersTypings) {
                stringBuffer.append(user + ", ");
            }
            typingText = stringBuffer.substring(0, stringBuffer.length() - 2);
            if(usersTypings.size() == 1) {
                typingText += " is typing";
            } else {
                typingText += " are typing";
            }
            return typingText;
        }

        private void HandleUsersTyping(String userName) {
            usersTypings.add(userName);
            layoutTyping.setVisibility(View.VISIBLE);
            String typingText = SetTypingText();
            tvUsersTyping.setText(typingText);
        }

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String userName = data.getString("userName");
                        String roomId = data.getString("roomId");
                        if(roomId.equals(room.getIdRoom())) {
                            HandleUsersTyping(userName);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private class StopTypingListener implements Emitter.Listener {

        private String SetTypingTest() {
            StringBuffer stringBuffer = new StringBuffer();
            String typingTest = "";
            for(String user : usersTypings) {
                stringBuffer.append(user + ", ");
            }
            typingTest = stringBuffer.substring(0, stringBuffer.length() - 2);
            if(usersTypings.size() == 1) {
                typingTest += " is typing";
            } else {
                typingTest += " are typing";
            }
            return typingTest;
        }

        private void RemoveUserFromUserTypingList(String userName) {
            if(usersTypings.indexOf(userName) >= 0) {
                usersTypings.remove(userName);
            }
        }

        private void HandleStopTyping(String userName) {
            RemoveUserFromUserTypingList(userName);
            if(usersTypings.size() == 0) {
                OffLayoutTyping();
                return;
            }
            String typingTest = SetTypingTest();
            tvUsersTyping.setText(typingTest);
        }

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String userName = data.getString("userName");
                        String roomId = data.getString("roomId");
                        if(roomId.equals(room.getIdRoom())) {
                            HandleStopTyping(userName);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void OffLayoutTyping() {
        tvUsersTyping.setText("");
        layoutTyping.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(this, "new intent", Toast.LENGTH_SHORT).show();
        edtMessage.setText("");
        user = (User) intent.getSerializableExtra("User");
        room = (Room) intent.getSerializableExtra("Room");
        messages.clear();
        adapter.notifyDataSetChanged();
        FetchMessagesInRoom();
        toolbarTitle.setTitle(room.getName());
        usersTypings = new LinkedList<>();
        OffLayoutTyping();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // set text to "" to call event text change
        edtMessage.setText("");
    }
}
