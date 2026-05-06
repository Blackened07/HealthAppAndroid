package com.blackened.healthappfront.auth;

public class RegisterAccountRequest {
    private final String email;
    private final String password;
    private final String firstName;
    private final String systemRole;

    public RegisterAccountRequest(String email, String password, String firstName, String systemRole) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.systemRole = systemRole;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstName;
    }

    public String getSystemRole() {
        return systemRole;
    }
}
