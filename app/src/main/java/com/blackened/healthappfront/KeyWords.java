package com.blackened.healthappfront;

public enum KeyWords {

    APP_PREFS("app_prefs"),
    JWT_TOKEN("jwt_token"),
    USER_ID("user_id"),
    DATE_FROM("Дата от: "),
    DATE_TO("Дата по: "),
    AUTHORIZATION("Authorization"),
    BEARER("Bearer ");

    private final String word;

    KeyWords(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
