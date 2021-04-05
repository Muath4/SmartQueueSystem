package com.example.myapplication.objects;

public class BranchAdmin {
    private String userId;
    private String email;
    private String branchID;

    public BranchAdmin(String userId, String email, String branchID) {
        this.userId = userId;
        this.email = email;
        this.branchID = branchID;
    }

    public BranchAdmin() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBranchID() {
        return branchID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }
}
