package com.sinhvien.appchatsocketio.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.adapter.SearchedUsersToAddAdapter;
import com.sinhvien.appchatsocketio.adapter.UserSelectionsAdapter;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.Ultilities;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
        user = (User) getIntent().getSerializableExtra("User");
        searchedUsers = new ArrayList<>();
        userSelections = new LinkedList<>();
        searchedUserAdapter = new SearchedUsersToAddAdapter(this, searchedUsers);
        userSelectionAdapter = new UserSelectionsAdapter(this, userSelections);
        SetRecyclerView();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Init();
        imgBtnBack.setOnClickListener(imgBtnBackOnClickListener);
        searchViewUser.setOnQueryTextListener(onQueryTextListener);
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
}
