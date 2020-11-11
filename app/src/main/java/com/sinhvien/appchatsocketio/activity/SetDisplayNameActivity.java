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

public class SetDisplayNameActivity extends AppCompatActivity {
    private EditText edtDisplayName;
    private Button btnDone;
    private TextView tvSignIn;

    private void Init() {
        edtDisplayName = findViewById(R.id.edtDisplayName);
        btnDone = findViewById(R.id.btnDone);
        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private View.OnClickListener tvSignInOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_display_name);
        Init();
        tvSignIn.setOnClickListener(tvSignInOnClickListener);
    }
}
