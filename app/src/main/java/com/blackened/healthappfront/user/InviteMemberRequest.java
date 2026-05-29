package com.blackened.healthappfront.user;

public class InviteMemberRequest {
    private String email;

    public InviteMemberRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
