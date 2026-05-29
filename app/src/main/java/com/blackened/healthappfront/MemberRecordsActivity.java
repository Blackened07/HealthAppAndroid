package com.blackened.healthappfront;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.blackened.healthappfront.user.UserResponseDTO;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemberRecordsActivity extends Calendar {

    private static final String TITLE = "Записи участника";

    private Spinner spinnerMemberSelector;
    private TextView tvMemberName;
    private TextView tvMemberRole;
    private TextView tvTotalRecords;
    private TextView tvLastActivity;
    private TextView tvDateHeader;
    private ImageButton btnReport;
    private Long selectedUserId;
    private String selectedUserName;
    private String selectedUserRole;
    private Long familyId;

    private ArrayAdapter<String> spinnerAdapter;
    private List<String> members = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_records);
        /*setUpToolbar();
        setToolbarTitle(TITLE);*/

        //get data from intent
        selectedUserId = getIntent().getLongExtra("targetId", -1);
        selectedUserName = getIntent().getStringExtra("targetName");
        selectedUserRole = getIntent().getStringExtra("targetRole");
        familyId = getIntent().getLongExtra("familyId", -1);
        initViews();

        setupRecyclerView(selectedUserId);
        setupCalendar(selectedUserId);
        setupFabAdd(selectedUserId);

        setupSpinner();
        setUpListeners();
        setupTexts();
    }

    private void setUpListeners() {
        btnReport.setOnClickListener(v -> {
            Intent intent = new Intent(MemberRecordsActivity.this, ReportActivity.class);
            intent.putExtra("targetId", selectedUserId);
            intent.putExtra("targetName", selectedUserName);
            startActivity(intent);
        });
    }

    private void setupTexts() {
        tvMemberName.setText(selectedUserName);
        tvMemberRole.setText(selectedUserRole);
        tvTotalRecords.setText(String.valueOf(adapter.getItemCount()));
    }

    @Override
    protected void initViews() {
        spinnerMemberSelector = findViewById(R.id.spinner_member_selector);
        tvMemberName = findViewById(R.id.tv_member_name);
        tvMemberRole = findViewById(R.id.tv_member_role);
        tvTotalRecords = findViewById(R.id.tv_total_records);
        tvLastActivity = findViewById(R.id.tv_last_activity);
        tvDateHeader = findViewById(R.id.tv_date_header);
        btnReport = findViewById(R.id.btn_report);
        calendarView = findViewById(R.id.calendar_view);
        recyclerView = findViewById(R.id.rv_health_records);
        fabAdd = findViewById(R.id.fab_add);
    }

    private void setupSpinner() {

        spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                members);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMemberSelector.setAdapter(spinnerAdapter);

        getFamilyMembers();
    }

    public void setMembers(List<UserResponseDTO> members) {
        if (members == null) return;

        this.members.clear();

        this.members = members.stream()
                .map(UserResponseDTO::getFirstName)
                .collect(Collectors.toList());

        spinnerAdapter.notifyDataSetChanged();
    }

    private void getFamilyMembers() {

        String endpoint = "families/" + familyId + "/members";

        ApiClient.get(endpoint, sessionManager.getToken(), new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();

                if (response.contains("critical")) {

                    ErrorResponse dto = gson.fromJson(response, ErrorResponse.class);

                    Toast.makeText(MemberRecordsActivity.this, dto.getMessage(), Toast.LENGTH_LONG).show();
                } else {

                    Type type = new TypeToken<List<UserResponseDTO>>() {
                    }.getType();

                    List<UserResponseDTO> m = gson.fromJson(response, type);

                    setMembers(m);

                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MemberRecordsActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
            }
        });

    }

}
