package com.sinhvien.appchatsocketio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.lang.reflect.Method;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private EditText edtAccountName, edtPassword;
    private Button btnSignIn;
    private CheckBox chbSaveMe;
    private TextView tvRegister;

    private void Init() {
        edtAccountName = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvRegister = findViewById(R.id.tvRegister);
        chbSaveMe = findViewById(R.id.chbSaveMe);
    }

    private View.OnClickListener tvRegisterOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }
    };

    private void Login() {
        String url = getString(R.string.origin) + "/api/user/login";
        HashMap<String, String> params = new HashMap<>();
        params.put("accountName", edtAccountName.getText().toString());
        params.put("password", edtPassword.getText().toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.length() > 0) {
                            try {
                                HandleLoginSuccessfully(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            HandleLoginFailed();
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

    private void HandleLoginSuccessfully(JSONObject response) throws JSONException {
        Intent intent = new Intent(this, MainActivity.class);
        User user = new User();
        user.setIdUser(response.getString("_id"));
        user.setDisplayName(response.getString("displayName"));
        user.setAccountName(response.getString("accountName"));
        user.setPassword(response.getString("password"));
        intent.putExtra("User", user);
        startActivity(intent);
    }

    private void HandleLoginFailed() {
        edtAccountName.setError("Account name or password are wrong");
        edtPassword.setError("Account name or password are wrong");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Init();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
        tvRegister.setOnClickListener(tvRegisterOnClickListener);
    }
}
