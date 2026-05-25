package com.blackened.healthappfront.familyInvitation;

public class CreateFamilyRequest {
    private String secretCode;
    private String adminEmail;

    public CreateFamilyRequest(String secretCode, String adminEmail) {
        this.secretCode = secretCode;
        this.adminEmail = adminEmail;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
}
