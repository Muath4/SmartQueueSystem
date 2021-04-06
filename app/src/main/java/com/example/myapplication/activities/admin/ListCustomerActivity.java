package com.example.myapplication.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.LoginPageActivity;
import com.example.myapplication.objects.Customer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.activities.MainLoadingPage.ACTIVATED;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;

public class ListCustomerActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private RelativeLayout progressBar;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseRecyclerAdapter<Customer, BranchHolder> firebaseRecyclerAdapter;
    private Button deletedUserListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_customer);
        deletedUserListButton = findViewById(R.id.deleted_user_button);
        deletedUserListButton.setOnClickListener(t->startActivity(new Intent(this,DeletedUsersListActivity.class)));
        progressBar = findViewById(R.id.loadingPanel_branches_company);
        loadingCustomers();
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

    private void loadingCustomers() {
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginPageActivity.class));
            return;
        }

//        progressBar.setVisibility(View.VISIBLE);


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query query = rootRef.child(CUSTOMER).orderByChild(ACTIVATED).equalTo(true);
        //I change the index to branch name for customer interface, I think its better for performance -- Muath


        RecyclerView recyclerView = findViewById(R.id.branches_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Customer> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Customer>()
                .setQuery(query, Customer.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Customer, BranchHolder>(firebaseRecyclerOptions) {


            @Override
            protected void onBindViewHolder(@NonNull BranchHolder branchHolder, int i, @NonNull Customer customer) {


                branchHolder.setCustomer(customer);
                progressBar.setVisibility(View.GONE);
                branchHolder.itemView.setOnClickListener(t ->
                {
                    Intent intent = new Intent(getApplicationContext(), editCustomerAdminActivity.class)
                            .putExtra(CUSTOMER, getRef(i).toString());
//                            .putExtra(Company_Control,  getItem(i));
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public BranchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_customer_item_panel, parent, false);
                return new BranchHolder(view);
            }

            @NonNull
            @Override
            public Customer getItem(int position) {
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
//        Button move = findViewById(R.id.button_addBranch);
//        Intent intent = new Intent(this, RegisterCompanyActivity.class);
//        startActivity(intent);
    }





    public static class BranchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView,phoneTextView,emailTextView;
        private Button delete;

        public BranchHolder(View itemView) {
            super(itemView);
            this.nameTextView = itemView.findViewById(R.id.customer_name_item_panel);
            this.phoneTextView = itemView.findViewById(R.id.customer_phone_item_panel);
            this.emailTextView=itemView.findViewById(R.id.customer_email_item_panel);
            delete = itemView.findViewById(R.id.delete_customer_button_item);
        }


        @Override
        public void onClick(View view) {

        }
        public void setCustomer(Customer customer) {
            String name = customer.getUsername();
            String phone=customer.getPhoneNumber();
            String email = customer.getEmail();
            nameTextView.setText(name);
            phoneTextView.setText(phone);
            emailTextView.setText(email);
            delete.setOnClickListener(t->{
                Map<String, Object> update = new HashMap<>();
                update.put(ACTIVATED,false);
                FirebaseDatabase.getInstance().getReference().child(CUSTOMER).child(customer.getUserId()).updateChildren(update);
            });

        }
    }




}