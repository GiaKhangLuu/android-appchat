package com.sinhvien.appchatsocketio.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.fragment.AccountFragment;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText edtOldPass, edtNewPass, edtConfNewPass;
    Button btnChangePass;
    User user;

    private void SetActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Back");
    }

    private void Init()  {
        toolbar = findViewById(R.id.toolbar);
        edtOldPass = findViewById(R.id.edtOldPass);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtConfNewPass = findViewById(R.id.edtConfirmNewPass);
        btnChangePass = findViewById(R.id.btnChangePass);
        user = (User) getIntent().getSerializableExtra("User");
        SetActionBar();

    }

    private boolean IsOldPassCorrect(String oldPass) {
        if(oldPass.equals(user.getPassword())) return true;
        edtOldPass.setError("Old password is not correct");
        return false;
    }

    private boolean IsNewPassNotEmpty(String newPass) {
        if(!newPass.isEmpty()) return true;
        edtNewPass.setError("New password is not empty");
        return false;
    }

    private boolean IsConfirmNewPassCorrect(String newPass, String confNewPass) {
        if(newPass.equals(confNewPass)) return true;
        edtConfNewPass.setError("Confirm new password is not correct");
        return false;
    }

    private void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification")
                .setMessage("Password changes completely")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ChangePasswordActivity.this,
                                MainActivity.class);
                        intent.putExtra("User", user);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    private void ChangePass() {
        String oldPass = edtOldPass.getText().toString().trim();
        String newPass = edtNewPass.getText().toString().trim();
        String confNewPass = edtConfNewPass.getText().toString().trim();
        // Validate
        if(!IsOldPassCorrect(oldPass) ||
                !IsNewPassNotEmpty(newPass) ||
                !IsConfirmNewPassCorrect(newPass, confNewPass)) return;
        String url = getString(R.string.origin) + "/api/user/updateUser";
        HashMap hashMap = new HashMap();
        hashMap.put("userId", user.getIdUser());
        hashMap.put("accountName", user.getAccountName());
        hashMap.put("password", newPass);
        hashMap.put("displayName", user.getDisplayName());
        hashMap.put("phoneNumber", user.getPhoneNumber());
        JSONObject jsonObject = new JSONObject(hashMap);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            user.setPassword(response.getString("password"));
                            ShowDialog();
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
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Init();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePass();
            }
        });
    }
}
