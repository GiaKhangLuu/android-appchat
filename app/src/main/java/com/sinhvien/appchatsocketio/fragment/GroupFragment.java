package com.sinhvien.appchatsocketio.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.CreateGroupActivity;
import com.sinhvien.appchatsocketio.activity.MessageActivity;
import com.sinhvien.appchatsocketio.adapter.RoomAdapter;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.LeaveGroupDialog;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;

public class GroupFragment extends Fragment {
    private Button btnCreateRoom;
    private RecyclerView rvRooms;
    private ArrayList<Room> rooms;
    private RoomAdapter roomAdapter;
    private User user;
    private Socket socket;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    private void SetRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvRooms.setLayoutManager(layoutManager);
        rvRooms.setAdapter(roomAdapter);
        rvRooms.addItemDecoration(new RecyclerView.ItemDecoration() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
                Drawable divider = getContext().getDrawable(R.drawable.divider);
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    int top = child.getBottom() + params.bottomMargin;
                    int bottom = top + divider.getIntrinsicHeight();
                    divider.setBounds(left, top, right, bottom);
                    divider.draw(c);
                }
            }
        });
    }

    private void Init(View view) {
        socket = ChatHelper.getInstace(getContext()).GetSocket();
        user = (User) getArguments().getSerializable("User");
        btnCreateRoom = view.findViewById(R.id.btnCreateRoom);
        rvRooms = view.findViewById(R.id.rvGroup);
        rooms = new ArrayList<>();
        roomAdapter = new RoomAdapter(getContext(), rooms, user);
        SetRecyclerView();
        FetchMultiMembersRoomsOfUser();
    }

    private void CheckSocketStatus() {
        if(!socket.connected()) {
            Toast.makeText(getContext(), "Connecting", Toast.LENGTH_SHORT).show();
            SetUpSocket();
        }
    }

    private void SetUpSocket() {
        socket.connect();
        socket.emit("setUpSocket", user.getIdUser());
        Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
    }

    private void FetchMultiMembersRoomsOfUser() {
        String url = getString(R.string.origin) + "/api/room/multiMembersRooms";
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", user.getIdUser());
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.POST,
                url,
                new JSONObject(param),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            SetRooms(response);
                            roomAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        requestQueue.add(request);
    }

    private void SetRooms(JSONArray jsonArray) throws JSONException {
        rooms.clear();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            Room room = new Room();
            room.setIdRoom(object.getString("_id"));
            room.setName(object.getString("name"));
            room.setCreateDate(object.getString("createDate"));
            rooms.add(room);
        }
    }

    public void LeaveRoom(final String roomId) {
        String url = getString(R.string.origin) + "/api/room/leaveRoom";
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", user.getIdUser());
        params.put("roomId", roomId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Leave successfully", Toast.LENGTH_SHORT).show();
                        EmitLeaveRoom(roomId);
                        FetchMultiMembersRoomsOfUser();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Leave", error.toString());
                    }
                });
        RequestQueue requestQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        requestQueue.add(request);
    }

    public void EmitLeaveRoom(String roomId) {
        CheckSocketStatus();
        Date time = Calendar.getInstance().getTime();
        HashMap hashMap = new HashMap();
        hashMap.put("roomId", roomId);
        hashMap.put("memberId", user.getIdUser());
        hashMap.put("time", time);
        JSONObject data = new JSONObject(hashMap);
        socket.emit("leave_room", data);
    }

    View.OnClickListener btnCreateOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), CreateGroupActivity.class);
            intent.putExtra("User", user);
            startActivity(intent);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
        btnCreateRoom.setOnClickListener(btnCreateOnClickListener);
    }
}
