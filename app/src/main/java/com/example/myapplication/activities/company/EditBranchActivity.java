package com.example.myapplication.activities.company;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Branch;
import com.example.myapplication.objects.Company;
import com.example.myapplication.objects.Customer;
import com.example.myapplication.objects.Queue;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.activities.MainLoadingPage.ACTIVATED;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_ADMIN;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_CLASS;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_BRANCH_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_NUMBER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER_ID_LIST;
import static com.example.myapplication.activities.MainLoadingPage.LATITUDE;
import static com.example.myapplication.activities.MainLoadingPage.LONGITUDE;
import static com.example.myapplication.activities.MainLoadingPage.NUMBER_IN_QUEUE;
import static com.example.myapplication.activities.MainLoadingPage.NUMBER_OF_BRANCHES;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE1;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE2;
import static com.example.myapplication.activities.MainLoadingPage.RADIUS;

public class EditBranchActivity extends AppCompatActivity {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private MaterialTextView branchLocation;
    private TextInputEditText branchName, queue1name,queue2name;
    private TextInputLayout SecondQueueNameLayout;
    private RadioGroup radioGroupQueue;
    private Button cancelButton,saveButton,deleteButton;
    FirebaseDatabase Root = FirebaseDatabase.getInstance();
    DatabaseReference branchReference;
    private RelativeLayout progressBar;
    private Branch branchFromRef;
    private Branch branchFromIntent;
    double latitude;
    double longitude;
    float radius = 0;

    private ImageButton locationIcon;
    int LAUNCH_SECOND_ACTIVITY = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_branch);
        setTitle(getString(R.string.edit_branch));
        setupUIViews();


    }

    private void setupUIViews() {

        radioGroupQueue = findViewById(R.id.radio_group_queue_edit_branch);
        SecondQueueNameLayout = findViewById(R.id.queue_two_name_layout_edit_branch);
        queue1name = findViewById(R.id.queue1_name_edit_branch_EditText);
        queue2name = findViewById(R.id.queue2_name_edit_branch_EditText);
        branchName = findViewById(R.id.branchName_editBranch_EditText);
        branchLocation = findViewById(R.id.location_editBranch_TextView);
//        numberOfQueue = findViewById(R.id.queueNumber_editBranch_EditText);
        progressBar = findViewById(R.id.loadingPanel_editBranch);

        cancelButton = findViewById(R.id.cancel_editBranch_Button);
        cancelButton.setOnClickListener(t -> finish());

        saveButton = findViewById(R.id.save_editBranch_Button);
        saveButton.setOnClickListener(t -> {
            if(isThereMissingValueInBranch())
                return;
            progressBar.setVisibility(View.VISIBLE);
            branchFromIntent.getQueue1().setQueueName(queue1name.getText().toString());
            if(branchFromIntent.getNumberOfQueues() == 2)
                if(branchFromIntent.getQueue2() == null) {
                    branchFromIntent.setQueue2(new Queue(FirebaseDatabase.getInstance().getReference().push().getKey(),branchFromIntent.getBranchID(),queue2name.getText().toString()));
                }else
                   branchFromIntent.getQueue2().setQueueName(queue2name.getText().toString());
            else
                branchFromIntent.setQueue2(null);
            Branch editedBranch = new Branch(branchFromIntent.getBranchID(),branchName.getText().toString(),branchFromIntent.getLatitude(),branchFromIntent.getLongitude(),branchFromIntent.getRadius(),branchFromIntent.getNumberOfQueues(),firebaseAuth.getUid(), branchFromIntent.getQueue1(), branchFromIntent.getQueue2(),branchFromIntent.getAdminEmail());
            branchReference.setValue(editedBranch)
                    .addOnSuccessListener( t2 -> {
                        Toast.makeText(getApplicationContext(),getString(R.string.changes_saved),Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnCompleteListener(t2 -> progressBar.setVisibility(View.GONE))
                    .addOnFailureListener(t2 -> Toast.makeText(getApplicationContext(),t2.getMessage(),Toast.LENGTH_SHORT).show());
        });

        deleteButton = findViewById(R.id.delete_editBranch_Button);
        deleteButton.setOnClickListener(t -> setDeleteBranchButton());

        locationIcon = findViewById(R.id.location_icon_edit_branch_image);
        locationIcon.setOnClickListener(t -> {

            Intent intent = new Intent(getApplicationContext(), EditMapsActivity.class);
            intent.putExtra(LATITUDE,branchFromIntent.getLatitude());
            intent.putExtra(LONGITUDE,branchFromIntent.getLongitude());
            intent.putExtra(RADIUS,branchFromIntent.getRadius());

            startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
        });

        branchFromIntent = (Branch) getIntent().getSerializableExtra(BRANCH_CLASS);
        branchName.setText(branchFromIntent.getBranchName());
//        branchLocation.setText(String.valueOf(branchFromIntent.getLatitude())); /////// should change this after ********
        queue1name.setText(branchFromIntent.getQueue1().getQueueName());
        if(branchFromIntent.getNumberOfQueues() == 1)
            radioGroupQueue.check(R.id.radio_button_one_queue_edit_branch);
        else{
            radioGroupQueue.check(R.id.radio_button_two_queue_edit_branch);
            SecondQueueNameLayout.setVisibility(View.VISIBLE);
            queue2name.setText(branchFromIntent.getQueue2().getQueueName());
        }
        radioGroupQueue.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.radio_button_two_queue_edit_branch) {
                SecondQueueNameLayout.setVisibility(View.VISIBLE);
               branchFromIntent.setNumberOfQueues(2);
            }
            else {
                SecondQueueNameLayout.setVisibility(View.GONE);
                branchFromIntent.setNumberOfQueues(1);
            }
        });
        branchReference = Root.getReferenceFromUrl(getIntent().getExtras().getString(BRANCH));
    }

    private void setDeleteBranchButton() {
        progressBar.setVisibility(View.VISIBLE);
        deleteCustomerInsideQueue();
    }

    private void deleteBranch() {
        branchReference.removeValue()
                .addOnSuccessListener( t2 -> {
                    Root.getReference().child(COMPANY).child(firebaseAuth.getUid()).get().addOnSuccessListener(t3->{
                        try {
                            if(t3.getValue(Company.class) != null)
                                Root.getReference().child(COMPANY).child(firebaseAuth.getUid()).child(NUMBER_OF_BRANCHES).setValue(t3.getValue(Company.class).getNumberOfBranches()-1);
                        }catch (Exception ignored){}
//                            if(t3.getValue(Company.class) != null)
//                                Root.getReference().child(COMPANY).child(firebaseAuth.getUid()).child(NUMBER_OF_BRANCHES).setValue(t3.getValue(Company.class).getNumberOfBranches()-1);
                    });
                    Toast.makeText(getApplicationContext(),getString(R.string.branch_deleted),Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnCompleteListener(t2 -> progressBar.setVisibility(View.GONE))
                .addOnFailureListener(t2 -> Toast.makeText(getApplicationContext(),t2.getMessage(),Toast.LENGTH_SHORT).show());
    }

    private void deleteCustomerInsideQueue() {
        branchReference.get().addOnSuccessListener(t->{
            Branch branch = t.getValue(Branch.class);
            deleteCustomerInOneQueue(QUEUE1);
            if(branch.getNumberOfQueues() == 2)
                deleteCustomerInOneQueue(QUEUE2);
        });
    }

    private void deleteCustomerInOneQueue(String queueNumber) {
        Log.d("#$%@EW","deleteCustomerInOneQueue");
//        Map<String, Object> deactivateBranchAdmin = new HashMap<>();
//        deactivateBranchAdmin.put(ACTIVATED,false);
//        String branchId = branchFromIntent.getBranchID();
//        Root.getReference().child(BRANCH_ADMIN).child(branchId).updateChildren(deactivateBranchAdmin);
        branchReference.child(queueNumber).child(CUSTOMER_ID_LIST).get().addOnSuccessListener(t2->{
            Log.d("#$%@EW",t2.getValue() +"^^"+t2.getKey()+"^^"+t2.getChildren());
            deleteBranch();

            t2.getChildren().forEach(t3->{

                Root.getReference().child(CUSTOMER).child(String.valueOf(t3.getValue())).get().addOnSuccessListener(t4->{
                    Map<String, Object> update = new HashMap<>();
                    update.put(CURRENT_BRANCH_ID,null);
                    update.put(CURRENT_QUEUE_NUMBER,null);
                    update.put(CURRENT_QUEUE_ID,null);
                    update.put(NUMBER_IN_QUEUE,null);
                    t4.getRef().updateChildren(update);
                });
            });
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                branchFromIntent.setLatitude(data.getDoubleExtra(LATITUDE,0));
                branchFromIntent.setLongitude(data.getDoubleExtra(LONGITUDE,0));
                branchFromIntent.setRadius(data.getFloatExtra(RADIUS,0));

                ////----------
                branchLocation.setText(String.valueOf(data.getDoubleExtra(LATITUDE,0)));
                ////-----------

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private boolean isThereMissingValueInBranch(){
        if(branchName.getText().toString().isEmpty()
                || queue1name.getText().toString().isEmpty()
                || (SecondQueueNameLayout.getVisibility() == View.VISIBLE && queue2name.getText().toString().isEmpty())) {
            Toast.makeText(getApplicationContext(), getString(R.string.pleaseFillAllFields) , Toast.LENGTH_SHORT).show();
            return true;
        }


        return false;
    }
}

