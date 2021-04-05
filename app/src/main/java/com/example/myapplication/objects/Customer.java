package com.example.myapplication.objects;


public class Customer {
    private String userId;
    private String username;
    private String email;
    private String currentQueueId;
    private String currentQueueNumber;
    private String currentBranchId;
    private int phoneNumber;
    private boolean notification;

    public Customer() {
    }

    public Customer(String userId, String username, String email, int phoneNumber) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.currentQueueId =null;
        this.currentBranchId =null;
        this.currentQueueNumber = null;
        notification = false;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCurrentQueueId() {
        return currentQueueId;
    }

    public void setCurrentQueueId(String currentQueueId) {
        this.currentQueueId = currentQueueId;
    }

    public String getCurrentBranchId() {
        return currentBranchId;
    }

    public void setCurrentBranchId(String currentBranchId) {
        this.currentBranchId = currentBranchId;
    }

    public String getCurrentQueueNumber() {
        return currentQueueNumber;
    }

    public void setCurrentQueueNumber(String currentQueueNumber) {
        this.currentQueueNumber = currentQueueNumber;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}