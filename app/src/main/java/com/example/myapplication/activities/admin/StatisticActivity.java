package com.example.myapplication.activities.admin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.objects.Branch;
import com.example.myapplication.objects.Statistic;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.STATISTIC;

public class StatisticActivity extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference statisticRef = databaseReference.child(STATISTIC);
    private DatabaseReference branchRef = databaseReference.child(BRANCH);
    private TextView completeTimes,outRangeTimes,cancelTimes,averageTotal,numberOfQueuesTextView,numberOfBranchesTextView,companyNumber,customerNumber;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        setViews();
        getAverageOfAllQueues();

        statisticRef.get()
                .addOnSuccessListener(t->{
                    Statistic statistic = t.getValue(Statistic.class);
                    completeTimes.setText(String.valueOf(statistic.getTimesTicketCompleted()));
                    outRangeTimes.setText(String.valueOf(statistic.getTimesCustomerOutRangeAfterBookTicket()));
                    cancelTimes.setText(String.valueOf(statistic.getTimesTicketCanceled()));
                });
        databaseReference.child(CUSTOMER).get().addOnSuccessListener(t->customerNumber.setText(String.valueOf(t.getChildrenCount())) );
        databaseReference.child(COMPANY).get().addOnSuccessListener(t->companyNumber.setText(String.valueOf(t.getChildrenCount())) );

    }

    int numberOfQueues = 0;
    int numberOfBranches = 0;
    long totalAverage = 0;
    long averageOfAllQueue = 0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getAverageOfAllQueues() {

        branchRef.get().addOnSuccessListener(t->{
           t.getChildren().forEach(t2->{
               numberOfBranches++;
               numberOfQueues++;
               Branch branch = t2.getValue(Branch.class);
               totalAverage += branch.getQueue1().getAverageWaitingTime();
               if(branch.getNumberOfQueues() == 2){
                   numberOfQueues++;
                   totalAverage += branch.getQueue2().getAverageWaitingTime();
               }
           });
            averageOfAllQueue = totalAverage/numberOfQueues;
            SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = new Date(averageOfAllQueue);
            averageTotal.setText(dateFormat.format(date));
            numberOfQueuesTextView.setText(String.valueOf(numberOfQueues));
            numberOfBranchesTextView.setText(String.valueOf(numberOfBranches));
        });
    }

    private void setViews() {
        completeTimes = findViewById(R.id.complete_ticket);
        outRangeTimes = findViewById(R.id.out_range_ticket);
        cancelTimes = findViewById(R.id.cancel_ticket);
        averageTotal = findViewById(R.id.average_waiting_time_of_all_queues);
        numberOfQueuesTextView = findViewById(R.id.queues_number);
        numberOfBranchesTextView = findViewById(R.id.branches_number);
        companyNumber = findViewById(R.id.company_number);
        customerNumber = findViewById(R.id.customer_number);

    }
}