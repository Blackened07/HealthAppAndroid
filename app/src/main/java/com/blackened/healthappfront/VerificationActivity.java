package com.blackened.healthappfront;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.blackened.healthappfront.auth.VerificationRequestDTO;
import com.google.gson.Gson;

public class VerificationActivity extends BaseActivity{
    private EditText etCode;
    private Button btnVerify;
    private String userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        etCode = findViewById(R.id.et_code);
        btnVerify = findViewById(R.id.btn_verify);

        userEmail = getIntent().getStringExtra("userEmail");

        btnVerify.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            if (code.length() < 6) {
                etCode.setError("Введите 6-значный код");
                return;
            }
            sendVerificationRequest(userEmail, code);
        });
    }

    private void sendVerificationRequest(String email, String code) {
        String endpoint = "auth/verify";

        VerificationRequestDTO dto = new VerificationRequestDTO(email, code);

        ApiClient.post(endpoint, sessionManager.getToken(), dto, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                if (response.contains("critical")) {

                    ErrorResponse dto = gson.fromJson(response, ErrorResponse.class);

                    Toast.makeText(VerificationActivity.this, dto.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    String dto = gson.fromJson(response, String.class);
                    Toast.makeText(VerificationActivity.this, dto, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onError(String error) {
                Toast.makeText(VerificationActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }


}
