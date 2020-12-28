package com.sinhvien.appchatsocketio.adapter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.MessageActivity;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchedUserToChatAdapter extends
        RecyclerView.Adapter<SearchedUserToChatAdapter.ViewHolder> {
    Context context;
    User user;
    ArrayList<User> searchedUsers;

    public SearchedUserToChatAdapter(Context context, User user, ArrayList<User> searchedUsers) {
        this.context = context;
        this.user = user;
        this.searchedUsers = searchedUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.row_search_user_to_chat, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedUserToChatAdapter.ViewHolder holder, final int position) {
        final User searchUser = searchedUsers.get(position);
        holder.tvUserDisplayName.setText(searchUser.getDisplayName());
        holder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User chosenOne = searchedUsers.get(position);
                FetchSingleChat(chosenOne.getIdUser(), chosenOne.getDisplayName());
            }
        });

    }

    private void MoveToMessageActivity(
            String roomId,
            String searchedUserDisplayName,
            String searchUserId) {
        Room room = new Room();
        room.setIdRoom(roomId);
        room.setName(searchedUserDisplayName);
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra("User", user);
        intent.putExtra("Room", room);
        intent.putExtra("IdChosenUser", searchUserId);
        context.startActivity(intent);
    }

    public void FetchSingleChat(final String searchedUserId, final String searchedUserDisplayName) {
        String url = context.getString(R.string.origin) + "/api/room/singleChat";
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", user.getIdUser());
        params.put("searchedUserId", searchedUserId);
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(JSONArray response) {
                        // Case user and searchedUser used to chat => render old messages
                        if(response.length() != 0) {
                            try {
                                String roomId = response.getJSONObject(0).getString("_id");
                                MoveToMessageActivity(roomId, searchedUserDisplayName, searchedUserId);
                                RemoveNoti(roomId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else { // Case user and searchedUser haven't chatted yet
                            MoveToMessageActivity(null, searchedUserDisplayName, searchedUserId);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue messageQueue = VolleySingleton.getInstance(context).getRequestQueue();
        messageQueue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void RemoveNoti(String roomID) {
        int roomIdHashCode = roomID.hashCode();
        context.getSystemService(NotificationManager.class).cancel(roomIdHashCode);
    }

    @Override
    public int getItemCount() {
        return searchedUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserDisplayName;
        private  Button btnChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserDisplayName = itemView.findViewById(R.id.tvDisplayName);
            btnChat = itemView.findViewById(R.id.btnSendMessage);
        }
    }
}

