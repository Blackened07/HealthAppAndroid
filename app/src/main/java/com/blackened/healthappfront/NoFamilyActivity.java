package com.blackened.healthappfront;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.blackened.healthappfront.familyInvitation.CreateFamilyRequest;
import com.blackened.healthappfront.familyInvitation.FamilyInvitationRequestDTO;
import com.blackened.healthappfront.familyInvitation.FamilyInvitationResponseDTO;
import com.blackened.healthappfront.familyInvitation.FamilyResponseDTO;
import com.google.gson.Gson;

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
                    /*Type type = new TypeToken<FamilyInvitationResponseDTO>() {
                    }.getType();*/
                    responseDTO = gson.fromJson(response, FamilyInvitationResponseDTO.class);

                    showSecretCodeAlert(responseDTO);
                    /*Toast.makeText(NoFamilyActivity.this, responseDTO.getSecretCode(), Toast.LENGTH_SHORT).show();*/
                }

            }

            @Override
            public void onError(String error) {
                Toast.makeText(NoFamilyActivity.this, "Something wrong 3", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showSecretCodeAlert(FamilyInvitationResponseDTO responseDTO) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✨ Секретный код создан ✨");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        //layout.setGravity(Gravity.CENTER);

        TextView description = new TextView(this);
        description.setText("Отправьте этот код человеку, которого хотите пригласить");
        description.setTextSize(14);
        description.setGravity(Gravity.CENTER);
        description.setPadding(0, 0, 0, 20);

        TextView secretCode = new TextView(this);
        secretCode.setText(responseDTO.getSecretCode());
        secretCode.setTextSize(28);

        layout.addView(description);
        layout.addView(secretCode);

        builder.setView(layout);

        builder.setPositiveButton("\uD83D\uDCE4 Поделиться", (d, w) -> {
            shareInvitation(responseDTO.getSecretCode());
        });

        builder.setNeutralButton("\uD83D\uDCCB Копировать", (d, w) -> {
            copyToClipboard(responseDTO.getSecretCode());
        });

        builder.setNegativeButton("Закрыть", null);

        builder.show();

    }
    private void shareInvitation(String secretCode) {
        String message = "\uD83C\uDFE0 Приглашение в семью!\nКод для вступления: " + secretCode;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Приглашение в семью");

        startActivity(Intent.createChooser(intent, "Отправить через: "));
    }

    private void copyToClipboard(String secretCode) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("invitation_code", secretCode);
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(this, " ✅ Код скопирован", Toast.LENGTH_LONG).show();
    }

    private void processSending() {

        //EnterCode
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Проверка кода");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);

        EditText adminUserEmail = new EditText(this);
        EditText secretCode = new EditText(this);

        adminUserEmail.setHint("Введите email ");
        adminUserEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        secretCode.setHint("Введите код");
        secretCode.setInputType(InputType.TYPE_CLASS_TEXT);

        layout.addView(adminUserEmail);
        layout.addView(secretCode);

        builder.setView(layout);

        builder.setPositiveButton("Отправить", (d, w) -> {
            String adminEmail = adminUserEmail.getText().toString().trim();
            String code = secretCode.getText().toString().trim();

            sendInv(code, adminEmail);
        });
        //Click to send
        //if all right -> create family -> start new activity (Family manager)
        //else -> AlertDialog with error text
        builder.setNegativeButton("Отмена", null);

        builder.show();
    }

    private void sendInv(String code, String adminEmail) {

        CreateFamilyRequest familyRequest = new CreateFamilyRequest(code, adminEmail);

        String endpoint = "families/" + adminEmail;

        ApiClient.post(endpoint, sessionManager.getToken(), familyRequest, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                if (response.contains("critical")) {
                    ErrorResponse dto = gson.fromJson(response, ErrorResponse.class);
                    Toast.makeText(NoFamilyActivity.this, dto.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    FamilyResponseDTO dto =  gson.fromJson(response, FamilyResponseDTO.class);

                    Toast.makeText(NoFamilyActivity.this, "Семья создана!!!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(NoFamilyActivity.this, FamilyManagerActivity.class);
                    intent.putExtra("familyId", dto.getFamilyId());
                    intent.putExtra("familyName", dto.getFamilyName());
                    intent.putExtra("familyRole", dto.getDisplayableRole());

                    startActivity(intent);
                }

            }

            @Override
            public void onError(String error) {
                Toast.makeText(NoFamilyActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
