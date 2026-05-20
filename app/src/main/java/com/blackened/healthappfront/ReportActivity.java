package com.blackened.healthappfront;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackened.healthappfront.healthRecord.HealthRecordAdapter;
import com.blackened.healthappfront.healthRecord.HealthRecordResponseDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReportActivity extends BaseActivity{

    private Button btnFromDate;
    private Button btnToDate;
    private Button btnGenerateReport;
    private Button btnExportExcel;
    private Button btnSendEmail;

    private Spinner spinnerMetricType;
    private RecyclerView recyclerView;
    private HealthRecordAdapter adapter;
    private List<HealthRecordResponseDTO> currentRecords;

    private String fromDate;
    private String toDate;

    private String jwtToken;
    private Long userId;

    private static final String TITLE = "\uD83D\uDCCA Отчёт";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setUpToolbar();
        setToolbarTitle(TITLE);
        enableBackButton();

        preferences = getSharedPreferences(KeyWords.APP_PREFS.getWord(), MODE_PRIVATE);
        jwtToken = preferences.getString(KeyWords.JWT_TOKEN.getWord(), null);
        userId = preferences.getLong(KeyWords.USER_ID.getWord(), -1);

        if (jwtToken == null || userId == -1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        initViews();

        setupRecyclerView();

        setupSpinner();
        setupListeners();

    }

    protected void initViews() {
        btnFromDate = findViewById(R.id.btn_from_date);
        btnToDate = findViewById(R.id.btn_to_date);
        btnGenerateReport = findViewById(R.id.btn_generate);
        btnExportExcel = findViewById(R.id.btn_export_excel);
        btnSendEmail = findViewById(R.id.btn_send_email);
        recyclerView = findViewById(R.id.rv_report);
        spinnerMetricType = findViewById(R.id.spinner_metric_type);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new HealthRecordAdapter();
        recyclerView.setAdapter(adapter);
    }
    private void setupSpinner() {

        String[] types = getMetricTypesText();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                types);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetricType.setAdapter(spinnerAdapter);

    }

    private void setupListeners() {
        btnFromDate.setOnClickListener(v -> showDatePicker(true));
        btnToDate.setOnClickListener(v -> showDatePicker(false));
        btnGenerateReport.setOnClickListener(v -> generateReport());
        btnExportExcel.setOnClickListener(v -> exportEcxel());
        btnSendEmail.setOnClickListener(v -> exportEcxel());
    }

    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                ((view, year1, month1, dayOfMonth) -> {
                    @SuppressLint("DefaultLocale") String date = String.format(
                            "%d-%02d-%02d",
                            year1,
                            month1 + 1,
                            dayOfMonth
                    );
                    if (isFromDate) {
                        fromDate = date;
                        btnFromDate.setText(
                                String.format("%s + %s", KeyWords.DATE_FROM.getWord(), date));
                    } else {
                        toDate = date;
                        btnToDate.setText(
                                String.format("%s + %s", KeyWords.DATE_TO.getWord(), date)
                        );
                    }
                }),
                year, month, day
        );

        datePickerDialog.show();

    }

    private void generateReport() {

        if (fromDate.isEmpty() || toDate.isEmpty()) {
            Toast.makeText(this, "Выберите даты", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedType = MetricTypes.fromString(
                spinnerMetricType.getSelectedItem().toString()
        ).name();

        fetchReport(fromDate, toDate, selectedType, userId);

    }


    private void fetchReport(String fromDate, String toDate, String selectedTypes, Long targetId) {

        String from = fromDate + "T00:00:00";
        String to = toDate + "T23:59:59";

        if (selectedTypes == null || selectedTypes.isEmpty()) {
            return;
        }

        HttpUrl url = Objects
                .requireNonNull(HttpUrl.parse("http://localhost:8080/api/v1/health-records/history/" + targetId)) // <- TARGET_ID
                .newBuilder()
                .addQueryParameter("type", selectedTypes)
                .addQueryParameter("from", from)
                .addQueryParameter("to", to)
                .build();

        Request request = getHttpRequestForGetMethods(url, jwtToken);

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ReportActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String responseBody = response.body().string();

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<HealthRecordResponseDTO>>() {
                        }.getType();
                        currentRecords = gson.fromJson(responseBody, type);
                        adapter.setRecords(currentRecords);

                        if (currentRecords.isEmpty()) {
                            Toast.makeText(ReportActivity.this, "Нет записей", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ReportActivity.this, "Найдено записей", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ReportActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void exportEcxel() {
        Toast.makeText(this, "Будьте прокляты! Кнопка в разработке!!!", Toast.LENGTH_LONG).show();
    }
}
