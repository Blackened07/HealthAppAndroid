package com.blackened.healthappfront;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackened.healthappfront.adapter.FamilyMemberAdapter;
import com.blackened.healthappfront.user.CreateVirtualMemberRequest;
import com.blackened.healthappfront.user.InviteMemberRequest;
import com.blackened.healthappfront.user.UserResponseDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FamilyManagerActivity extends BaseActivity {

    private static final String TITLE = "Моя Семья";

    private TextView tvFamilyName;
    private TextView tvRole;

    private ImageButton btnAddMember;
    private RecyclerView rvMembers;
    private TextView emptyMembers;

    //leave delete

    private FamilyMemberAdapter adapter;

    private Long familyId;
    private String familyName;
    private String familyRole;

    private List<UserResponseDTO> members = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_manager);

        setUpToolbar();
        setToolbarTitle(TITLE);
        setUpNavMenu();

        familyId = getIntent().getLongExtra("familyId", -1);
        familyName = getIntent().getStringExtra("familyName");
        familyRole = getIntent().getStringExtra("familyRole");

        initViews();
        setUpTexts(familyName, familyRole);
        setUpRecyclerView();

    }

    private void setUpTexts(String familyName, String familyRole) {
        tvFamilyName.setText(familyName);
        tvRole.setText(familyRole);
    }

    @Override
    protected void initViews() {
        tvFamilyName = findViewById(R.id.tv_family_name);
        tvRole = findViewById(R.id.tv_role);
        rvMembers = findViewById(R.id.rv_family_members);
    }

    private void setUpRecyclerView() {
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FamilyMemberAdapter();
        rvMembers.setAdapter(adapter);

        adapter.setOnMemberListener((targetId, targetName, targetRole) -> {
            //TODO: new intent
            Intent intent = new Intent(FamilyManagerActivity.this, MemberRecordsActivity.class);
            intent.putExtra("targetId", targetId);
            intent.putExtra("familyId", familyId);
            intent.putExtra("targetName", targetName);
            intent.putExtra("targetRole", targetRole);
            startActivity(intent);
        });

        getFamilyMembers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_family_admin, menu);

        MenuItem inv = menu.findItem(R.id.invite);
        MenuItem addV = menu.findItem(R.id.add_virtual);
        MenuItem remove = menu.findItem(R.id.remove_member);

        if (familyRole.equals("Член семьи")) {
            if (inv != null) inv.setVisible(false);
            if (addV != null) addV.setVisible(false);
            if (remove != null) remove.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.invite) {
            createInviteMemberDialog();
            return true;
        }

        if (id == R.id.add_virtual) {
            createAddVirtualMemberDialog();
            return true;
        }

        if (id == R.id.remove_member) {
            removeMember();
            return true;
        }

        if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeMember() {
        Toast.makeText(this, "Пока лень делать", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void createInviteMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Создание виртуального члена семьи");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        layout.setGravity(Gravity.CENTER);

        TextView description = new TextView(this);
        description.setText("Введите email члена семьи");
        description.setTextSize(14);
        description.setGravity(Gravity.CENTER);
        description.setPadding(0, 0, 0, 20);

        EditText invitedMemberEmail = new EditText(this);
        invitedMemberEmail.setHint("email члена семьи");
        invitedMemberEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        layout.addView(description);
        layout.addView(invitedMemberEmail);

        builder.setView(layout);

        builder.setPositiveButton("Отправить", (d , w) -> {
           String invitedEmail = invitedMemberEmail.getText().toString().trim();

           if (invitedEmail.isEmpty()) {
               Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
               return;
           }

            InviteMemberRequest request = new InviteMemberRequest(invitedEmail);

           inviteMember(request);
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void createAddVirtualMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Создание виртуального члена семьи");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        layout.setGravity(Gravity.CENTER);

        TextView description = new TextView(this);
        description.setText("Введите имя виртуального члена семьи");
        description.setTextSize(14);
        description.setGravity(Gravity.CENTER);
        description.setPadding(0, 0, 0, 20);

        EditText virtualUserName = new EditText(this);
        virtualUserName.setHint("Имя виртуального члена семьи");
        virtualUserName.setInputType(InputType.TYPE_CLASS_TEXT);

        layout.addView(description);
        layout.addView(virtualUserName);

        builder.setView(layout);

        builder.setPositiveButton("Создать", (d, w) -> {
            String virtualName = virtualUserName.getText().toString().trim();

            if (virtualName.isEmpty()) {
                Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
                return;
            }

            CreateVirtualMemberRequest request = new CreateVirtualMemberRequest(virtualName);

            inviteMember(request);
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();

    }

    private void inviteMember(Object request) {

        String endpoint = "";

        if (request instanceof CreateVirtualMemberRequest) {
            endpoint = "families/" + familyId + "/virtual-members";
        }
        if (request instanceof InviteMemberRequest) {
            endpoint = "families/" + familyId + "/invite";
        }

        ApiClient.post(endpoint, sessionManager.getToken(), request, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();

                if (response.contains("critical")) {

                    ErrorResponse dto = gson.fromJson(response, ErrorResponse.class);

                    Toast.makeText(FamilyManagerActivity.this, dto.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    getFamilyMembers();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(FamilyManagerActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getFamilyMembers() {

        String endpoint = "families/" + familyId + "/members";

        ApiClient.get(endpoint, sessionManager.getToken(), new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();

                if (response.contains("critical")) {

                    ErrorResponse dto = gson.fromJson(response, ErrorResponse.class);

                    Toast.makeText(FamilyManagerActivity.this, dto.getMessage(), Toast.LENGTH_LONG).show();
                } else {

                    Type type = new TypeToken<List<UserResponseDTO>>() {
                    }.getType();
                    members = gson.fromJson(response, type);

                    adapter.setMembers(members);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(FamilyManagerActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
            }
        });

    }
}
