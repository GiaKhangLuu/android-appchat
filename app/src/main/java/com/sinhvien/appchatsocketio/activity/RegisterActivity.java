package com.sinhvien.appchatsocketio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sinhvien.appchatsocketio.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword, edtConfirmPasswond, edtPhone;
    private Button btnNext;
    private TextView tvSignIn;

    private void Init() {
        edtUsername = findViewById(R.id.edtUsername);
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
            Intent intent = new Intent(getApplicationContext(), SetDisplayNameActivity.class);
            startActivity(intent);
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
