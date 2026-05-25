package com.blackened.healthappfront.familyInvitation;

public class FamilyResponseDTO {
    private boolean isNoFamily;
    private Long familyId;
    private String familyName;
    private String familyRole;

    public FamilyResponseDTO(boolean isNoFamily) {
        this.isNoFamily = isNoFamily;
    }

    public boolean isNoFamily() {
        return isNoFamily;
    }

    public void setNoFamily(boolean noFamily) {
        isNoFamily = noFamily;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyRole() {
        return familyRole;
    }

    public void setFamilyRole(String familyRole) {
        this.familyRole = familyRole;
    }

    public String getDisplayableRole() {

        String role = "";

        switch(familyRole) {
            case "ADMIN" : role = "Админ";
            break;
            case "MEMBER" : role = "Член семьи";
            break;
            default: role = "unknown";
        }

        return role;
    }
}
