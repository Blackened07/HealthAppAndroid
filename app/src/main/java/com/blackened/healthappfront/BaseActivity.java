package com.blackened.healthappfront;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blackened.healthappfront.familyInvitation.FamilyResponseDTO;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected SessionManager sessionManager;
    protected NavigationView nav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        if (!(this instanceof MainActivity)) {
            Long currentId = sessionManager.getUserId();
            String jwtToken = sessionManager.getToken();

            if (jwtToken == null || currentId == -1) {
                Intent intent = new Intent(this, MainActivity.class);
                /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);*/
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_report) {
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra("targetId", sessionManager.getUserId());
            intent.putExtra("targetName", sessionManager.getUserName());
            startActivity(intent);

            return true;
        }

        if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initViews(){};

    protected void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    protected void setUpNavMenu() {
        nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                sessionManager.logout(this);
                return true;
            }

            if (id == R.id.nav_contacts) {
                /*showContacts();*/
                return true;
            }

            if (id == R.id.nav_family) {
                checkFamilyStatus();
                return true;
            }
            return false;
        });

        String firstName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();

        updateNavHeader(
                nav,
                firstName,
                userEmail
        );
    }

    protected void enableBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    protected String[] getMetricTypesText() {
        String[] s = new String[MetricTypes.values().length];
        int counter = 0;
        for (MetricTypes types : MetricTypes.values()) {
            s[counter] = types.getMetricText();
            counter++;
        }
        return s;
    }

    private void checkFamilyStatus() {
        String url = "families/is-no-family";

        ApiClient.get(url, sessionManager.getToken(), new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                FamilyResponseDTO dto = gson.fromJson(response, FamilyResponseDTO.class);

                Intent intent;

                if (dto.isNoFamily()) {
                    intent = new Intent(BaseActivity.this, NoFamilyActivity.class);
                } else {
                    intent = new Intent(BaseActivity.this, FamilyManagerActivity.class);
                    intent.putExtra("familyId", dto.getFamilyId());
                    intent.putExtra("familyName", dto.getFamilyName());
                    intent.putExtra("familyRole", dto.getDisplayableRole());
                }

                startActivity(intent);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(BaseActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNavHeader(NavigationView nav, String userName, String userEmail) {
        View headerView = nav.getHeaderView(0);

        TextView name = headerView.findViewById(R.id.tv_user_name);
        TextView email = headerView.findViewById(R.id.tv_user_email);

        name.setText(userName);
        email.setText(userEmail);
    }


}
