package com.sinhvien.appchatsocketio.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.adapter.SearchedUserAddToGroupAdapter;
import com.sinhvien.appchatsocketio.adapter.SelectedMemberAdapter;
import com.sinhvien.appchatsocketio.helper.Ultilities;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class CreateGroupActivity extends AppCompatActivity {
    // View
    private EditText edtGroupName;
    private SearchView searchViewUser;
    private ListView lvSearchedUsers, lvGroupMembers;
    private Button btnDone;
    private ImageButton imgBtnBack;
    // Data
    private User user;
    private ArrayList<User> searchedUsers;
    private LinkedList<User> userSelections;
    private ArrayAdapter searchedUserAdapter, userSelectionAdapter;

    private void Init() {
        // Init view
        edtGroupName = findViewById(R.id.edtGroupName);
        searchViewUser = findViewById(R.id.searchViewUser);
        lvSearchedUsers = findViewById(R.id.lvSearchUser);
        lvGroupMembers = findViewById(R.id.lvGroupMembers);
        btnDone = findViewById(R.id.btnDone);
        imgBtnBack = findViewById(R.id.imgBtnBack);
        // Init data
        searchedUsers = Ultilities.GetSearchedUser();
        userSelections = Ultilities.GetUserSelection();
        searchedUsers.clear();
        userSelections.clear();
        searchedUserAdapter = new SearchedUserAddToGroupAdapter(this, R.layout.row_search_user_to_add, searchedUsers);
        userSelectionAdapter = new SelectedMemberAdapter(this, R.layout.row_delete_user, userSelections);
        lvSearchedUsers.setAdapter(searchedUserAdapter);
        lvGroupMembers.setAdapter(userSelectionAdapter);
    }

    View.OnClickListener imgBtnBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void FetchUserByDisplayName(final String displayName) {
        String url = getString(R.string.origin) + "/searchUser";
        HashMap<String, String> param = new HashMap<>();
        param.put("displayName", displayName);
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.POST,
                url,
                new JSONObject(param),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Ultilities.SetSearchedUser(response);
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

    public void AddUserToUserSelection(User user) {
        Ultilities.AddToUserSelections(user);
        userSelectionAdapter.notifyDataSetChanged();
    }

    public void RemoveUserFromUserSelection(User user) {
        Ultilities.RemoveFromUserSelections(user);
        userSelectionAdapter.notifyDataSetChanged();
    }

    public int IndexOfUserInUserSelection(User user) {
        return Ultilities.GetPositionOfUserInUserSelection(user);
    }

    public void SetUncheckFromSearchedUser(User user) {
        int index = Ultilities.GetPositionOfUserInSearchedUsers(user);
        if(index > -1) {
            ((CheckBox)lvSearchedUsers.getChildAt(index).findViewById(R.id.chbChoose)).setChecked(false);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Init();
        user = (User) getIntent().getSerializableExtra("User");
        imgBtnBack.setOnClickListener(imgBtnBackOnClickListener);
        searchViewUser.setOnQueryTextListener(onQueryTextListener);
    }
}
