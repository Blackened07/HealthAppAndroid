package com.blackened.healthappfront.familyInvitation;

public class FamilyInvitationResponseDTO {
    private String secretCode;

    public FamilyInvitationResponseDTO(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}
