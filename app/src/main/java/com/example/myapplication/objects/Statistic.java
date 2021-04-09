package com.example.myapplication.objects;

import java.util.Date;

public class Statistic {
    private int timesTicketCanceled;
    private int timesTicketCompleted;
    private int timesCustomerOutRangeAfterBookTicket;
//    private int totalNumberOfQueues;
//    private Date averageWaitingTimeOfAllQueue;


    public Statistic() {
    }

    public Statistic(int timesTicketCanceled, int timesTicketCompleted, int timesCustomerOutRangeAfterBookTicket) {
        this.timesTicketCanceled = timesTicketCanceled;
        this.timesTicketCompleted = timesTicketCompleted;
        this.timesCustomerOutRangeAfterBookTicket = timesCustomerOutRangeAfterBookTicket;
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
}
