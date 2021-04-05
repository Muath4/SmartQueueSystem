package com.example.myapplication.activities.admin;

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
import com.example.myapplication.activities.LoginPageActivity;
import com.example.myapplication.activities.RegisterCompanyActivity;
import com.example.myapplication.objects.Company;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_ID;
import static com.example.myapplication.activities.MainLoadingPage.Company_Control;

public class Admin_Activity extends AppCompatActivity {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private RelativeLayout progressBar;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseRecyclerAdapter<Company, BranchHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_admin);
        setTitle("Admin Page");
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
        Query query = rootRef.child(COMPANY).orderByChild(COMPANY_ID);
        //I change the index to branch name for customer interface, I think its better for performance -- Muath


        RecyclerView recyclerView = findViewById(R.id.branches_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Company> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Company>()
                .setQuery(query, Company.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Company, BranchHolder>(firebaseRecyclerOptions) {


            @Override
            protected void onBindViewHolder(@NonNull BranchHolder branchHolder, int i, @NonNull Company company) {


                branchHolder.setBranch(company);
                progressBar.setVisibility(View.GONE);
                branchHolder.itemView.setOnClickListener(t ->
                {
                    Intent intent = new Intent(getApplicationContext(), CompanyControl.class)
                            .putExtra(COMPANY, getRef(i).toString())
                            .putExtra(Company_Control,  getItem(i));
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public BranchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_panel, parent, false);
                return new BranchHolder(view);
            }

            @NonNull
            @Override
            public Company getItem(int position) {
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
        Intent intent = new Intent(this, RegisterCompanyActivity.class);
        startActivity(intent);
    }





    public static class BranchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView branchID, branchNameTextView,location,companyID, UsernameTextView,BranchEmailTextView;

        public BranchHolder(View itemView) {
            super(itemView);
            this.branchNameTextView = itemView.findViewById(R.id.itemBranchName);
            this.UsernameTextView = itemView.findViewById(R.id.itemBranchQueueNumber);
            this.BranchEmailTextView=itemView.findViewById(R.id.itemBranchEmail);
        }


        @Override
        public void onClick(View view) {

        }
        public void setBranch(Company branch) {
            String companyName = branch.getCompany_name();
            String companyEmail = branch.getEmail();
            String companyUsername=branch.getCompany_name();
            branchNameTextView.setText(companyName);
            UsernameTextView.setText(companyUsername);
            BranchEmailTextView.setText(companyEmail);

        }
    }


    public void Logout(View view){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(Admin_Activity.this, LoginPageActivity.class));
    }





}
