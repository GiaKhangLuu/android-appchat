package com.sinhvien.appchatsocketio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

public class SetDisplayNameActivity extends AppCompatActivity {
    private EditText edtDisplayName;
    private Button btnDone;
    private TextView tvSignIn;
    private String accountName, pw, phoneNumber;

    private void Init() {
        edtDisplayName = findViewById(R.id.edtDisplayName);
        btnDone = findViewById(R.id.btnDone);
        tvSignIn = findViewById(R.id.tvSignIn);
        accountName = getIntent().getStringExtra("AccountName");
        pw = getIntent().getStringExtra("Password");
        phoneNumber = getIntent().getStringExtra("PhoneNumber");
        edtDisplayName.setText(accountName);
    }

    private boolean IsValidDisplayName(String displayName) {
        if(displayName.isEmpty()) {
            edtDisplayName.setError("Display name cannot be empty");
            return false;
        }
        return true;
    }

    private void Register() {
        String url = getString(R.string.origin) + "/api/user/createUser";
        String displayName = edtDisplayName.getText().toString().trim();
        if(!IsValidDisplayName(displayName)) return;
        HashMap hashMap = new HashMap();
        hashMap.put("accountName", accountName);
        hashMap.put("password", pw);
        hashMap.put("phoneNumber", phoneNumber);
        hashMap.put("displayName", displayName);
        JSONObject jsonObject = new JSONObject(hashMap);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(SetDisplayNameActivity.this, SignInActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        edtDisplayName.setError("This display name has existed. " +
                                "Please choose another");
                    }
                });
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_display_name);
        Init();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }
}
