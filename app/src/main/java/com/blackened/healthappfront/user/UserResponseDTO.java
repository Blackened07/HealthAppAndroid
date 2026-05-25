package com.blackened.healthappfront.user;

import java.time.LocalDateTime;

public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String familyRole;
    private LocalDateTime lastActivity;

    public UserResponseDTO(Long id, String email, String firstName, String familyRole, LocalDateTime lastActivity) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.familyRole = familyRole;
        this.lastActivity = lastActivity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFamilyRole() {
        return familyRole;
    }

    public void setFamilyRole(String familyRole) {
        this.familyRole = familyRole;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
}
