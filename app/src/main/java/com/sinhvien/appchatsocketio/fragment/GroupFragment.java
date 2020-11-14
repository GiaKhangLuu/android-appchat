package com.sinhvien.appchatsocketio.fragment;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;

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
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupFragment extends Fragment {
    private Button btnCreateRoom;
    private ListView lvRooms;
    private ArrayList<Room> rooms;
    private ArrayAdapter<Room> adapter;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    private void Init(View view) {
        user = (User) getArguments().getSerializable("User");
        btnCreateRoom = view.findViewById(R.id.btnCreateRoom);
        lvRooms = view.findViewById(R.id.lvRooms);
        rooms = new ArrayList<>();
        adapter = new RoomAdapter(getContext(), R.layout.row_room, rooms);
        FetchMultiMembersRoomsOfUser();
        lvRooms.setAdapter(adapter);
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
                            adapter.notifyDataSetChanged();
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

    private void MoveToMessageActivity(Room room) {
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.putExtra("User", user);
        intent.putExtra("Room", room);
        startActivity(intent);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MoveToMessageActivity(adapter.getItem(position));
        }
    };

    public void LeaveRoom(Room room) {
        String url = getString(R.string.origin) + "/api/room/leaveRoom";
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", user.getIdUser());
        params.put("roomId", room.getIdRoom());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Leave successfully", Toast.LENGTH_SHORT).show();
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
        lvRooms.setOnItemClickListener(onItemClickListener);
    }

}
