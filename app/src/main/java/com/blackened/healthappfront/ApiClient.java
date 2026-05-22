package com.blackened.healthappfront;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/v1/";
    private static final MediaType JSON = MediaType.parse(KeyWords.APPLICATION_JSON.getWord());
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final Gson GSON = new Gson();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ApiCallback {
        void onSuccess(String response);

        void onError(String error);
    }

    public static void get(String endpoint, @Nullable String token, ApiCallback callback) {

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get();

        addHeader(builder, token);

        execute(builder.build(), callback);
    }

    public static void post(String endpoint, @Nullable String token, Object dtoBody, ApiCallback callback) {

        String json = getJson(dtoBody);

        RequestBody requestBody = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(requestBody);

        addHeader(builder, token);

        execute(builder.build(), callback);

    }

    public static void put(String endpoint, @Nullable String token, Object dtoBody, ApiCallback callback) {
        String json = getJson(dtoBody);

        RequestBody requestBody = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(requestBody);

        addHeader(builder, token);

        execute(builder.build(), callback);
    }

    public static void delete() {
    }

    private static void execute(Request httpRequest, ApiCallback callbackack) {
        CLIENT.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mainHandler.post(() -> {
                    callbackack.onError("Ошибка сети " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String responseBody = response.body() != null ? response.body().string() : "0";

                mainHandler.post(() -> {
                    callbackack.onSuccess(responseBody);
                });
            }
        });
    }

    private static void addHeader(Request.Builder builder, @Nullable String token) {
        if (token != null && !token.isEmpty()) {
            builder.addHeader(KeyWords.AUTHORIZATION.getWord(), KeyWords.BEARER.getWord() + token);
        }
    }

    private static String getJson(Object request) {
        return GSON.toJson(request);
    }

}
