package com.blackened.healthappfront.auth;

public class AuthResponse {

    private String message;
    private Long userId;
    private String firstName;
    private boolean success;

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
