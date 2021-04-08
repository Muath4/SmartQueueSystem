package com.example.myapplication.objects;


public class Customer {
    private String userId;
    private String username;
    private String email;
    private String currentQueueId;
    private String currentQueueNumber;
    private String currentBranchId;
    private int numberInQueue;
    private String phoneNumber;
    private boolean notification;
    private boolean activated;
    private int timesTicketCanceled;
    private int timesTicketCompleted;
    private int timesCustomerOutRangeAfterBookTicket;

    public Customer() {
    }

    public Customer(String userId, String username, String email, String phoneNumber) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.currentQueueId =null;
        this.currentBranchId =null;
        this.currentQueueNumber = null;
        notification = false;
        activated = true;
        timesTicketCanceled=0;
        timesTicketCompleted=0;
        timesCustomerOutRangeAfterBookTicket=0;
        numberInQueue=0;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
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

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public int getTimesTicketCanceled() {
        return timesTicketCanceled;
    }

    public void setTimesTicketCanceled(int timesTicketCanceled) {
        this.timesTicketCanceled = timesTicketCanceled;
    }

    public int getTimesTicketCompleted() {
        return timesTicketCompleted;
    }

    public void setTimesTicketCompleted(int timesTicketCompleted) {
        this.timesTicketCompleted = timesTicketCompleted;
    }

    public int getTimesCustomerOutRangeAfterBookTicket() {
        return timesCustomerOutRangeAfterBookTicket;
    }

    public void setTimesCustomerOutRangeAfterBookTicket(int timesCustomerOutRangeAfterBookTicket) {
        this.timesCustomerOutRangeAfterBookTicket = timesCustomerOutRangeAfterBookTicket;
    }

    public int getNumberInQueue() {
        return numberInQueue;
    }

    public void setNumberInQueue(int numberInQueue) {
        this.numberInQueue = numberInQueue;
    }
}