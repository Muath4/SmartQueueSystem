package com.example.myapplication.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.myapplication.activities.MainLoadingPage.ACTIVATED;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.USER_TYPE;

public class Admin_Activity extends AppCompatActivity {

    private Button manageCompany,manageCustomer,logout;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//    TextView numberCompanies,numberCustomers;
    FirebaseDatabase Root = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_admin);
        setTitle("Admin Page");
//        progressBar = findViewById(R.id.loadingPanel_branches_company);
        manageCompany = findViewById(R.id.manage_company_button);
        manageCustomer = findViewById(R.id.manage_customer_button);
        logout = findViewById(R.id.log_out_button_admin);
//        numberCompanies = findViewById(R.id.number_of_companies);
//        numberCustomers = findViewById(R.id.number_of_customers);
//        setUsersNumbers();
        manageCompany.setOnClickListener(t->startActivity(new Intent(this, UsersListActivity.class).putExtra(USER_TYPE,COMPANY)));
        manageCustomer.setOnClickListener(t->startActivity(new Intent(this, UsersListActivity.class).putExtra(USER_TYPE,CUSTOMER)));
        logout.setOnClickListener(t->{
            firebaseAuth.signOut();
            finish();
        });

    }

    /*private void setUsersNumbers() {
        databaseReference.child(COMPANY).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                databaseReference.child(COMPANY).orderByChild(ACTIVATED).equalTo(true).get()
                        .addOnSuccessListener(t->numberCompanies.setText(String.valueOf(t.getChildrenCount())) );
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                databaseReference.child(COMPANY).orderByChild(ACTIVATED).equalTo(true).get()
                        .addOnSuccessListener(t->numberCompanies.setText(String.valueOf(t.getChildrenCount())) );
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                databaseReference.child(COMPANY).orderByChild(ACTIVATED).equalTo(true).get()
                        .addOnSuccessListener(t->numberCompanies.setText(String.valueOf(t.getChildrenCount())) );
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child(CUSTOMER).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                databaseReference.child(CUSTOMER).orderByChild(ACTIVATED).equalTo(true).get()
                        .addOnSuccessListener(t->numberCustomers.setText(String.valueOf(t.getChildrenCount())) );
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                databaseReference.child(CUSTOMER).orderByChild(ACTIVATED).equalTo(true).get()
                        .addOnSuccessListener(t->numberCustomers.setText(String.valueOf(t.getChildrenCount())) );
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                databaseReference.child(CUSTOMER).orderByChild(ACTIVATED).equalTo(true).get()
                        .addOnSuccessListener(t->numberCustomers.setText(String.valueOf(t.getChildrenCount())) );
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/


}
