package com.example.myapplication.activities.branch_manager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import static com.example.myapplication.activities.MainLoadingPage.IS_QUEUE_RUN;
import static com.example.myapplication.activities.MainLoadingPage.NOTIFICATION;
import static com.example.myapplication.activities.MainLoadingPage.NUMBER_IN_QUEUE;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE_ID;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE_NAME;
import static com.example.myapplication.activities.MainLoadingPage.STATISTIC;
import static com.example.myapplication.activities.MainLoadingPage.TIMES_TICKET_CANCELED;
import static com.example.myapplication.activities.MainLoadingPage.TIMES_TICKET_COMPLETED;

public class ManageQueueActivity extends AppCompatActivity {

    private final FirebaseDatabase Root = FirebaseDatabase.getInstance();
    private final DatabaseReference BranchReference = Root.getReference(BRANCH);
    private final DatabaseReference CustomerReference = Root.getReference(CUSTOMER);

    private FirebaseRecyclerAdapter<String, CustomerDetailHolder> firebaseRecyclerAdapter;

    private ContentLoadingProgressBar progressBar;
    private TextView numberInQueue;
    private String branchId;
    private String queueNumber,queueName,queueId;
    private Button callNext, statusQueueButton, resetQueueButton;
    private String firstCustomerId;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_queue);

        queueName = getIntent().getExtras().getString(QUEUE_NAME);
        branchId = getIntent().getExtras().getString(BRANCH_ID);
        queueNumber = getIntent().getExtras().getString(QUEUE);
        getSupportActionBar().setTitle(queueName);
        setViews();
        progressBar.setVisibility(View.VISIBLE);
        BranchReference.child(branchId).child(queueNumber).child(QUEUE_ID).get().addOnSuccessListener(t->{
            queueId = String.valueOf(t.getValue());

            loadingCustomerInQueue();
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setViews() {
        progressBar = findViewById(R.id.progress_bar_manage_queue);
        numberInQueue = findViewById(R.id.number_in_queue);
        callNext = findViewById(R.id.call_next);
        statusQueueButton = findViewById(R.id.stop_queue);
        resetQueueButton = findViewById(R.id.reset_queue);
        callNext.setOnClickListener(t-> callNextCustomer(firstCustomerId));
        statusQueueButton.setOnClickListener(t->changeQueueState());
        resetQueueButton.setOnClickListener(t->resetQueue());

        //set button text
        BranchReference.child(branchId).child(queueNumber).child(IS_QUEUE_RUN).get()
                .addOnSuccessListener(t->{
                    if(t.getValue() == null)
                        return;
                    boolean isQueueRun = t.getValue(Boolean.TYPE);
                    if(isQueueRun)
                        statusQueueButton.setText(R.string.stop);
                    else
                        statusQueueButton.setText(R.string.start);
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void resetQueue() {
        BranchReference.child(branchId).child(queueNumber).child(CUSTOMER_ID_LIST).removeValue();
        CustomerReference.orderByChild(CURRENT_BRANCH_ID).equalTo(branchId).get().addOnSuccessListener(t->{

            setTimesTicketCanceled((int) t.getChildrenCount());
           t.getChildren().forEach(t2->{
               if(t2.getValue()==null)
                   return;
               String customerId = t2.getValue(Customer.class).getUserId();
               CustomerReference.child(customerId).child(CURRENT_BRANCH_ID).removeValue();
               CustomerReference.child(customerId).child(CURRENT_QUEUE_ID).removeValue();
               CustomerReference.child(customerId).child(CURRENT_QUEUE_NUMBER).removeValue();
               CustomerReference.child(customerId).child(NUMBER_IN_QUEUE).removeValue();
           });
        });

    }

    private void setTimesTicketCanceled(int childrenCount) {
        Root.getReference().child(STATISTIC).child(TIMES_TICKET_CANCELED).get().addOnSuccessListener(t-> {
            if(t.getValue()==null)
                Root.getReference().child(STATISTIC).child(TIMES_TICKET_CANCELED).setValue(childrenCount);
            else
                Root.getReference().child(STATISTIC).child(TIMES_TICKET_CANCELED).setValue(t.getValue(Integer.TYPE) + childrenCount);
        });
    }

    private void changeQueueState() {
        Log.d("^%$%", String.valueOf("changeQueueState"));
        BranchReference.child(branchId).child(queueNumber).child(IS_QUEUE_RUN).get()
                .addOnSuccessListener(t->{
                    if(t.getValue() == null) {
                        BranchReference.child(branchId).child(queueNumber).child(IS_QUEUE_RUN).setValue(true);
                        statusQueueButton.setText(R.string.stop);
                        return;
                    }

                    boolean isQueueRun = t.getValue(Boolean.TYPE); //true

                    if(isQueueRun)
                        statusQueueButton.setText(R.string.start);
                    else
                        statusQueueButton.setText(R.string.stop);
                    BranchReference.child(branchId).child(queueNumber).child(IS_QUEUE_RUN).setValue(!isQueueRun);

                });
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

    private void loadingCustomerInQueue() {
//        if(firebaseAuth.getCurrentUser() == null){
//            startActivity(new Intent(this, LoginPageActivity.class));
//            return;
//        }



//        BranchReference.child(branchId).child(queueNumber).child(CUSTOMER_ID_LIST).get()
//                .addOnSuccessListener(t-> t.getChildren().forEach(t2->Log.d("***",t2.getKey().toString())) );
        Query query = BranchReference.child(branchId).child(queueNumber).child(CUSTOMER_ID_LIST);

//        BranchReference.child(branchId).child(queueNumber).get().addOnSuccessListener(t->Log.d("$#@%@",String.valueOf(t.getValue())));


        RecyclerView recyclerView = findViewById(R.id.customer_list_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<String> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(query, String.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<String, CustomerDetailHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CustomerDetailHolder customerDetailHolder, int position, @NonNull String customer) {
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
            public String getItem(int position) {
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
                if(getItemCount() == 0) {
                    progressBar.setVisibility(View.GONE);
                    firstCustomerId = null;
                }
                else{
                    firstCustomerId = getItem(0);
                    Log.d("%$^$",firstCustomerId);
                }


            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }



    public class CustomerDetailHolder extends RecyclerView.ViewHolder {
        private final TextView phone;
        private final TextView name;
        private final Button call,notification;

        public CustomerDetailHolder(View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.item_customer_phone);
            name = itemView.findViewById(R.id.item_customer_name);
            call = itemView.findViewById(R.id.call_button_manage_queue_item);
            notification = itemView.findViewById(R.id.send_notification_button_item);
        }



        public void setCustomer(String s) {
            Log.d("##%#",s);
            CustomerReference.child(s).get()
                    .addOnSuccessListener(t->{
                        Customer customer = t.getValue(Customer.class);

            String customerPhone = String.valueOf(customer.getPhoneNumber());
            String CustomerName = customer.getUsername();
            phone.setText(customerPhone);
            name.setText(String.valueOf(CustomerName));
            call.setOnClickListener(t2->{
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+customer.getPhoneNumber()));
                startActivity(callIntent);
            });
//            remove.setOnClickListener(t->callNextCustomer(customer.getUserId()));
            notification.setOnClickListener(t3->sendNotification(customer.getUserId()));
                    });
        }
    }

    private void sendNotification(String customerId) {
        CustomerReference.child(customerId).child(NOTIFICATION).setValue(false);
        CustomerReference.child(customerId).child(NOTIFICATION).setValue(true);
    }

    String customerNumberInQueue = null;
    private void callNextCustomer(String customerId) {
        if(firstCustomerId == null)
            return;


        CustomerReference.child(customerId).child(CURRENT_QUEUE_ID).removeValue();
        CustomerReference.child(customerId).child(CURRENT_BRANCH_ID).removeValue();
        CustomerReference.child(customerId).child(CURRENT_QUEUE_NUMBER).removeValue();
        CustomerReference.child(customerId).child(NUMBER_IN_QUEUE).get()
        .addOnSuccessListener(t->{
            customerNumberInQueue = String.valueOf(t.getValue());
            t.getRef().removeValue();
            Map<String, Object> customerList = new HashMap<>();
            customerList.put(customerNumberInQueue,null);
            BranchReference.child(branchId).child(queueNumber).child(CUSTOMER_ID_LIST).updateChildren(customerList);
        });




        ticketCompleted(customerId);




    }

    private void ticketCompleted(String customerId){
        CustomerReference.child(customerId).child(TIMES_TICKET_COMPLETED).get().addOnSuccessListener(t-> {
            if(t.getValue()==null)
                CustomerReference.child(customerId).child(TIMES_TICKET_COMPLETED).setValue(1);
            else
                CustomerReference.child(customerId).child(TIMES_TICKET_COMPLETED).setValue(t.getValue(Integer.TYPE) + 1);
        });

        Root.getReference().child(STATISTIC).child(TIMES_TICKET_COMPLETED).get().addOnSuccessListener(t-> {
            if(t.getValue()==null)
                Root.getReference().child(STATISTIC).child(TIMES_TICKET_COMPLETED).setValue(1);
            else
                Root.getReference().child(STATISTIC).child(TIMES_TICKET_COMPLETED).setValue(t.getValue(Integer.TYPE) + 1);
        });
    }


}