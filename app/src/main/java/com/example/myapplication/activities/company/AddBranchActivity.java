package com.example.myapplication.activities.company;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainLoadingPage;
import com.example.myapplication.objects.Branch;
import com.example.myapplication.objects.BranchAdmin;
import com.example.myapplication.objects.Company;
import com.example.myapplication.objects.Queue;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_ADMIN;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.LATITUDE;
import static com.example.myapplication.activities.MainLoadingPage.LONGITUDE;
import static com.example.myapplication.activities.MainLoadingPage.NUMBER_OF_BRANCHES;
import static com.example.myapplication.activities.MainLoadingPage.QUEUE;
import static com.example.myapplication.activities.MainLoadingPage.RADIUS;
import static com.example.myapplication.activities.MainLoadingPage.USER_ID;

public class AddBranchActivity extends AppCompatActivity {

    FirebaseDatabase Root = FirebaseDatabase.getInstance();
    DatabaseReference BranchReference = Root.getReference(BRANCH);
    DatabaseReference QueueReference = Root.getReference(QUEUE);
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private EditText branchName;
    private int numberOfQueues;
    Button addBranch;
    Button cancelBranch;
    private RelativeLayout progressBar; // to show the progress bar
    private String currentUserId;
    private ImageButton locationIcon;
    private RadioGroup radioGroupQueue;
    private TextInputLayout SecondQueueNameLayout;
    private TextInputEditText firstQueueName;
    private TextInputEditText secondQueueName;
    private TextInputEditText adminEmail;
    private TextInputEditText adminPassword;
    int LAUNCH_SECOND_ACTIVITY = 1;
    double latitude = 0;
    double longitude = 0;
    float radius = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_branch);
        setTitle(getString(R.string.add_branch));
        setupUIViews();
    }

    public void AddBranch() {
        if(isThereMissingValueInBranch()){
            return;
        }


        String branchId = BranchReference.push().getKey();
        String queueId1 = QueueReference.push().getKey();
        Queue queue1 = new Queue(queueId1, branchId, firstQueueName.getText().toString());
        Queue queue2 = null;
//        String queueId1 = QueueReference.push().getKey();
//        QueueReference.child(queueId1).setValue(new Queue(queueId1, branchId, firstQueueName.getText().toString()));

        if (radioGroupQueue.getCheckedRadioButtonId() == R.id.radio_button_two_queue) {
            String queueId2 = QueueReference.push().getKey();
            queue2 = new Queue(queueId2, branchId, secondQueueName.getText().toString());
//            QueueReference.child(queueId2).setValue(new Queue(queueId2, branchId, secondQueueName.getText().toString()));
        }


        currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        progressBar.setVisibility(View.VISIBLE);



        Branch branch = new Branch(branchId,branchName.getText().toString(),latitude,longitude,radius,numberOfQueues,currentUserId, queue1, queue2,adminEmail.getText().toString());

        firebaseAuth.createUserWithEmailAndPassword(adminEmail.getText().toString(), adminPassword.getText().toString())
                .addOnSuccessListener(t2 -> {

                    BranchReference.child(branchId).setValue(branch)
                            .addOnSuccessListener(task -> {
                                BranchAdmin branchAdmin = new BranchAdmin(t2.getUser().getUid(),adminEmail.getText().toString(),branchId,currentUserId);
                                Root.getReference().child(BRANCH_ADMIN).child(t2.getUser().getUid()).setValue(branchAdmin)/*.addOnCompleteListener(t->progressBar.setVisibility(View.GONE))*/;
                                Root.getReference().child(COMPANY).child(currentUserId).get().addOnSuccessListener(t->{
                                    try {
                                        if(t.getValue(Company.class) != null)
                                            Root.getReference().child(COMPANY).child(currentUserId).child(NUMBER_OF_BRANCHES).setValue(t.getValue(Company.class).getNumberOfBranches()+1);

                                    }catch (Exception ignored){}
                                });
                                Toast.makeText(getApplicationContext(), R.string.branchAdded, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainLoadingPage.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            })
                            .addOnFailureListener(task -> {
                                Toast.makeText(getApplicationContext(), R.string.cannotAddBranch + "\n" + task.getMessage(), Toast.LENGTH_LONG).show();
                                t2.getUser().delete(); // if branch didn't created admin account will be deleted
                            });
                })
                .addOnFailureListener(t2 -> {
                    Toast.makeText(getApplicationContext(), "Sorry create an admin account:\n "+t2.getMessage(), Toast.LENGTH_LONG).show();
                })
                .addOnCompleteListener(t->progressBar.setVisibility(View.GONE));








    }

    private boolean isThereMissingValueInBranch(){
            if(branchName.getText().toString().isEmpty()
                    || firstQueueName.getText().toString().isEmpty()
                    || adminPassword.getText().toString().isEmpty()
                    || adminPassword.getText().toString().isEmpty()
                    || (SecondQueueNameLayout.getVisibility() == View.VISIBLE && secondQueueName.getText().toString().isEmpty())) {
                Toast.makeText(getApplicationContext(), getString(R.string.pleaseFillAllFields) , Toast.LENGTH_SHORT).show();
                return true;
            }
            if(latitude == 0){
                Toast.makeText(getApplicationContext(), getString(R.string.please_choose_branch_location) , Toast.LENGTH_SHORT).show();
                return true;
            }

        return false;
    }

    private void setupUIViews(){
        numberOfQueues = 1;
        progressBar = findViewById(R.id.loadingPanel_addBranch);
        branchName = findViewById(R.id.branch_name_add_branch);
        locationIcon = findViewById(R.id.location_icon_add_branch_image);
        adminEmail = findViewById(R.id.branch_admin_email_add_branch);
        adminPassword = findViewById(R.id.branch_admin_password_add_branch);
        addBranch = findViewById(R.id.addBranch);
        addBranch.setOnClickListener(t->AddBranch());
        cancelBranch = findViewById(R.id.cancel_add_branch);
        cancelBranch.setOnClickListener(t -> finish());
        SecondQueueNameLayout = findViewById(R.id.queue_two_name_layout);
        firstQueueName = findViewById(R.id.queue_one_name);
        secondQueueName = findViewById(R.id.queue_two_name);
        radioGroupQueue = findViewById(R.id.radio_group_queue);
        radioGroupQueue.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.radio_button_two_queue) {
                SecondQueueNameLayout.setVisibility(View.VISIBLE);
                numberOfQueues = 2;
            }
            else {
                SecondQueueNameLayout.setVisibility(View.GONE);
                numberOfQueues = 1;
                secondQueueName.setText(null);
            }
        });
        locationIcon.setOnClickListener(t -> {
            Intent intent = new Intent(getApplicationContext(), EditMapsActivity.class);
            intent.putExtra(LATITUDE,latitude);
            intent.putExtra(LONGITUDE,longitude);
            intent.putExtra(RADIUS,radius);
            startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                latitude = data.getDoubleExtra(LATITUDE,0);
                longitude = data.getDoubleExtra(LONGITUDE,0);
                radius = data.getFloatExtra(RADIUS,0);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}
