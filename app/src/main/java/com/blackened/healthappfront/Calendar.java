package com.blackened.healthappfront;

import android.annotation.SuppressLint;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackened.healthappfront.adapter.HealthRecordAdapter;
import com.blackened.healthappfront.healthRecord.HealthRecordRequestDTO;
import com.blackened.healthappfront.healthRecord.HealthRecordResponseDTO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class Calendar extends BaseActivity{

    protected RecyclerView recyclerView;
    protected CalendarView calendarView;
    protected String currentSelectedDate;
    protected FloatingActionButton fabAdd;

    protected java.util.Calendar calendar;
    protected HealthRecordAdapter adapter;

    protected String today;
    protected static final String ADD_RECORD = "Добавить запись";
    protected static final String EDIT_RECORD = "Редактировать запись";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("DefaultLocale")
    protected void setupCalendar(Long userId) {
        calendarView.setOnDateChangeListener(((view, year, month, dayOfMonth) -> {
            currentSelectedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
            fetchSelfRecordsByDate(currentSelectedDate, userId);
        }));

        calendar = java.util.Calendar.getInstance();
        today = String.format("%d-%02d-%02d",
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH) + 1,
                calendar.get(java.util.Calendar.DAY_OF_MONTH));
        currentSelectedDate = today;
        fetchSelfRecordsByDate(today, userId);
    }

    protected void setupRecyclerView(Long targetId) {

        adapter = new HealthRecordAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnDeleteClickListener(r -> {
            confirmDelete(r, targetId);//для удаления тоже можно сделать просмотр даты
        });

        adapter.setOnEditClickListener(r -> {
            if (currentSelectedDate.equals(today)) {
                confirmEdit(r, targetId);
            } else {
                Toast.makeText(this, "Вы не можете редактирвать запись вчерашним числом", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnNoteClickListener(r -> {
            viewNote(r, targetId);
        });
    }

    protected void setupFabAdd(Long targetId) {
        fabAdd.setOnClickListener((v) -> {
            if (currentSelectedDate.equals(today)) {
                showAddRecordDialogue(ADD_RECORD, null, targetId);
            } else {
                Toast.makeText(this, String.format("Вы не можете сделать запись на: %s", currentSelectedDate), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void showAddRecordDialogue(String title, Long targetRecordIdForEdit, Long targetId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);

        Spinner spinnerType = new Spinner(this);
        String[] types = getMetricTypesText();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        spinnerType.setAdapter(adapter);

        EditText etVal1 = new EditText(this);
        etVal1.setHint("Значение_1");
        etVal1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        EditText etVal2 = new EditText(this);
        etVal2.setHint("Значение_2");
        etVal2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        EditText notes = new EditText(this);
        notes.setHint("Примечание");

        layout.addView(spinnerType);
        layout.addView(etVal1);
        layout.addView(etVal2);
        layout.addView(notes);

        builder.setView(layout);

        builder.setPositiveButton("Сохранить", ((dialog, which) -> {

            String type = MetricTypes.fromString(spinnerType.getSelectedItem().toString()).name();
            String val1 = etVal1.getText().toString().trim();
            String val2 = etVal2.getText().toString().trim();
            String note = notes.getText().toString().trim();

            if (val1.isEmpty()) {
                Toast.makeText(this, "введите значение", Toast.LENGTH_SHORT).show();
                return;
            }

            double value1 = Double.parseDouble(val1);
            double value2 = 0;

            if (!val2.isEmpty()) {
                value2 = Double.parseDouble(val2);
            }

            HealthRecordRequestDTO requestDTO = new HealthRecordRequestDTO(
                    type, value1, value2, note, ApiClient.TIMESTAMP, ApiClient.ZONE_OFFSET
            );

            switch (title) {
                case ADD_RECORD:
                    createRecord(targetId, requestDTO);
                    break;
                case EDIT_RECORD:
                    updateRecord(targetRecordIdForEdit, requestDTO);
                    break;
            }
        }));

        builder.setNegativeButton("Отмена", null);
        builder.show();

    }


    @SuppressLint("DefaultLocale")
    private void fetchSelfRecordsByDate(String date, Long targetId) {

        String from = date + "T00:00:00";
        String to = date + "T23:59:59";

        ApiClient.get(
                String.format("health-records/history/%d?from=%s&to=%s", targetId, from, to),
                sessionManager.getToken(),
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<HealthRecordResponseDTO>>() {
                        }.getType();
                        List<HealthRecordResponseDTO> records = gson.fromJson(response, type);

                        adapter.setRecords(records);

                        /*if (records.isEmpty()) {
                            Toast.makeText(Calendar.this, "Нет записей за: " + date, Toast.LENGTH_SHORT).show();
                        }*/
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(Calendar.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void createRecord(Long targetId, HealthRecordRequestDTO request) {



        ApiClient.post("health-records/" + targetId, sessionManager.getToken(), request, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {

                if (response.contains("critical")) {
                    Gson gson = new Gson();
                    ErrorResponse dto = gson.fromJson(response, ErrorResponse.class);

                    Toast.makeText(Calendar.this, dto.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                   /* Toast.makeText(Calendar.this, "Запись добавлена", Toast.LENGTH_SHORT).show();*/

                    refreshCurrentDateRecords(targetId);
                }

            }

            @Override
            public void onError(String error) {
                Toast.makeText(Calendar.this, error, Toast.LENGTH_SHORT).show();
            }
        });

    }
    @SuppressLint("DefaultLocale")
    private void updateRecord(Long recordId, HealthRecordRequestDTO request) {

        ApiClient.put(String.format("%d", recordId), sessionManager.getToken(), request, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.contains("critical")) {
                    Gson gson = new Gson();
                    ErrorResponse dto = gson.fromJson(response, ErrorResponse.class);

                    Toast.makeText(Calendar.this, dto.getMessage(), Toast.LENGTH_SHORT).show();

                } else {

                   /* Toast.makeText(Calendar.this, "Запись обновлена", Toast.LENGTH_SHORT).show();*/

                    refreshCurrentDateRecords(sessionManager.getUserId());
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Calendar.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshCurrentDateRecords(Long id) {
        if (currentSelectedDate != null) {
            fetchSelfRecordsByDate(currentSelectedDate, id);
        }
    }
    private void confirmDelete(HealthRecordResponseDTO r, Long targetId) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage(String.format("Удалить запись: %s?", r.getDisplayType()))
                .setPositiveButton("Да", (dialog, which) -> deleteRecord(r.getId(), sessionManager.getUserId()))
                .setNegativeButton("Нет", null)
                .show();
    }

    private void viewNote(HealthRecordResponseDTO r, Long targetId) {
        new AlertDialog.Builder(this)
                .setTitle("Примечание к записи")
                .setMessage(r.getNote())
                .setPositiveButton("Ок", null)
                .show();
    }

    private void confirmEdit(HealthRecordResponseDTO r, Long targetId) {
        showAddRecordDialogue(EDIT_RECORD, r.getId(), targetId);
    }

    private void deleteRecord(Long recordId, Long actorId) {
        @SuppressLint("DefaultLocale") String url = String.format("http://localhost:8080/api/v1/health-records/%d?actorId=%d", recordId, actorId);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + sessionManager.getToken())
                .delete()
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(Calendar.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        /*Toast.makeText(Calendar.this, "Запись удалена", Toast.LENGTH_SHORT).show();*/
                        refreshCurrentDateRecords(sessionManager.getUserId());
                    } else {
                        Toast.makeText(Calendar.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
