package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.UserMethods;
import com.example.myapplication.activities.company.CompanyBranchesActivity;
import com.example.myapplication.objects.BranchAdmin;
import com.example.myapplication.objects.Company;
import com.example.myapplication.objects.Customer;
import com.google.firebase.auth.FirebaseAuth;


public class MainLoadingPage extends AppCompatActivity {

    //final variables
    public final static String COMPANY = "Company";
    public final static String CUSTOMER = "Customer";
    public final static String USER_NAME = "username";
    public final static String PHONE_NUMBER = "phoneNumber";
    public final static String BRANCH = "Branch";
    public final static String BRANCH_ADMIN = "branchAdmin";
    public final static String BRANCH_ID = "branchID";
    public final static String QUEUE = "Queue";
    public final static String QUEUE_NAME = "queueName";
    public final static String QUEUE1 = "queue1";
    public final static String QUEUE2 = "queue2";
    public final static String CUSTOMER_ID_LIST = "customerListUpdates";
    public final static String USER_TYPE = "UserType";
    public final static String USER_ID = "userId";
    public final static String QUEUE_ID = "queueID";
    public final static String BRANCH_CLASS = "branchClass";
    public final static String Company_Control = "companyClass";
    public final static String PANEL = "Panel";
    public final static String CURRENT_QUEUE = "currentQueue";
    public final static String CURRENT_QUEUE_ID = "currentQueueId";
    public final static String CURRENT_QUEUE_NUMBER = "currentQueueNumber";
    public final static String CURRENT_BRANCH_ID = "currentBranchId";
    public final static String NOTIFICATION = "notification";
    public final static String ACTIVATED = "activated";
    public final static String EMAIL = "email";
    public final static String NUMBER_OF_BRANCHES = "numberOfBranches";
    public final static String NUMBER_IN_QUEUE = "numberInQueue";
    public final static String COMPANY_NAME = "company_name";
    public final static String IS_QUEUE_RUN = "isQueueRun";
    public final static String LAST_CUSTOMER_NUMBER = "lastCustomerNumber";
    public final static String STATISTIC = "statistic";
    public final static String TIMES_TICKET_CANCELED = "timesTicketCanceled";
    public final static String TIMES_TICKET_COMPLETED = "timesTicketCompleted";
    public final static String TIMES_CUSTOMER_OUT_RANGE = "timesCustomerOutRangeAfterBookTicket";
    public final static String TOTAL_NUMBER_OF_QUEUES = "totalNumberOfQueues";
    public final static String AVERAGE_WAITING_TIME_OF_ALL_QUEUES = "averageWaitingTimeOfAllQueues";

    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longitude";
    public final static String RADIUS = "radius";
    public final static String COMPANY_ID = "companyID";
    public final static String HOME_FRAGMENT = "homeFragment";
    public final static String BRANCH_NAME = "branchName";

//    public static Customer customer = null;
//    public static Company company = null;
//    public static BranchAdmin branchAdmin = null;




    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_loading_page);
     // startActivity(new Intent(getApplicationContext(), CompanyBranchesActivity.class));

        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() != null)
            UserMethods.determineUserTypeAndMoveToCorrectPage(this);
        else{
            finish();
            startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
        }

    }



}