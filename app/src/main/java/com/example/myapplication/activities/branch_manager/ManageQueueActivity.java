package com.example.myapplication.activities.branch_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.objects.Customer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_BRANCH_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_NUMBER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER_ID_LIST;
import static com.example.myapplication.activities.MainLoadingPage.GET_NOTIFICATION;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE_NAME;

public class ManageQueueActivity extends AppCompatActivity {

    private final FirebaseDatabase Root = FirebaseDatabase.getInstance();
    private final DatabaseReference BranchReference = Root.getReference(BRANCH);
    private final DatabaseReference CustomerReference = Root.getReference(CUSTOMER);

    private FirebaseRecyclerAdapter<Customer, CustomerDetailHolder> firebaseRecyclerAdapter;

    private ContentLoadingProgressBar progressBar;
    private TextView numberInQueue;
    private String branchId;
    private String queueNumber,queueName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_queue);

        queueName = getIntent().getExtras().getString(QUEUE_NAME);
        branchId = getIntent().getExtras().getString(BRANCH_ID);
        queueNumber = getIntent().getExtras().getString(QUEUE);
        getSupportActionBar().setTitle(queueName);
        setViews();
        loadingBranches();
    }

    private void setViews() {
        progressBar = findViewById(R.id.progress_bar_manage_queue);
        numberInQueue = findViewById(R.id.number_in_queue);
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
//        if(firebaseAuth.getCurrentUser() == null){
//            startActivity(new Intent(this, LoginPageActivity.class));
//            return;
//        }

        progressBar.setVisibility(View.VISIBLE);

//        BranchReference.child(branchId).child(queueNumber).child(CUSTOMER_ID_LIST).get()
//                .addOnSuccessListener(t-> t.getChildren().forEach(t2->Log.d("***",t2.getKey().toString())) );
        Query query = CustomerReference.orderByChild(CURRENT_BRANCH_ID).equalTo(branchId);


        RecyclerView recyclerView = findViewById(R.id.customer_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Customer> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Customer>()
                .setQuery(query, Customer.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Customer, CustomerDetailHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CustomerDetailHolder customerDetailHolder, int position, @NonNull Customer customer) {
                customerDetailHolder.setCustomer(customer);
                progressBar.setVisibility(View.GONE);
                customerDetailHolder.itemView.setOnClickListener(t ->
                {
//                    Intent intent = new Intent(getApplicationContext(), EditBranchActivity.class)
//                            .putExtra(BRANCH, getRef(position).toString())
//                            .putExtra(BRANCH_CLASS, getItem(position));
//                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public CustomerDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_customer_item_branch_admin, parent, false);
                return new CustomerDetailHolder(view);
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
                numberInQueue.setText(String.valueOf(getItemCount()));
                if(getItemCount() == 0)
                    progressBar.setVisibility(View.GONE);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }



    public class CustomerDetailHolder extends RecyclerView.ViewHolder {
        private final TextView phone;
        private final TextView name;
        private final Button call,remove,notification;

        public CustomerDetailHolder(View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.item_customer_phone);
            name = itemView.findViewById(R.id.item_customer_name);
            call = itemView.findViewById(R.id.call_button_manage_queue_item);
            remove = itemView.findViewById(R.id.remove_customer_button_item);
            notification = itemView.findViewById(R.id.send_notification_button_item);
        }



        public void setCustomer(Customer customer) {
            String customerPhone = String.valueOf(customer.getPhoneNumber());
            String CustomerName = customer.getUsername();
            phone.setText(customerPhone);
            name.setText(String.valueOf(CustomerName));
            call.setOnClickListener(t->{
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+customer.getPhoneNumber()));
                startActivity(callIntent);
            });
            remove.setOnClickListener(t->removeCustomer(customer.getUserId()));
            notification.setOnClickListener(t->sendNotification(customer.getUserId()));

        }
    }

    private void sendNotification(String customerId) {
        CustomerReference.child(customerId).child(GET_NOTIFICATION).setValue(true);
    }

    private void removeCustomer(String customerId) {
        CustomerReference.child(customerId).child(CURRENT_QUEUE_ID).removeValue();
        CustomerReference.child(customerId).child(CURRENT_BRANCH_ID).removeValue();
        CustomerReference.child(customerId).child(CURRENT_QUEUE_NUMBER).removeValue();

        Map<String, Object> customerList = new HashMap<>();
        customerList.put(customerId,null);
        BranchReference.child(branchId).child(queueNumber).child(CUSTOMER_ID_LIST).updateChildren(customerList);

    }


}