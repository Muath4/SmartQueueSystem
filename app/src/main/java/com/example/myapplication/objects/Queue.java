package com.example.myapplication.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Queue implements Serializable {
    private String QueueID;
    private String BranchID;
    private String QueueName;
    private boolean isQueueRun;
    private int lastCustomerNumber;
    private Map<String, Object> customerListUpdates = new HashMap<>();
// فيه ميثود جديدة اضفتها هي ResetQueue
    // الميثودات Edit and Delete ماراح نسويها الحين

//    FirebaseDatabase Root;
//    DatabaseReference RootReference ;

    public Queue(){

    }

    public Queue(String queueID, String branchId , String queueName) {
        QueueID = queueID;
        BranchID = branchId;
        QueueName = queueName;
        lastCustomerNumber = 0;
        isQueueRun = true;
    }




    public String getQueueID() {
        return QueueID;
    }

    public String getBranchID() {
        return BranchID;
    }

    public String getQueueName() {
        return QueueName;
    }



    public void setQueueID(String queueID) {
        QueueID = queueID;
    }

    public void setBranchID(String branchID) {
        BranchID = branchID;
    }

    public void setQueueName(String queueName) {
        QueueName = queueName;
    }


//    public void add(Queue NewQueue){
//        NewQueue.setQueueStatus(false);
//        Root = FirebaseDatabase.getInstance();
//        RootReference = Root.getReference("Queue");
//        RootReference.child(String.valueOf(NewQueue.BranchID)).setValue(NewQueue);
//    }

    // Start()...

//    public void resumeQueue(int FK){
//        HashMap hash = new HashMap();
//        hash.put("QueueStatus","true");
//        RootReference = FirebaseDatabase.getInstance().getReference().child(String.valueOf(FK));
//        RootReference.updateChildren(hash);
//    }
//
//    public void StopQueue(int FK){
//        HashMap hash = new HashMap();
//        hash.put("QueueStatus","false");
//        RootReference = FirebaseDatabase.getInstance().getReference().child(String.valueOf(FK));
//        RootReference.updateChildren(hash);
//    }

//
    public Map<String, Object> getCustomerListUpdates() {
        return customerListUpdates;
    }

    public void setCustomerListUpdates(Map<String, Object> customerListUpdates) {
        this.customerListUpdates = customerListUpdates;
    }



    public int getLastCustomerNumber() {
        return lastCustomerNumber;
    }

    public void setLastCustomerNumber(int lastCustomerNumber) {
        this.lastCustomerNumber = lastCustomerNumber;
    }

    public boolean isQueueRun() {
        return isQueueRun;
    }

    public void setQueueRun(boolean queueRun) {
        isQueueRun = queueRun;
    }
}
