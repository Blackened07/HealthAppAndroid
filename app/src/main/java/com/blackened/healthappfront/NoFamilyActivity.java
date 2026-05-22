package com.blackened.healthappfront;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blackened.healthappfront.familyInvitation.FamilyInvitationRequestDTO;
import com.blackened.healthappfront.familyInvitation.FamilyInvitationResponseDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NoFamilyActivity extends BaseActivity {

    private static final String TITLE = "Семья";

    private Button btnCreate;
    private Button btnSendSecretCode;

    private FamilyInvitationResponseDTO responseDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_family);

        setUpToolbar();
        setToolbarTitle(TITLE);

        initViews();
        setupClickListeners();
    }

    @Override
    protected void initViews() {
        btnCreate = findViewById(R.id.btn_create_family);
        btnSendSecretCode = findViewById(R.id.btn_enter_secret_code);
    }

    private void setupClickListeners() {

        btnCreate.setOnClickListener((v) -> showCreationDialog());
        btnSendSecretCode.setOnClickListener((v) -> processSending());

    }

    private void showCreationDialog() {
        //create dialog with ENTER FAMILY NAME and generate code button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Создание семьи");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);

        EditText invitedUserEmail = new EditText(this);
        EditText familyName = new EditText(this);

        invitedUserEmail.setHint("Введите e-mail пользователя");
        invitedUserEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        familyName.setHint("Введите название семьи");
        familyName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        layout.addView(invitedUserEmail);
        layout.addView(familyName);

        builder.setView(layout);

        builder.setPositiveButton(KeyWords.SEND.getWord(), (a, b) -> /*processCreating(invitedUserEmail, familyName)*/ {

            String email = invitedUserEmail.getText().toString().trim();
            String fName = familyName.getText().toString().trim();

            if (email.isEmpty() || fName.isEmpty()) {
                Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
                return;
            }

            //regex email and toast

            FamilyInvitationRequestDTO request = new FamilyInvitationRequestDTO(email, fName);

            createInvitation(request);
        });

        builder.setNegativeButton(KeyWords.CANCEL.getWord(), null);

        builder.show();

    }

    private void createInvitation(FamilyInvitationRequestDTO request) {

        ApiClient.post("invitation", sessionManager.getToken(), request, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();

                if (response.contains("critical")) {
                    ErrorResponse dto = gson.fromJson(response, ErrorResponse.class);

                    Toast.makeText(NoFamilyActivity.this, dto.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Type type = new TypeToken<FamilyInvitationResponseDTO>() {
                    }.getType();
                    responseDTO = gson.fromJson(response, type);
                    Toast.makeText(NoFamilyActivity.this, responseDTO.getSecretCode(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(String error) {
                Toast.makeText(NoFamilyActivity.this, "Something wrong 3", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void processSending() {
    }


}
