package com.example.myapplication.activities.customer.ui.ticket;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.activities.customer.ui.home.CompanyFragment;
import com.example.myapplication.objects.Branch;
import com.example.myapplication.objects.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_BRANCH_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_NUMBER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER_ID_LIST;
import static com.example.myapplication.activities.MainLoadingPage.NUMBER_IN_QUEUE;
import static com.example.myapplication.activities.MainLoadingPage.STATISTIC;
import static com.example.myapplication.activities.MainLoadingPage.TIMES_TICKET_CANCELED;
import static com.example.myapplication.activities.MainLoadingPage.TIME_TICKET_BOOKED;

public class TicketFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static View root;
    private TextView notHaveTicketView,ticketBranchName,TicketQueueName,numberBeforeYou;
    private ConstraintLayout ticketConstraintLayout;
    private TicketViewModel ticketViewModel;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    Customer customer;
    private Button deleteTicket;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ticketViewModel =
                new ViewModelProvider(this).get(TicketViewModel.class);
        root = inflater.inflate(R.layout.fragment_ticket, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_ticket);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        setViews();
        setTicket();
        backButtonBehavior();

        /*final TextView textView = root.findViewById(R.id.text_dashboard);
        ticketViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        return root;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setViews() {
        notHaveTicketView = root.findViewById(R.id.do_have_ticket);
        ticketBranchName = root.findViewById(R.id.ticket_branch_name);
        TicketQueueName = root.findViewById(R.id.ticket_queue_name);
        ticketConstraintLayout = root.findViewById(R.id.ticket_info_layout);
        contentLoadingProgressBar = root.findViewById(R.id.progress_bar_ticket);
        numberBeforeYou = root.findViewById(R.id.ticket_queue_number_in_queue);
        deleteTicket = root.findViewById(R.id.delete_ticket_customer);
        deleteTicket.setOnClickListener(t-> ticketCanceled());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void ticketCanceled() {
        Map<String, Object> customerList = new HashMap<>();
        customerList.put(String.valueOf(customer.getNumberInQueue()) ,null);
        rootRef.child(BRANCH).child(customer.getCurrentBranchId()).child(customer.getCurrentQueueNumber()).child(CUSTOMER_ID_LIST).updateChildren(customerList);
        rootRef.child(CUSTOMER).child(customer.getUserId()).child(CURRENT_QUEUE_ID).removeValue();
        rootRef.child(CUSTOMER).child(customer.getUserId()).child(CURRENT_QUEUE_NUMBER).removeValue();
        rootRef.child(CUSTOMER).child(customer.getUserId()).child(CURRENT_BRANCH_ID).removeValue();
        rootRef.child(CUSTOMER).child(customer.getUserId()).child(NUMBER_IN_QUEUE).removeValue();
        rootRef.child(CUSTOMER).child(customer.getUserId()).child(TIME_TICKET_BOOKED).removeValue();
        setTimesTicketCanceled();
        setTicket();
    }

    private void setTimesTicketCanceled() {
        rootRef.child(CUSTOMER).child(customer.getUserId()).child(TIMES_TICKET_CANCELED).get().addOnSuccessListener(t-> {
            if(t.getValue()==null)
                rootRef.child(CUSTOMER).child(customer.getUserId()).child(TIMES_TICKET_CANCELED).setValue(1);
            else
                rootRef.child(CUSTOMER).child(customer.getUserId()).child(TIMES_TICKET_CANCELED).setValue(t.getValue(Integer.TYPE) + 1);
        });

        rootRef.child(STATISTIC).child(TIMES_TICKET_CANCELED).get().addOnSuccessListener(t-> {
            if(t.getValue()==null)
                rootRef.child(STATISTIC).child(TIMES_TICKET_CANCELED).setValue(1);
            else
                rootRef.child(STATISTIC).child(TIMES_TICKET_CANCELED).setValue(t.getValue(Integer.TYPE) + 1);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setTicket() {
        rootRef.child(CUSTOMER).child(firebaseAuth.getUid()).get()
                .addOnSuccessListener(t-> {
                    customer = t.getValue(Customer.class);
                    if(customer.getCurrentQueueId() != null){
                        showTicketDetails();
                    }else{
                        notHaveTicketView.setVisibility(View.VISIBLE);
                        ticketConstraintLayout.setVisibility(View.GONE);
                        contentLoadingProgressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(t-> {
                    contentLoadingProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public static void removeTicket(){
        try{
            TextView notHaveTicketView;
            ContentLoadingProgressBar contentLoadingProgressBar;
            ConstraintLayout ticketConstraintLayout;
            notHaveTicketView = root.findViewById(R.id.do_have_ticket);
            ticketConstraintLayout = root.findViewById(R.id.ticket_info_layout);
            contentLoadingProgressBar = root.findViewById(R.id.progress_bar_ticket);

            notHaveTicketView.setVisibility(View.VISIBLE);
            ticketConstraintLayout.setVisibility(View.GONE);
            contentLoadingProgressBar.setVisibility(View.GONE);
        }catch (Exception ignored){}

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showTicketDetails() {
        rootRef.child(BRANCH).child(customer.getCurrentBranchId()).get()
                .addOnSuccessListener(t-> {
                    Branch branch = t.getValue(Branch.class);
                    ticketBranchName.setText(branch.getBranchName());
                    TicketQueueName.setText(branch.getQueueByNumber(customer.getCurrentQueueNumber()).getQueueName());
                    setNumberBeforeYou(branch);
                    rootRef.child(BRANCH).child(branch.getBranchID()).child(customer.getCurrentQueueNumber()).child(CUSTOMER_ID_LIST).addChildEventListener(new ChildEventListener(){

                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                            setNumberBeforeYou(branch);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.getValue().equals(customer.getUserId()))
                                setNumberBeforeYou(branch);
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    contentLoadingProgressBar.setVisibility(View.INVISIBLE);
                    ticketConstraintLayout.setVisibility(View.VISIBLE);

                    notHaveTicketView.setVisibility(View.GONE);
                });


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    int beforeYou = 0;
    int currentOrderNumber;
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setNumberBeforeYou(Branch branch) {
        try {

            rootRef.child(BRANCH).child(branch.getBranchID()).child(customer.getCurrentQueueNumber()).child(CUSTOMER_ID_LIST).get()
                    .addOnSuccessListener(t->{
                        beforeYou = 0;
                        rootRef.child(BRANCH)
                                .child(branch.getBranchID())
                                .child(customer.getCurrentQueueNumber())
                                .child(CUSTOMER_ID_LIST)
                                .child(String.valueOf(customer.getNumberInQueue()))
                                .get()
                                .addOnSuccessListener(number-> {
                                    if(number.getValue()!=null) {
                                        int indexOf_ = number.getKey().indexOf("_");
                                        String splitNumber = number.getKey().substring(0,indexOf_-1);
                                        currentOrderNumber = Integer.parseInt(splitNumber);

                                        t.getChildren().forEach(t2 -> {
//                            Log.d("&&^^", String.valueOf(currentOrderNumber)+"   " +t2.getValue(Integer.TYPE));
                                            int indexOf_2 = t2.getKey().indexOf("_");
                                            String splitNumber2 = t2.getKey().substring(0,indexOf_-1);
                                            if (Integer.parseInt(splitNumber2) < currentOrderNumber) {

                                                beforeYou++;
                                                Log.d("&&^^", String.valueOf(beforeYou));
                                            }
//                        if(t2.getKey().equals(customer.getUserId())) {


//                        }
//                        else
//                            beforeYou++;
                                            numberBeforeYou.setText(String.valueOf(beforeYou));
                                        });
                                    }
                                });


                    });
        }catch (Exception ignored){}


    }

    private void backButtonBehavior() {
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment,new CompanyFragment())
                            .commit();
                    return true;
                }
                return false;
            }
        } );
    }

}