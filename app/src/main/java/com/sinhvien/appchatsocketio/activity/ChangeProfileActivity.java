package com.sinhvien.appchatsocketio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ChangeProfileActivity extends AppCompatActivity {
    User user;
    EditText edtDisplayName, edtPhone, edtAccountName;
    Button btnDone;
    Toolbar toolbar;

    private void SetActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Back");
    }

    private void Init() {
        user = (User) getIntent().getSerializableExtra("User");
        edtDisplayName = findViewById(R.id.edtDisplayName);
        edtPhone = findViewById(R.id.edtPhoneNumber);
        btnDone = findViewById(R.id.btnDone);
        edtAccountName = findViewById(R.id.edtAccountName);
        toolbar = findViewById(R.id.toolbar);
        SetActionBar();
        SetData();
    }

    private void SetData() {
        edtAccountName.setText(user.getAccountName());
        edtDisplayName.setText(user.getDisplayName());
        edtPhone.setText(user.getPhoneNumber());
    }

    private boolean IsFillAll(String displayName, String phoneNumber) {
        if(displayName.isEmpty()) {
            edtDisplayName.setError("Display name cannot be empty");
            return false;
        }
        if(phoneNumber.isEmpty()) {
            edtPhone.setError("Phone number cannot be empty");
            return false;
        }
        return true;
    }

    private boolean IsPhoneNumberValid(String phoneNumber) {
        String regex = "^[\\d]{10}$";
        if(Pattern.matches(regex, phoneNumber)) return true;
        edtPhone.setError("Phone number is not valid");
        return false;
    }

    private void UpdateUser() {
        String displayName = edtDisplayName.getText().toString().trim();
        String phoneNumber = edtPhone.getText().toString().trim();
        if(!IsFillAll(displayName, phoneNumber) || !IsPhoneNumberValid(phoneNumber)) return;
        String url = getString(R.string.origin) + "/api/user/updateUser";
        HashMap hashMap = new HashMap();
        hashMap.put("userId", user.getIdUser());
        hashMap.put("accountName", user.getAccountName());
        hashMap.put("password", user.getPassword());
        hashMap.put("displayName", displayName);
        hashMap.put("phoneNumber", phoneNumber);
        JSONObject jsonObject = new JSONObject(hashMap);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            user.setIdUser(response.getString("_id"));
                            user.setAccountName(response.getString("accountName"));
                            user.setPhoneNumber(response.getString("phoneNumber"));
                            user.setPassword(response.getString("password"));
                            user.setDisplayName(response.getString("displayName"));
                            Intent intent = new Intent(
                                    ChangeProfileActivity.this,
                                    MainActivity.class);
                            intent.putExtra("User", user);
                            startActivity(intent);
                        } catch (JSONException ex) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.toString().indexOf("displayName") > 0) {
                            edtDisplayName.setError("This display name has existed. Please " +
                                    "choose another");
                        }
                    }
                });
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        Init();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUser();
            }
        });
    }
}
