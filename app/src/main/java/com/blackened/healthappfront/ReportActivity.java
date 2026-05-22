package com.blackened.healthappfront;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackened.healthappfront.adapter.HealthRecordAdapterWithoutButtons;
import com.blackened.healthappfront.healthRecord.HealthRecordResponseDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;


public class ReportActivity extends BaseActivity{

    private Button btnFromDate;
    private Button btnToDate;
    private Button btnGenerateReport;
    private Button btnExportExcel;
    private Button btnSendEmail;

    private Spinner spinnerMetricType;
    private RecyclerView recyclerView;
    private HealthRecordAdapterWithoutButtons adapter;
    private List<HealthRecordResponseDTO> currentRecords;

    private String fromDate;
    private String toDate;
    private static final String TITLE = "\uD83D\uDCCA Отчёт";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setUpToolbar();
        setToolbarTitle(TITLE);
        enableBackButton();

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

        adapter = new HealthRecordAdapterWithoutButtons();
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

        fetchReport(fromDate, toDate, selectedType, sessionManager.getUserId());

    }
    @SuppressLint("DefaultLocale")
    private void fetchReport(String fromDate, String toDate, String selectedType, Long targetId) {

        String from = fromDate + "T00:00:00";
        String to = toDate + "T23:59:59";

        if (selectedType == null || selectedType.isEmpty()) {
            return;
        }

        ApiClient.get(
                String.format("health-records/history/%d?type=%s&from=%s&to=%s", targetId, selectedType, from, to),
                sessionManager.getToken(),
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<HealthRecordResponseDTO>>() {
                        }.getType();
                        currentRecords = gson.fromJson(response, type);
                        adapter.setRecords(currentRecords);

                        if (currentRecords.isEmpty()) {
                            Toast.makeText(ReportActivity.this, "Нет записей", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ReportActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void exportEcxel() {
        Toast.makeText(this, "Будьте прокляты! Кнопка в разработке!!!", Toast.LENGTH_LONG).show();
    }
}
