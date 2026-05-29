package com.blackened.healthappfront;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blackened.healthappfront.auth.AuthResponse;
import com.blackened.healthappfront.auth.LoginRequest;
import com.blackened.healthappfront.auth.RegisterAccountRequest;
import com.blackened.healthappfront.auth.RegisterAccountResponseDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private TextInputLayout tilFirstName;
    private TextInputEditText etFirstName;
    private TextInputLayout tilRole;
    private MaterialButton btnAction;
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;

    private MaterialAutoCompleteTextView spinnerSystemRole;

    private MaterialButton registerButton;
    private MaterialButton loginButton;
    private MaterialButtonToggleGroup toggleGroup;
    private Map<String, String> roleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      /*  if (sessionManager.getToken() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }*/

        setContentView(R.layout.activity_main);

        initViews();

        setUpRoleMAp();
        setupListeners();
        initToggleButtons();
      /*  inflateSpinner();*/
    }

    /*private void inflateSpinner() {
        String[] roles = getResources().getStringArray(R.array.account_role);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                roles
        );
        spinnerSystemRole.setAdapter(adapter);
    }*/

    private void setUpRoleMAp() {
        roleMap = new HashMap<>();
        roleMap.put("Пользователь", "USER");
        roleMap.put("Доктор", "DOCTOR");
    }

    @Override
    protected void initViews() {

        tilFirstName = findViewById(R.id.til_first_name);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilRole = findViewById(R.id.til_role);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etFirstName = findViewById(R.id.et_first_name);
        spinnerSystemRole = findViewById(R.id.spinner_role);

        btnAction = findViewById(R.id.btn_action);
        toggleGroup = findViewById(R.id.toggle_mode);

        registerButton = findViewById(R.id.btn_toggle_register);
        loginButton = findViewById(R.id.btn_toggle_login);

        if (toggleGroup.getCheckedButtonId() == View.NO_ID) {
            toggleGroup.check(loginButton.getId());
        }
    }

    private void initToggleButtons() {

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_toggle_login) {
                    tilFirstName.setVisibility(View.GONE);
                    tilRole.setVisibility(View.GONE);
                    btnAction.setText("Войти");
                } else if (checkedId == registerButton.getId()) {
                    tilFirstName.setVisibility(View.VISIBLE);
                    tilRole.setVisibility(View.VISIBLE);
                    btnAction.setText("Зарегистрироваться");
                }
            }
        });
    }
    private void setupListeners() {
        btnAction.setOnClickListener(v -> {
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(etPassword.getText()).toString().trim();

            if (tilFirstName.getVisibility() == View.VISIBLE) {
                String firstname = Objects.requireNonNull(etFirstName.getText()).toString().trim();
                String selectedRole = spinnerSystemRole.getText().toString();
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
            } else {
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                login(email, password);
            }
        });
    }
    private void login(String email, String password) {

        LoginRequest request = new LoginRequest(email, password);

        Gson gson = new Gson();

        String json = gson.toJson(request);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON);

        OkHttpClient client = new OkHttpClient();

        Request httpRequest = new Request.Builder()
                .url(ApiClient.BASE_URL + "auth/login")
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

                        sessionManager.edit(jwtToken, email, authResponse);

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
                .url(ApiClient.BASE_URL + "auth/register")
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

                        Intent intent = new Intent(MainActivity.this, VerificationActivity.class);
                        intent.putExtra("userEmail", dto.getEmail());
                        startActivity(intent);


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