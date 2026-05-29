package com.blackened.healthappfront;


import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;

public class HomeActivity extends Calendar {
    private static final String TITLE = "HealthApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*EdgeToEdge.enable(this);*/
        setContentView(R.layout.activity_home);

        setUpToolbar();
        setToolbarTitle(TITLE);
        setUpNavMenu();

        initViews();
        setupRecyclerView(sessionManager.getUserId());
        setupCalendar(sessionManager.getUserId());
        setupFabAdd(sessionManager.getUserId());
    }
    @Override
    protected void initViews() {
        recyclerView = findViewById(R.id.rv_health_records);
        calendarView = findViewById(R.id.calendar_view);
        fabAdd = findViewById(R.id.fab_add);
    }

}