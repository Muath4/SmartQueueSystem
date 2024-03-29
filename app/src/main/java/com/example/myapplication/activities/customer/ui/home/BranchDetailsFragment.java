package com.example.myapplication.activities.customer.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.objects.Branch;
import com.example.myapplication.objects.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static androidx.navigation.Navigation.findNavController;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_CLASS;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_DETAILS_FRAGMENT;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_FRAGMENT;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_FRAGMENT;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_BRANCH_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_NUMBER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER_ID_LIST;
import static com.example.myapplication.activities.MainLoadingPage.IS_QUEUE_RUN;
import static com.example.myapplication.activities.MainLoadingPage.LAST_CUSTOMER_NUMBER;
import static com.example.myapplication.activities.MainLoadingPage.NUMBER_IN_QUEUE;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE1;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE2;
import static com.example.myapplication.activities.MainLoadingPage.TIME_TICKET_BOOKED;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BranchDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BranchDetailsFragment extends Fragment {
//
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = firebaseDatabase.getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Button queueOne;
    Button queueTwo;
    View root;
    Branch branch;
    LinearLayout queue2Layout;
    TextView waitingTimeQueue1,waitingTimeQueue2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public BranchDetailsFragment() {

    }

    public static BranchDetailsFragment newInstance(String param1, String param2) {
        BranchDetailsFragment fragment = new BranchDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_branch_details, container, false);

        Bundle bundle = this.getArguments();
        branch = (Branch) bundle.getSerializable(BRANCH_CLASS);
        queue2Layout = root.findViewById(R.id.layout_queue2_queue_details);
        waitingTimeQueue1 = root.findViewById(R.id.waiting_time_queue1);
        waitingTimeQueue2 = root.findViewById(R.id.waiting_time_queue2);
        TextView branchName = root.findViewById(R.id.branch_name_customer_detail);

        setQueueButtons();

        SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        branchName.setText(branch.getBranchName());

        queueOne.setText(branch.getQueue1().getQueueName());
        if(branch.getQueue1().getAverageWaitingTime() == 0)
            waitingTimeQueue1.setText(getString(R.string.no_info));
        else {
            Date date1 = new Date(branch.getQueue1().getAverageWaitingTime());
            waitingTimeQueue1.setText(dateFormat.format(date1));
        }
        if(branch.getNumberOfQueues()==2) {
            queue2Layout.setVisibility(View.VISIBLE);
            queueTwo.setText(branch.getQueue2().getQueueName());
            if(branch.getQueue2().getAverageWaitingTime() == 0)
                waitingTimeQueue2.setText(getString(R.string.no_info));
            else {
                Date date2 = new Date(branch.getQueue2().getAverageWaitingTime());
                waitingTimeQueue2.setText(dateFormat.format(date2));
            }
        }


        backButtonBehavior();
        //control back button

        return root;
    }



    private void setQueueButtons() {
        queueOne = root.findViewById(R.id.queue_one_customer);
        queueTwo = root.findViewById(R.id.queue_two_customer);
        queueOne.setEnabled(false);
        queueTwo.setEnabled(false);
        listerForQueueStatus();
//        checkQueueStatus();
        queueOne.setOnClickListener(t-> addQueueToDatabase(QUEUE1));

        if(branch.getNumberOfQueues()==2)
            queueTwo.setOnClickListener(t-> addQueueToDatabase(QUEUE2));
//        Log.d("****",String.valueOf(branch.getNumberOfQueues()));
    }

    private void listerForQueueStatus() {
        rootRef.child(BRANCH).child(branch.getBranchID()).child(QUEUE1).child(IS_QUEUE_RUN).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null)
                    queueOne.setEnabled(snapshot.getValue(Boolean.TYPE));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(branch.getNumberOfQueues()==2)
            rootRef.child(BRANCH).child(branch.getBranchID()).child(QUEUE2).child(IS_QUEUE_RUN).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue()!=null)
                        queueTwo.setEnabled(snapshot.getValue(Boolean.TYPE));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }


    int lastCustomerNumber;
    String queueId = null;
    private void addQueueToDatabase(String queueNumber) {
        rootRef.child(BRANCH).child(branch.getBranchID()).child(queueNumber).child(IS_QUEUE_RUN).get()
                .addOnSuccessListener(t0->{
                    if(t0.getValue() == null)
                        return;
                    if(t0.getValue(Boolean.TYPE)){
                        if(queueNumber.equals(QUEUE1))
                            queueId = branch.getQueue1().getQueueID();
                        else
                            queueId = branch.getQueue2().getQueueID();

                        rootRef
                                .child(CUSTOMER)
                                .child(firebaseAuth.getUid())
                                .get()
                                .addOnSuccessListener(t-> {
                                    if(t.getValue(Customer.class).getCurrentQueueId() == null)
                                    {

                                        rootRef.child(BRANCH).child(branch.getBranchID()).child(queueNumber).child(LAST_CUSTOMER_NUMBER).get()
                                                .addOnSuccessListener(tt->{
                                                    if(tt.getValue() == null)
                                                        lastCustomerNumber = 1;
                                                    else
                                                        lastCustomerNumber = tt.getValue(Integer.TYPE) + 1;

                                                    rootRef.child(BRANCH).child(branch.getBranchID()).child(queueNumber).child(LAST_CUSTOMER_NUMBER).setValue(lastCustomerNumber);
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put(String.valueOf(lastCustomerNumber+" _"),firebaseAuth.getUid());
                                                    rootRef.child(BRANCH).child(branch.getBranchID()).child(queueNumber).child(CUSTOMER_ID_LIST).updateChildren(map);
                                                    t.getRef().child(CURRENT_QUEUE_ID).setValue(queueId);
                                                    t.getRef().child(CURRENT_QUEUE_NUMBER).setValue(queueNumber);
                                                    t.getRef().child(CURRENT_BRANCH_ID).setValue(branch.getBranchID());
                                                    t.getRef().child(NUMBER_IN_QUEUE).setValue(String.valueOf(lastCustomerNumber+" _"));
                                                    t.getRef().child(TIME_TICKET_BOOKED).setValue(Calendar.getInstance().getTimeInMillis());


                        Toast.makeText(getActivity(),getText(R.string.alb),Toast.LENGTH_SHORT).show();
                                                    goToTicketFragment();
                                                });
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(),getString(R.string.in_queue_before) , Toast.LENGTH_SHORT).show();
                                        goToTicketFragment();
                                    }
                                });
                    }
                    else
                        Toast.makeText(getActivity(),getString(R.string.this_branch_stopped),Toast.LENGTH_SHORT).show();
                });


    }

    private void goToTicketFragment(){

        FragmentManager fragmentManager = getParentFragmentManager();
//            fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.popBackStack(COMPANY_FRAGMENT,POP_BACK_STACK_INCLUSIVE);
        Navigation.findNavController(root).navigate(R.id.go_to_ticket);

//        getParentFragmentManager()
//                .beginTransaction()
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
//                .setReorderingAllowed(true)
//                .replace(R.id.nav_host_fragment,new TicketFragment())
//                .commit();
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
//                    startActivity(new Intent(getActivity(),CustomerInterfaceActivity.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());


                    FragmentManager fragmentManager = getParentFragmentManager();
//                    fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-2).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

//                    NavHostFragment.findNavController(new TicketFragment()).navigate(R.id.action_signInFragment_to_usersFragment);

                    fragmentManager.popBackStack(BRANCH_FRAGMENT,POP_BACK_STACK_INCLUSIVE);
//                    findNavController(root).popBackStack(R.id.navigation_home, false);

//                    getParentFragmentManager().popBackStack();
//                    getParentFragmentManager().popBackStack();
//                    getParentFragmentManager()
//                            .beginTransaction()
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .setReorderingAllowed(true)
//                            .replace(R.id.nav_host_fragment,new CompanyFragment())
//                            .commit();
                    return true;
                }
                return false;
            }
        } );
    }

}