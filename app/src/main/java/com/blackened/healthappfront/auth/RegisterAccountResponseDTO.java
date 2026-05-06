package com.blackened.healthappfront.auth;
//ПРИ РЕГИСТРАЦИИ ОТПРАВЛЯЕТСЯ С СЕРВЕРА!
public class RegisterAccountResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String familyRole;
    private String lastActivity;

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFamilyRole() {
        return familyRole;
    }

    public String getLastActivity() {
        return lastActivity;
    }
}
