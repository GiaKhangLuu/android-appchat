package com.sinhvien.appchatsocketio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.adapter.MessageAdapter;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.Conversation;
import com.sinhvien.appchatsocketio.model.Message;
import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {
    // View
    private ListView lvMain;
    private EditText edtMessage;
    private Button btnSend;
    private Toolbar toolbarTitle;
    // Data
    private Room room;
    private User user;
    private ArrayList<Message> messages;
    private ArrayAdapter<Message> adapter;

    private void Init() throws JSONException {
        // Set view
        lvMain = findViewById(R.id.lvMain);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        setSupportActionBar(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Set data
        user = (User) getIntent().getSerializableExtra("User");
        room = (Room) getIntent().getSerializableExtra("Room");
        messages = new ArrayList<>();
        adapter = new MessageAdapter(getApplicationContext(), R.layout.line_message, messages);
        FetchMessagesInRoom();
        //FetchRoomName();
        lvMain.setAdapter(adapter);
        getSupportActionBar().setTitle(room.getName());
    }

    /*private void FetchRoomName() throws JSONException {
        JSONObject param = new JSONObject();
        param.put("roomId", roomId);
        String url = getString(R.string.origin) + "/getRoomName";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            toolbarTitle.setTitle(response.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }
        );
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
    }*/

    private void SetMessages(JSONArray jsonArray) {
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
                        SetMessages(response);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        try {
            Init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        toolbarTitle.setNavigationOnClickListener(toolBarOnClickListener);
    }
}
