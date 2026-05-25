package com.blackened.healthappfront;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackened.healthappfront.adapter.FamilyMemberAdapter;
import com.blackened.healthappfront.healthRecord.HealthRecordResponseDTO;
import com.blackened.healthappfront.user.UserResponseDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class FamilyManagerActivity extends BaseActivity{

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_manager);

        setUpToolbar();
        setToolbarTitle(TITLE);

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

        getFamilyMembers();
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
                    List<UserResponseDTO> members = gson.fromJson(response, type);

                    adapter.setMembers(members);
                }
            }

            @Override
            public void onError(String error) {

            }
        });

    }
}
