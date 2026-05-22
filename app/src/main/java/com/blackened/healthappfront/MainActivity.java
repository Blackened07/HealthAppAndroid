package com.blackened.healthappfront;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.blackened.healthappfront.auth.AuthResponse;
import com.blackened.healthappfront.auth.LoginRequest;
import com.blackened.healthappfront.auth.RegisterAccountRequest;
import com.blackened.healthappfront.auth.RegisterAccountResponseDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private EditText etFirstName;
    private Spinner spinnerSystemRole;
    private Button registerButton;
    private Button loginButton;
    private SharedPreferences preferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        etFirstName = findViewById(R.id.editTextFirstName);
        spinnerSystemRole = findViewById(R.id.spinnerSystemRole);
        registerButton = findViewById(R.id.buttonRegistration);
        loginButton = findViewById(R.id.buttonLogin);

        Map<String, String> roleMap = new HashMap<>();
        roleMap.put("Пользователь", "USER");
        roleMap.put("Доктор", "DOCTOR");

        registerButton.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String firstname = etFirstName.getText().toString().trim();
            String selectedRole = spinnerSystemRole.getSelectedItem().toString();

            String systemRole = roleMap.get(selectedRole);

            if (email.isEmpty() || password.isEmpty() || firstname.isEmpty()) {

                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Некорректно введён email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Пароль должен быть не меньше 6 символов", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(email, password, firstname, systemRole);
        });

        loginButton.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            login(email, password);

        });

        preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

    }

    private void login(String email, String password) {

        LoginRequest request = new LoginRequest(email, password);

        Gson gson = new Gson();

        String json = gson.toJson(request);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON);

        OkHttpClient client = new OkHttpClient();

        Request httpRequest = new Request.Builder()
                .url("http://localhost:8080/api/v1/auth/login")
                .post(requestBody)
                .build();

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Сервер недоступен " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                assert response.body() != null;
                String responseBody = response.body().string();
                AuthResponse authResponse = gson.fromJson(responseBody, AuthResponse.class);

                runOnUiThread(() -> {
                    if (response.isSuccessful() && authResponse.isSuccess()) {

                        String jwtToken = authResponse.getMessage();

                        preferences.edit()
                                .putString("jwt_token", jwtToken)
                                .putString("user_email", email)
                                .putLong("user_id", authResponse.getUserId())
                                .putString("user_name", authResponse.getFirstName())
                                .apply();

                        Toast.makeText(MainActivity.this,
                                "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();


                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    } else {

                        String errorMsg = authResponse.getMessage();

                        if (errorMsg == null || errorMsg.isEmpty()) {
                            errorMsg = "Неверный email или пароль";
                        }

                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    private void registerUser(String email, String password, String firstname, String systemRole) {

        RegisterAccountRequest request = new RegisterAccountRequest(
                email, password, firstname, systemRole
        );

        Gson gson = new Gson();

        String json = gson.toJson(request);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON);

        OkHttpClient client = new OkHttpClient();

        Request httpRequest = new Request.Builder()
                .url("http://localhost:8080/api/v1/auth/register")
                .post(requestBody)
                .build();

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Сервер недоступен", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                assert response.body() != null;
                String responseBody = response.body().string();

                RegisterAccountResponseDTO dto = gson.fromJson(responseBody, RegisterAccountResponseDTO.class);

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {

                        etEmail.setText(dto.getEmail());
                        etPassword.setText("");
                        etFirstName.setText("");
                        spinnerSystemRole.setSelection(0);
                        //TODO : подтвердить почту
                        Toast.makeText(MainActivity.this,
                                "Регистрация прошла успешно! Теперь войдите в аккаунт!", Toast.LENGTH_SHORT).show();

                    } else if (response.code() == 400) {

                        try {

                            Type type = new TypeToken<Map<String, String>>() {
                            }.getType();
                            Map<String, String> errors = gson.fromJson(responseBody, type);

                            StringBuilder errorMessage = new StringBuilder();
                            for (String er : errors.values()) {
                                errorMessage.append(er).append("\n");
                            }

                            Toast.makeText(MainActivity.this,
                                    errorMessage.toString(), Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this,
                                    "Ошибка регистрации! Проверьте введённые данные!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivity.this,
                                "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}