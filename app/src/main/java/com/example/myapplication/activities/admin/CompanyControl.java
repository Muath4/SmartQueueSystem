package com.example.myapplication.activities.admin;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.GeofenceBroadcastReceiver;
import com.example.myapplication.activities.LoginPageActivity;
import com.example.myapplication.activities.company.AddBranchActivity;
import com.example.myapplication.activities.company.EditBranchActivity;
import com.example.myapplication.objects.Branch;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_CLASS;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_ID;


public class CompanyControl extends AppCompatActivity {
    private FirebaseRecyclerAdapter<Branch, BranchHolder> firebaseRecyclerAdapter;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private RelativeLayout progressBar;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companycontrol);
        setTitle(getString(R.string.branches));
        progressBar = findViewById(R.id.loadingPanel_branches_company);

        loadingBranches();
    }
    @Override
    protected void onStart() {
        super.onStart();



        if(firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    private void loadingBranches() {
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginPageActivity.class));
            return;
        }

//        progressBar.setVisibility(View.VISIBLE);


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query query = rootRef.child(BRANCH);
        //I change the index to branch name for customer interface, I think its better for performance -- Muath


        RecyclerView recyclerView = findViewById(R.id.branches_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Branch> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Branch>()
                .setQuery(query, Branch.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Branch, BranchHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BranchHolder branchHolder, int position, @NonNull Branch branch) {
                branchHolder.setBranch(branch);

              progressBar.setVisibility(View.GONE);
                branchHolder.itemView.setOnClickListener(t ->
                {
                    Intent intent = new Intent(getApplicationContext(), EditBranchActivity.class)
                            .putExtra(BRANCH, getRef(position).toString())
                            .putExtra(BRANCH_CLASS, getItem(position));
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public BranchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_branch_item_company, parent, false);
                return new BranchHolder(view);
            }

            @NonNull
            @Override
            public Branch getItem(int position) {
                return super.getItem(position);
            }

            @NonNull
            @Override
            public DatabaseReference getRef(int position) {
                return super.getRef(position);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount() == 0)
                    progressBar.setVisibility(View.GONE);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public void addBranchButtonClicked(View view) {
        Button move = findViewById(R.id.button_addBranch);
        Intent intent = new Intent(this, AddBranchActivity.class);
        startActivity(intent);
    }


    public void Logout(View view){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(CompanyControl.this, LoginPageActivity.class));
    }


    public static class BranchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView branchID, branchNameTextView,location,companyID, numberOfQueuesTextView,email;

        public BranchHolder(View itemView) {
            super(itemView);
            this.branchNameTextView = itemView.findViewById(R.id.itemBranchName);
            this.numberOfQueuesTextView = itemView.findViewById(R.id.itemBranchQueueNumber);
        }


        @Override
        public void onClick(View view) {

        }
        public void setBranch(Branch branch) {
            String branchName = branch.getBranchName();
            int branchQueueNumber = branch.getNumberOfQueues();
            branchNameTextView.setText(branchName);
            numberOfQueuesTextView.setText(String.valueOf(branchQueueNumber));

        }
    }



}
