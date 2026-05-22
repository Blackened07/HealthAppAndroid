package com.blackened.healthappfront;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences preferences;

    public SessionManager(Context context) {
        this.preferences = context.getSharedPreferences(KeyWords.APP_PREFS.getWord(), MODE_PRIVATE);
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
        context.startActivity(new Intent(context, MainActivity.class));
        //finish();
    }
}
