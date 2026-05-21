package com.blackened.healthappfront.familyInvitation;

public class FamilyInvitationRequestDTO {

    private final String invitedUserEmail;
    private final String familyName;

    public FamilyInvitationRequestDTO(String invitedUserEmail, String familyName) {
        this.invitedUserEmail = invitedUserEmail;
        this.familyName = familyName;
    }

    public String getInvitedUserEmail() {
        return invitedUserEmail;
    }

    public String getFamilyName() {
        return familyName;
    }
}
