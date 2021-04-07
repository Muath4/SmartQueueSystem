package com.example.myapplication.objects;

import java.io.Serializable;

import static com.example.myapplication.activities.MainLoadingPage.QUEUE1;

public class Branch implements Serializable {
    private String branchID;
    private String branchName;
    private double latitude;
    private double longitude;
    private int numberOfQueues;
    private float radius;
    private String companyID;
    private Queue queue1,queue2;///
    private String adminEmail;
    private boolean activated;

    public Branch(){

    }

    public Branch(String branchID, String branchName, double latitude, double longitude, float radius, int numberOfQueues, String companyID, Queue queue1, Queue queue2,String adminEmail) {
        this.branchID = branchID;
        this.branchName = branchName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numberOfQueues = numberOfQueues;
        this.companyID =companyID;
        this.radius = radius;
        this.queue1 = queue1;
        this.queue2 = queue2;
        this.adminEmail = adminEmail;
        activated = true;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setNumberOfQueues(int numberOfQueues) {
        this.numberOfQueues = numberOfQueues;
    }

    public String getBranchID() {
        return branchID;
    }

    public String getBranchName() {
        return branchName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getNumberOfQueues() {
        return numberOfQueues;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Queue getQueue1() {
        return queue1;
    }

    public void setQueue1(Queue queue1) {
        this.queue1 = queue1;
    }

    public Queue getQueue2() {
        return queue2;
    }

    public void setQueue2(Queue queue2) {
        this.queue2 = queue2;
    }

    public Queue getQueueByNumber(String queueNumber){
        if(queueNumber.equals(QUEUE1))
            return queue1;
        else
            return queue2;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
