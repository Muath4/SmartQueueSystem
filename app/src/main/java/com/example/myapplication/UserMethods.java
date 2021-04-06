package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.activities.DeletedAccountPageActivity;
import com.example.myapplication.activities.branch_manager.BranchAdminActivity;
import com.example.myapplication.activities.company.CompanyBranchesActivity;
import com.example.myapplication.activities.admin.Admin_Activity;
import com.example.myapplication.activities.customer.CustomerInterfaceActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.myapplication.activities.MainLoadingPage.ACTIVATED;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_ADMIN;
import static com.example.myapplication.activities.MainLoadingPage.PANEL;

import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.USER_ID;

public class UserMethods {
    private static int errorTimesFlag = 0;
    private final static FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final static FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
    private static int userTypeNumber = 0; //customer = 0, company = 1, branch admin = 2, panel = 3
    private static String userType = null;

    public static void determineUserTypeAndMoveToCorrectPage(Activity activity){
        determineUser(activity);
    }

    private static void determineUser(Activity activity){
        switch (userTypeNumber) {
            case 0:
                userType = CUSTOMER;
                break;
            case 1:
                userType = COMPANY;
                break;
            case 2:
                userType = BRANCH_ADMIN;
                break;
            case 3:
                userType = PANEL;
                break;
            default:
                fAuth.signOut();
                activity.startActivity(new Intent(activity.getApplicationContext(), DeletedAccountPageActivity.class));
                activity.finish();
                return;
        }

        DatabaseReference typeRef = firebaseDatabase.getReference().child(userType);
        typeRef.orderByChild(USER_ID).equalTo(fAuth.getUid()).get()
                .addOnFailureListener(task -> {
                    if(++errorTimesFlag >=3) {
                        Toast.makeText(activity.getApplicationContext(), task.getMessage(), Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                    else
                        determineUser(activity);
                })
                .addOnSuccessListener(task -> {
                    errorTimesFlag = 0;
                    if(task.getValue() != null) {
                        userTypeNumber = 0;
                        if(!String.valueOf(task.child(fAuth.getUid()).child(ACTIVATED).getValue()).equals("true")) {
                            fAuth.signOut();
                            activity.startActivity(new Intent(activity.getApplicationContext(), DeletedAccountPageActivity.class));
                            activity.finish();
                        }else
                            moveToCorrectPage(activity);
                    }
                    else{
                        userTypeNumber++;
                        determineUser(activity);
                    }

                });
    }

    private static void moveToCorrectPage(Activity activity){
        Intent intent = null;
        switch (userType) {
            case CUSTOMER:
                intent = new Intent(activity.getApplicationContext(), CustomerInterfaceActivity.class);
                break;
            case COMPANY:
                intent = new Intent(activity.getApplicationContext(), CompanyBranchesActivity.class);
                break;
            case BRANCH_ADMIN:
                intent = new Intent(activity.getApplicationContext(), BranchAdminActivity.class);
                break;
            case PANEL:
                intent = new Intent(activity.getApplicationContext(), Admin_Activity.class);
                break;
            default:
                Toast.makeText(activity.getApplicationContext(), "ERROR_948", Toast.LENGTH_SHORT).show();
                activity.finish();
                break;
        }

        activity.startActivity(intent);
        activity.finish();
    }
}
