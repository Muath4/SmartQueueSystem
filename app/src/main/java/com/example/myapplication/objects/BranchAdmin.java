package com.example.myapplication.objects;

public class BranchAdmin {
    private String userId;
    private String email;
    private String branchID;
    private String companyID;
    private boolean activated;

    public BranchAdmin(String userId, String email, String branchID,String companyID) {
        this.userId = userId;
        this.email = email;
        this.branchID = branchID;
        activated = true;
        this.companyID = companyID;
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
    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }
}
