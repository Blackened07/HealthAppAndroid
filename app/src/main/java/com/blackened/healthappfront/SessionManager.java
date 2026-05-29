package com.blackened.healthappfront;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.blackened.healthappfront.auth.AuthResponse;

public class SessionManager {
    private SharedPreferences preferences;

    public SessionManager(Context context) {
        this.preferences = context.getSharedPreferences(KeyWords.APP_PREFS.getWord(), MODE_PRIVATE);
    }

    public void edit(String jwtToken, String email, AuthResponse authResponse) {
        preferences.edit()
                .putString("jwt_token", jwtToken)
                .putString("user_email", email)
                .putLong("user_id", authResponse.getUserId())
                .putString("user_name", authResponse.getFirstName())
                .apply();
    }

    public String getToken() {
       return preferences.getString(KeyWords.JWT_TOKEN.getWord(), null);
    }

    public Long getUserId() {
        return preferences.getLong(KeyWords.USER_ID.getWord(), -1);
    }

    public String getUserName() {
        return preferences.getString("user_name", "User_Name");
    }

    public String getUserEmail() {
        return preferences.getString("user_email", "mail@example.ru");
    }

    public void logout(Context context) {
        preferences.edit().clear().apply();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finishAffinity();
        }
    }
}
