package com.example.myapplication.activities.branch_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainLoadingPage;
import com.example.myapplication.objects.Branch;
import com.example.myapplication.objects.BranchAdmin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_ADMIN;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_ID;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE1;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE2;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE_NAME;

public class BranchAdminActivity extends AppCompatActivity {

    private final FirebaseDatabase Root = FirebaseDatabase.getInstance();
    private final DatabaseReference BranchReference = Root.getReference(BRANCH);
    private final DatabaseReference AdminReference = Root.getReference(BRANCH_ADMIN);
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private LinearLayout linearLayout;
    private ContentLoadingProgressBar progressBar;
    private BranchAdmin branchAdmin;
    private Branch branch;
    private Button queue1,queue2,logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_admin);


        getSupportActionBar().hide();
        setView();
        loadBranch();


    }

    private void setView() {
        queue1 = findViewById(R.id.branch_admin_queue1_name);
        queue2 = findViewById(R.id.branch_admin_queue2_name);
        logout = findViewById(R.id.log_out_button_branch_admin);
        logout.setOnClickListener(t->{
            firebaseAuth.signOut();
            startActivity(new Intent(this, MainLoadingPage.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
        progressBar = findViewById(R.id.progress_bar_branch_admin);
        linearLayout = findViewById(R.id.linearLayout_branch_admin);
    }

    private void loadBranch() {
        AdminReference.child(firebaseAuth.getUid()).get()
                .addOnSuccessListener(t-> {
                    branchAdmin = t.getValue(BranchAdmin.class);
                    BranchReference.child(branchAdmin.getBranchID()).get()
                            .addOnSuccessListener(t2-> {
                                branch = t2.getValue(Branch.class);
                                setTitle(branch.getBranchName());
                                setQueue();
                            });
                })
                .addOnFailureListener(t-> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                });



    }

    private void setQueue() {
        queue1.setText(branch.getQueue1().getQueueName());
        queue1.setOnClickListener(t->{
            Intent intent = new Intent(this,ManageQueueActivity.class);
            intent.putExtra(BRANCH_ID, branch.getBranchID());
            intent.putExtra(QUEUE,QUEUE1);
            intent.putExtra(QUEUE_NAME,branch.getQueue1().getQueueName());
            startActivity(intent);
        });
        if (branch.getQueue2() != null){
            queue2.setText(branch.getQueue2().getQueueName());
            queue2.setVisibility(View.VISIBLE);
            queue2.setOnClickListener(t->{
                Intent intent = new Intent(this,ManageQueueActivity.class);
                intent.putExtra(BRANCH_ID, branch.getBranchID());
                intent.putExtra(QUEUE,QUEUE2);
                intent.putExtra(QUEUE_NAME,branch.getQueue2().getQueueName());
                startActivity(intent);
            });
        }
        getSupportActionBar().show();
        linearLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}