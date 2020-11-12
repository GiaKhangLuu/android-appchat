package com.sinhvien.appchatsocketio.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.CreateGroupActivity;
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
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            Room room = new Room();
            room.setIdRoom(object.getString("_id"));
            room.setName(object.getString("name"));
            room.setCreateDate(object.getString("createDate"));
            rooms.add(room);
        }
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
