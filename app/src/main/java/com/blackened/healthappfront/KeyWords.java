package com.blackened.healthappfront;

public enum KeyWords {
    CANCEL("Отмена"),
    SEND("Отправить"),
    APP_PREFS("app_prefs"),
    JWT_TOKEN("jwt_token"),
    USER_ID("user_id"),
    DATE_FROM("Дата от: "),
    DATE_TO("Дата по: "),
    AUTHORIZATION("Authorization"),
    APPLICATION_JSON("application/json"),
    BEARER("Bearer ");

    private final String word;

    KeyWords(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
