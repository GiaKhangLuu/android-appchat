package com.sinhvien.appchatsocketio.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.MessageActivity;
import com.sinhvien.appchatsocketio.adapter.ConversationAdapter;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
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

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ConversationsFragment extends Fragment {
    private RecyclerView rvConversation;
    private User user;
    private ArrayList<Conversation> conversations;
    private ConversationAdapter adapter;
    private Socket socket;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void SetRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvConversation.setHasFixedSize(true); // Improve performance of rv when scrolling
        rvConversation.setLayoutManager(layoutManager);
        // Set divider for recycler view
        rvConversation.addItemDecoration(new RecyclerView.ItemDecoration() {
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
        rvConversation.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void Init(View view) {
        socket = ChatHelper.getInstace(getContext()).GetSocket();
        rvConversation = view.findViewById(R.id.rvConversations);
        conversations = new ArrayList<>();
        adapter = new ConversationAdapter(getContext(), conversations, user);
        CheckSocketStatus();
        SetRecyclerView();
        FetchConversations();
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
                adapter.notifyDataSetChanged();
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
                            adapter.notifyItemChanged(conversations.indexOf(conversation));
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

    private void CheckSocketStatus() {
        if(!socket.connected()) {
            Toast.makeText(getContext(), "Connecting", Toast.LENGTH_SHORT).show();
            SetUpSocket();
        } else {
            Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void SetUpSocket() {
        socket.connect();
        socket.emit("setUpSocket", user.getIdUser());
    }

    private Emitter.Listener OnUpdateConversation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) args[0];
                            RemoveConversation(data);
                            InsertNewConversation(data);
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };

    private void RemoveConversation(JSONObject data) {
        try {
            String convId = data.getString("roomId");
            for(int i = 0; i < conversations.size(); i++) {
                if(conversations.get(i).getRoomId().equals(convId)) {
                    conversations.remove(i);
                    adapter.notifyItemRemoved(i);
                    break;
                }
            }
        } catch(JSONException ex) {

        }
    }

    private void InsertNewConversation(JSONObject data) {
        try {
            Conversation cv = new Conversation();
            conversations.add(0, cv);
            cv.setRoomId(data.getString("roomId"));
            // If roomName = "" => set roomName = displayName of your friend
            if(data.getString("name").isEmpty()) {
                FetchMemberDisplayName(cv.getRoomId(), user.getIdUser(), cv);
            } else {
                cv.setName(data.getString("name"));
            }
            cv.setMessage(data.getString("content"));
            cv.setTime(data.getString("time"));
            adapter.notifyItemInserted(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = (User) getArguments().getSerializable("User");
        Init(view);
        socket.on("update_conversation", OnUpdateConversation);
    }
}
