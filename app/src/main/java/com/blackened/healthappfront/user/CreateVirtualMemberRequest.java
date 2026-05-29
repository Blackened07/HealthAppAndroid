package com.blackened.healthappfront.user;

public class CreateVirtualMemberRequest {
    private String firstName;

    public CreateVirtualMemberRequest(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
