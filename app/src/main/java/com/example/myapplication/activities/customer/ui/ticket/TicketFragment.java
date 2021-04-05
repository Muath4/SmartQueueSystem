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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.activities.customer.ui.home.HomeFragment;
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

public class TicketFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private View root;
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

    private void setViews() {
        notHaveTicketView = root.findViewById(R.id.do_have_ticket);
        ticketBranchName = root.findViewById(R.id.ticket_branch_name);
        TicketQueueName = root.findViewById(R.id.ticket_queue_name);
        ticketConstraintLayout = root.findViewById(R.id.ticket_info_layout);
        contentLoadingProgressBar = root.findViewById(R.id.progress_bar_ticket);
        numberBeforeYou = root.findViewById(R.id.ticket_queue_number_in_queue);
        deleteTicket = root.findViewById(R.id.delete_ticket_customer);
        deleteTicket.setOnClickListener(t->{
            Map<String, Object> customerList = new HashMap<>();
            customerList.put(customer.getUserId(),null);
            rootRef.child(BRANCH).child(customer.getCurrentBranchId()).child(customer.getCurrentQueueNumber()).child(CUSTOMER_ID_LIST).updateChildren(customerList);
            rootRef.child(CUSTOMER).child(customer.getUserId()).child(CURRENT_QUEUE_ID).removeValue();
            rootRef.child(CUSTOMER).child(customer.getUserId()).child(CURRENT_QUEUE_NUMBER).removeValue();
            rootRef.child(CUSTOMER).child(customer.getUserId()).child(CURRENT_BRANCH_ID).removeValue();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment,new TicketFragment())
                    .commit();
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
                            setNumberBeforeYou(branch);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
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
    int beforeYou = 0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setNumberBeforeYou(Branch branch) {

        rootRef.child(BRANCH).child(branch.getBranchID()).child(customer.getCurrentQueueNumber()).child(CUSTOMER_ID_LIST).get()
                .addOnSuccessListener(t->{
                    beforeYou = (int) t.getChildrenCount() - 1;

                    t.getChildren().forEach(t2->{
                        if(t2.getKey().equals(customer.getUserId())) {
                            numberBeforeYou.setText(String.valueOf(beforeYou));
//                            Log.d("&*&^^","setNumberBeforeYou"+ t2.getKey());
                        }
                        else
                            beforeYou--;
                    });
                });

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
                            .replace(R.id.nav_host_fragment,new HomeFragment())
                            .commit();
                    return true;
                }
                return false;
            }
        } );
    }

}