package com.example.myapplication.objects;


import java.io.Serializable;

import static com.example.myapplication.activities.MainLoadingPage.COMPANY;


public class Company implements Serializable {
    private String userId;
    private String company_name;
    private String email;
    private boolean isVerified = false;


    public Company() {
    }

    public Company(String userId, String company_name,  String email) {
        this.userId = userId;
        this.company_name = company_name;
        this.email = email;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
