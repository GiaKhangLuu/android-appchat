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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.helper.CustomJsonArrayRequest;
import com.sinhvien.appchatsocketio.helper.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtAccountName, edtPassword, edtConfirmPasswond, edtPhone;
    private Button btnNext;
    private TextView tvSignIn;

    private boolean IsFillAll(String accountName, String pw, String phoneNumber) {
        if(accountName.isEmpty()) {
            edtAccountName.setError("Account name is required");
            return false;
        }
        if(pw.isEmpty()) {
            edtPassword.setError("Password is required");
            return false;
        }
        if(phoneNumber.isEmpty())  {
            edtPhone.setError("Phone number is required");
            return false;
        }
        return true;
    }

    private boolean IsSamePassword(String pw, String confPW) {
        if(pw.equals(confPW)) return true;
        edtConfirmPasswond.setError("Confirm password is not as same as password");
        return false;
    }

    private boolean IsValidPhoneNumber(String phoneNumber) {
        String regex = "^[\\d]{10}$";
        if(Pattern.matches(regex, phoneNumber)) return true;
        edtPhone.setError("Phone number is not valid");
        return false;
    }

    private void Register() {
        final String accountName = edtAccountName.getText().toString().trim();
        final String pw = edtPassword.getText().toString().trim();
        String confPW = edtConfirmPasswond.getText().toString().trim();
        final String phoneNumber = edtPhone.getText().toString().trim();
        // Validate
        if(!IsFillAll(accountName, pw, phoneNumber) ||
                !IsSamePassword(pw, confPW) ||
                !IsValidPhoneNumber(phoneNumber)) return;
        String url = getString(R.string.origin) + "/api/user/searchByAccount";
        HashMap hashMap = new HashMap();
        hashMap.put("accountName", accountName);
        JSONObject jsonObject = new JSONObject(hashMap);
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response.length() == 0) {
                            Intent intent = new Intent(RegisterActivity.this,
                                    SetDisplayNameActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("AccountName", accountName);
                            bundle.putString("Password", pw);
                            bundle.putString("PhoneNumber", phoneNumber);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            return;
                        }
                        edtAccountName.setError("This account name has existed. " +
                                "Please choose another");
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

    private void Init() {
        edtAccountName = findViewById(R.id.edtAccountName);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPasswond = findViewById(R.id.edtConfirmPassword);
        edtPhone = findViewById(R.id.edtPhone);
        btnNext = findViewById(R.id.btnNext);
        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private View.OnClickListener tvSignInOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener btnDoneOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Register();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Init();
        tvSignIn.setOnClickListener(tvSignInOnClickListener);
        btnNext.setOnClickListener(btnDoneOnClickListener);
    }
}
