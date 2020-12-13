package com.sinhvien.appchatsocketio.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.adapter.SearchedUsersToAddAdapter;
import com.sinhvien.appchatsocketio.adapter.UserSelectionsAdapter;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.OnShowNotiListener;
import com.sinhvien.appchatsocketio.helper.Ultilities;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class CreateGroupActivity extends AppCompatActivity
        implements
        SearchedUsersToAddAdapter.SearchedUserListener,
        UserSelectionsAdapter.UserSelectionsListener {
    // View
    private EditText edtGroupName;
    private SearchView searchViewUser;
    private RecyclerView rvSearchedUsers, rvGroupMembers;
    private Button btnDone;
    private ImageButton imgBtnBack;
    // Data
    private User user;
    private ArrayList<User> searchedUsers;
    private LinkedList<User> userSelections;
    private SearchedUsersToAddAdapter searchedUserAdapter;
    private UserSelectionsAdapter userSelectionAdapter;
    private Socket socket;
    private Emitter.Listener onShowNotiListener;

    private void SetRecyclerView() {
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        rvSearchedUsers.setHasFixedSize(true);
        rvSearchedUsers.setLayoutManager(layoutManager1);
        rvSearchedUsers.setAdapter(searchedUserAdapter);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        rvGroupMembers.setHasFixedSize(true);
        rvGroupMembers.setLayoutManager(layoutManager2);
        rvGroupMembers.setAdapter(userSelectionAdapter);
    }

    private void Init() {
        // Init view
        edtGroupName = findViewById(R.id.edtGroupName);
        searchViewUser = findViewById(R.id.searchViewUser);
        rvSearchedUsers = findViewById(R.id.rvSearchedUserToAdd);
        rvGroupMembers = findViewById(R.id.rvGroupMember);
        btnDone = findViewById(R.id.btnDone);
        imgBtnBack = findViewById(R.id.imgBtnBack);
        // Init data
        socket = ChatHelper.getInstace(this).GetSocket();
        user = (User) getIntent().getSerializableExtra("User");
        searchedUsers = new ArrayList<>();
        userSelections = new LinkedList<>();
        searchedUserAdapter = new SearchedUsersToAddAdapter(this, searchedUsers);
        userSelectionAdapter = new UserSelectionsAdapter(this, userSelections);
        SetRecyclerView();
        CheckSocketStatus();
        onShowNotiListener = new OnShowNotiListener(user, this);
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

    View.OnClickListener imgBtnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void SetSearchedUsers(JSONArray jsonArray) {
        searchedUsers.clear();
        try {
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String userId = object.getString("_id");
                if(!user.getIdUser().equals(userId)) {
                    String displayName = object.getString("displayName");
                    searchedUsers.add(new User(userId, displayName));
                }
            }
        } catch(Exception err) {
            err.printStackTrace();
        }
    }

    private void FetchUserByDisplayName(final String displayName) {
        String url = getString(R.string.origin) + "/api/user/searchUsers";
        HashMap<String, String> param = new HashMap<>();
        param.put("displayName", displayName);
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.POST,
                url,
                new JSONObject(param),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        searchedUsers.clear();
                        SetSearchedUsers(response);
                        searchedUserAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
    }

    SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            FetchUserByDisplayName(query.trim());
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.isEmpty()) {
               searchedUsers.clear();
               searchedUserAdapter.notifyDataSetChanged();
            }
            return true;
        }
    };

    private boolean IsGroupNameValid() {
        String groupName = edtGroupName.getText().toString().trim();
        if(!groupName.isEmpty()) return true;
        return false;
    }

    private boolean IsEnoughMembers() {
        // qtyOfMembers to create group is more than 2 (user and userSelections)
        int qtyOfMembers = userSelections.size();
        if(qtyOfMembers > 1) return true;
        return false;
    }

    private boolean IsValid() {
        if(!IsGroupNameValid()) {
            edtGroupName.setError("You must name your group");
            return false;
        }
        if(!IsEnoughMembers()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your group must more than 2 members")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.create().show();
            return false;
        }
        return true;
    }

    private String[] GetMemberIds() {
        String[] memberIds = new String[userSelections.size() + 1];
        for(int i = 0; i < userSelections.size(); i++) {
            memberIds[i] = userSelections.get(i).getIdUser();
        }
        memberIds[memberIds.length - 1] = user.getIdUser();
        return  memberIds;
    }

    private void EmitJoinNewRoom(String[] members, String roomId) {
        HashMap hashMap = new HashMap();
        hashMap.put("members", members);
        hashMap.put("roomId", roomId);
        JSONObject newRoom = new JSONObject(hashMap);
        socket.emit(ChatHelper.EMIT_CREATE_NEW_ROOM, newRoom);
    }

    public void EmitNotifyNewRoom(String roomId) {
        String content = user.getDisplayName() + " create the group";
        Date time = Calendar.getInstance().getTime();
        HashMap hashMap = new HashMap();
        hashMap.put("roomId", roomId);
        hashMap.put("content", content);
        hashMap.put("time", time);
        JSONObject message = new JSONObject(hashMap);
        socket.emit(ChatHelper.EMIT_NOTIFY_NEW_ROOM, message);
    }

    private void CreateGroup() {
        CheckSocketStatus();
        String url = getString(R.string.origin) + "/api/room/createRoom";
        final String[] members = GetMemberIds();
        HashMap hashMap = new HashMap();
        hashMap.put("name", edtGroupName.getText().toString().trim());
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
                            String roomId = response.getString("_id");
                            EmitJoinNewRoom(members, roomId);
                            EmitNotifyNewRoom(roomId);
                            BackToMainActivity();
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

    View.OnClickListener btnDoneOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(IsValid()) {
                CreateGroup();
            }
        }
    };

    private void BackToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }

    @Override
    public void AddToUserSelections(User user) {
        Ultilities.AddToUserSelections(userSelections, user);
        userSelectionAdapter.notifyItemInserted(userSelections.size());
    }

    @Override
    public void RemoveFromUserSelections(User user) {
        int index = Ultilities.RemoveFromUserSelections(userSelections, user);
        if(index > -1) {
            userSelectionAdapter.notifyItemRemoved(index);
        }
    }

    @Override
    public int GetIndexOfUserInUserSelections(User user) {
        return Ultilities.GetPositionOfUserInUserSelection(userSelections, user);
    }

    @Override
    public void SetUncheckFromSearchedUser(User user) {
        int index = Ultilities.GetPositionOfUserInSearchedUsers(searchedUsers, user);
        if(index > -1) {
            ((CheckBox)rvSearchedUsers.getChildAt(index).findViewById(R.id.chbChoose)).
                    setChecked(false);
            return;
        }
        RemoveFromUserSelections(user);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Init();
        imgBtnBack.setOnClickListener(imgBtnBackOnClickListener);
        searchViewUser.setOnQueryTextListener(onQueryTextListener);
        btnDone.setOnClickListener(btnDoneOnClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        socket.off(ChatHelper.ON_SHOW_NOTI_IN_MSG_ACTIVITY);
        socket.off(ChatHelper.ON_NEW_MESSAGE);
        socket.off(ChatHelper.ON_UPDATE_CONVERSATION);
        socket.on(ChatHelper.ON_SHOW_NOTIFICATION, onShowNotiListener);
    }
}
