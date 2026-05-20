package com.blackened.healthappfront;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import okhttp3.HttpUrl;
import okhttp3.Request;

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected SharedPreferences preferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            startActivity(new Intent(this, ReportActivity.class));
            return true;
        }

        if (id == android.R.id.home) {
            //finish();
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract void initViews();

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

    protected Request getHttpRequestForGetMethods(HttpUrl url, String token) {
        return new Request.Builder()
                .url(url)
                .addHeader(KeyWords.AUTHORIZATION.getWord(), KeyWords.BEARER.getWord() + token)
                .get().build();
    }
}
