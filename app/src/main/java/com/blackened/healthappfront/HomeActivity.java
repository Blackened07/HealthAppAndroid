package com.blackened.healthappfront;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackened.healthappfront.healthRecord.HealthRecordAdapter;
import com.blackened.healthappfront.healthRecord.HealthRecordRequestDTO;
import com.blackened.healthappfront.healthRecord.HealthRecordResponseDTO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends BaseActivity {

    private Long userId;
    private String jwtToken;

    private RecyclerView recyclerView;
    private CalendarView calendarView;
    private FloatingActionButton fabAdd;
    private ImageView report;

    private Calendar calendar;
    private HealthRecordAdapter adapter;
    private NavigationView nav;
    private String today;
    private static final String ADD_RECORD = "Добавить запись";
    private static final String EDIT_RECORD = "Редактировать запись";

    private static final String TITLE = "HealthApp";

    private String currentSelectedDate;
    private OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDaySelected(String date);
    }



    @SuppressLint({"DefaultLocale", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        setUpToolbar();
        setToolbarTitle(TITLE);

        preferences = getSharedPreferences(KeyWords.APP_PREFS.getWord(), MODE_PRIVATE);
        Long currentId = preferences.getLong(KeyWords.USER_ID.getWord(), -1);
        String jwtToken = preferences.getString(KeyWords.JWT_TOKEN.getWord(), null);

        if (jwtToken == null || currentId == -1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        this.userId = currentId;
        this.jwtToken = jwtToken;
        initViews();
        setupRecyclerView();
        setupCalendar();
        setupFabAdd();
        setupNav();

    }
    @Override
    protected void initViews() {
        recyclerView = findViewById(R.id.rv_health_records);
        calendarView = findViewById(R.id.calendar_view);
        fabAdd = findViewById(R.id.fab_add);
        nav = findViewById(R.id.nav_view);
    }

    @SuppressLint("DefaultLocale")
    private void setupCalendar() {
        calendarView.setOnDateChangeListener(((view, year, month, dayOfMonth) -> {
            currentSelectedDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
            fetchSelfRecordsByDate(currentSelectedDate, userId);
        }));

        calendar = Calendar.getInstance();
        today = String.format("%d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        currentSelectedDate = today;
        fetchSelfRecordsByDate(today, userId);
    }

    private void setupRecyclerView() {
        adapter = new HealthRecordAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnDeleteClickListener(this::confirmDelete); //для удаления тоже можно сделать просмотр даты
        adapter.setOnEditClickListener(r -> {
            if (currentSelectedDate.equals(today)) {
                confirmEdit(r);
            } else {
                Toast.makeText(this, "Вы не можете редактирвать запись вчерашним числом", Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnNoteClickListener(this::viewNote);
    }

    private void setupFabAdd() {
        fabAdd.setOnClickListener((v) -> {
            if (currentSelectedDate.equals(today)) {
                showAddRecordDialogue(ADD_RECORD, null);
            } else {
                Toast.makeText(this, String.format("Вы не можете сделать запись на: %s", currentSelectedDate), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNav() {
        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                logout();
                return true;
            }
            return false;
        });

        String firstName = preferences.getString("user_name", "User_Name");
        String userEmail = preferences.getString("user_email", "mail@example.ru");

        updateNavHeader(
                nav,
                firstName,
                userEmail
        );
    }
    private void updateNavHeader(NavigationView nav, String userName, String userEmail) {
        View headerView = nav.getHeaderView(0);

        TextView name = headerView.findViewById(R.id.tv_user_name);
        TextView email = headerView.findViewById(R.id.tv_user_email);

        name.setText(userName);
        email.setText(userEmail);
    }

    private void showAddRecordDialogue(String title, Long targetRecordIdForEdit) {

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
        notes.setText("Примечание");

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
                    type, value1, value2, note
            );

            switch (title) {
                case ADD_RECORD:
                    createRecord(userId, requestDTO);
                    break;
                case EDIT_RECORD:
                    updateRecord(targetRecordIdForEdit, requestDTO, userId);
                    break;
            }
        }));

        builder.setNegativeButton("Отмена", null);
        builder.show();

    }


    private void fetchSelfRecordsByDate(String date, Long targetId) {

        String from = date + "T00:00:00";
        String to = date + "T23:59:59";

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("http://localhost:8080/api/v1/health-records/history/" + targetId))
                .newBuilder()
                .addQueryParameter("from", from)
                .addQueryParameter("to", to)
            /*    .addQueryParameter("actorId", String.valueOf(userId))*/
                .build();

        Request request = getHttpRequestForGetMethods(url, jwtToken);

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(HomeActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
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
                        List<HealthRecordResponseDTO> records = gson.fromJson(responseBody, type);

                        adapter.setRecords(records);

                        if (records.isEmpty()) {
                            Toast.makeText(HomeActivity.this, "Нет записей за: " + date, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }



    private void createRecord(Long targetId, HealthRecordRequestDTO request) {

        String url = "http://localhost:8080/api/v1/health-records/" + targetId /*+ "?actorId=" + actorId*/;

        Gson gson = new Gson();
        String json = gson.toJson(request);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + jwtToken)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(HomeActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                runOnUiThread(() -> {

                    if (response.isSuccessful()) {
                        Toast.makeText(HomeActivity.this, "Запись добавлена", Toast.LENGTH_SHORT).show();

                        refreshCurrentDateRecords(targetId);
                    } else {
                        Toast.makeText(HomeActivity.this, "Ошибка: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void refreshCurrentDateRecords(Long id) {
        if (currentSelectedDate != null) {
            fetchSelfRecordsByDate(currentSelectedDate, id);
        }
    }

    private void updateRecord(Long recordId, HealthRecordRequestDTO request, Long actorId) {
        @SuppressLint("DefaultLocale") String url = String.format("http://localhost:8080/api/v1/health-records/%d?actorId=%d", recordId, actorId);

        Gson gson = new Gson();
        String json = gson.toJson(request);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + jwtToken)
                .put(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(HomeActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                runOnUiThread(() -> {

                    if (response.isSuccessful()) {
                        Toast.makeText(HomeActivity.this, "Запись обновлена", Toast.LENGTH_SHORT).show();

                        refreshCurrentDateRecords(userId);
                    } else {
                        Toast.makeText(HomeActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void confirmDelete(HealthRecordResponseDTO r) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage(String.format("Удалить запись: %s?", r.getType()))
                .setPositiveButton("Да", (dialog, which) -> deleteRecord(r.getId(), userId))
                .setNegativeButton("Нет", null)
                .show();
    }

    private void viewNote(HealthRecordResponseDTO r) {
        new AlertDialog.Builder(this)
                .setTitle("Примечание к записи")
                .setMessage(r.getNote())
                .setPositiveButton("Ок", null)
                .show();
    }

    private void confirmEdit(HealthRecordResponseDTO r) {
        showAddRecordDialogue(EDIT_RECORD, r.getId());
        Log.d("EDIT_REC", String.valueOf(r.getId()));
    }

    private void deleteRecord(Long recordId, Long actorId) {
        @SuppressLint("DefaultLocale") String url = String.format("http://localhost:8080/api/v1/health-records/%d?actorId=%d", recordId, actorId);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + jwtToken)
                .delete()
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(HomeActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(HomeActivity.this, "Запись удалена", Toast.LENGTH_SHORT).show();
                        refreshCurrentDateRecords(userId);
                    } else {
                        Toast.makeText(HomeActivity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    private void logout() {
        SharedPreferences pref = getSharedPreferences("app_prefs", MODE_PRIVATE);
        pref.edit().clear().apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}