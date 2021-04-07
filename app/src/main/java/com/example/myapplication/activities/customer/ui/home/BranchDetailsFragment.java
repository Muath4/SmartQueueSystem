package com.example.myapplication.activities.customer.ui.home;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activities.customer.ui.ticket.TicketFragment;
import com.example.myapplication.objects.Branch;
import com.example.myapplication.objects.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_CLASS;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_BRANCH_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_NUMBER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER_ID_LIST;
import static com.example.myapplication.activities.MainLoadingPage.IS_QUEUE_RUN;
import static com.example.myapplication.activities.MainLoadingPage.LAST_CUSTOMER_NUMBER;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE1;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE2;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BranchDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BranchDetailsFragment extends Fragment {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = firebaseDatabase.getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Button queueOne;
    Button queueTwo;
    View root;
    Branch branch;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BranchDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment branchDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
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


// This callback will only be called when MyFragment is at least Started.
//        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//            @Override
//            public void handleOnBackPressed() {
//                // Handle the back button event
//                getParentFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.nav_host_fragment,new HomeFragment())
//                        .commit();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("*****","onDestroy");
        if(getParentFragmentManager().getBackStackEntryCount() != 0)
            getParentFragmentManager().popBackStack();


    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_branch_details, container, false);

        Bundle bundle = this.getArguments();
        branch = (Branch) bundle.getSerializable(BRANCH_CLASS);

        TextView branchName = root.findViewById(R.id.branch_name_customer_detail);

        setQueueButtons();



        branchName.setText(branch.getBranchName());
        queueOne.setText(branch.getQueue1().getQueueName());
        if(branch.getNumberOfQueues()==2) {
            queueTwo.setVisibility(View.VISIBLE);
            queueTwo.setText(branch.getQueue2().getQueueName());
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
                        queueOne.setEnabled(snapshot.getValue(Boolean.TYPE));
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
                                                        lastCustomerNumber = 0;
                                                    else
                                                        lastCustomerNumber = tt.getValue(Integer.TYPE) + 1;

                                                    rootRef.child(BRANCH).child(branch.getBranchID()).child(queueNumber).child(LAST_CUSTOMER_NUMBER).setValue(lastCustomerNumber);

                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put(firebaseAuth.getUid(), lastCustomerNumber);
                                                    rootRef.child(BRANCH).child(branch.getBranchID()).child(queueNumber).child(CUSTOMER_ID_LIST).updateChildren(map);
                                                    t.getRef().child(CURRENT_QUEUE_ID).setValue(queueId);
                                                    t.getRef().child(CURRENT_QUEUE_NUMBER).setValue(queueNumber);
                                                    t.getRef().child(CURRENT_BRANCH_ID).setValue(branch.getBranchID());

                                                    Toast.makeText(getActivity(),"Added in queue",Toast.LENGTH_SHORT).show();
                                                    goToTicketFragment();


                                                });


                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(), "You are in a queue before!", Toast.LENGTH_SHORT).show();
                                        goToTicketFragment();
                                    }
                                });
                    }
                    else
                        Toast.makeText(getActivity(),getString(R.string.this_branch_stopped),Toast.LENGTH_SHORT).show();
                });


    }

    private void goToTicketFragment(){
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment,new TicketFragment())
                .commit();
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