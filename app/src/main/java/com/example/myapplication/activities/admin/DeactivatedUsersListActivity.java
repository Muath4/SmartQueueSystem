package com.example.myapplication.activities.admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.objects.Branch;
import com.example.myapplication.objects.BranchAdmin;
import com.example.myapplication.objects.Company;
import com.example.myapplication.objects.Customer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.activities.MainLoadingPage.ACTIVATED;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_ADMIN;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_ID;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_LOGO;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.USER_TYPE;

public class DeactivatedUsersListActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private RelativeLayout progressBar;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseRecyclerAdapter<Customer, CustomerHolder> firebaseCustomerRecyclerAdapter;
    private FirebaseRecyclerAdapter<Company, CompanyHolder> firebaseCompanyRecyclerAdapter;;
    private TextView numberOfDeletedUsers;
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private Query query;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_users_list);
        numberOfDeletedUsers = findViewById(R.id.number_of_deleted_user);
        recyclerView = findViewById(R.id.deleted_user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(getIntent().getStringExtra(USER_TYPE).equals(CUSTOMER))
            setUserTypeCustomer();
        else
            setUserTypeCompany();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseCustomerRecyclerAdapter != null)
            firebaseCustomerRecyclerAdapter.startListening();
        if(firebaseCompanyRecyclerAdapter !=null)
            firebaseCompanyRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseCustomerRecyclerAdapter != null)
            firebaseCustomerRecyclerAdapter.stopListening();
        if(firebaseCompanyRecyclerAdapter !=null)
            firebaseCompanyRecyclerAdapter.stopListening();

    }

    private void setUserTypeCompany() {
        query = rootRef.child(COMPANY).orderByChild(ACTIVATED).equalTo(false);
        FirebaseRecyclerOptions<Company> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Company>()
                .setQuery(query, Company.class)
                .build();
        firebaseCompanyRecyclerAdapter = new FirebaseRecyclerAdapter<Company, CompanyHolder>(firebaseRecyclerOptions) {


            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindViewHolder(@NonNull CompanyHolder companyHolder, int i, @NonNull Company company) {
                companyHolder.setCompany(company);
                companyHolder.itemView.setOnClickListener(t ->
                {
                    Intent intent = new Intent(getApplicationContext(), editCompanyAdminActivity.class)
                            .putExtra(COMPANY, getRef(i).toString());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public CompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_deactivated_company_item_panel, parent, false);
                return new CompanyHolder(view);
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
                numberOfDeletedUsers.setText(String.valueOf(getItemCount()));

            }

        };


        recyclerView.setAdapter(firebaseCompanyRecyclerAdapter);
    }

    private void setUserTypeCustomer() {
        query = rootRef.child(CUSTOMER).orderByChild(ACTIVATED).equalTo(false);
        FirebaseRecyclerOptions<Customer> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Customer>()
                .setQuery(query, Customer.class)
                .build();
        firebaseCustomerRecyclerAdapter = new FirebaseRecyclerAdapter<Customer, CustomerHolder>(firebaseRecyclerOptions) {


            @Override
            protected void onBindViewHolder(@NonNull CustomerHolder customerHolder, int i, @NonNull Customer customer) {


                customerHolder.setCustomer(customer);
                customerHolder.itemView.setOnClickListener(t ->
                {
                    Intent intent = new Intent(getApplicationContext(), editCustomerAdminActivity.class)
                            .putExtra(CUSTOMER, getRef(i).toString());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_deactivated_user_item_panel, parent, false);
                return new CustomerHolder(view);
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
                numberOfDeletedUsers.setText(String.valueOf(getItemCount()));

            }
        };
        recyclerView.setAdapter(firebaseCustomerRecyclerAdapter);
    }

    public static class CustomerHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView,phoneTextView,emailTextView,completedTicketRatio;
        private Button activate;

        public CustomerHolder(View itemView) {
            super(itemView);
            this.nameTextView = itemView.findViewById(R.id.user_name_item_panel);
            this.phoneTextView = itemView.findViewById(R.id.user_phone_item_panel);
            this.emailTextView=itemView.findViewById(R.id.user_email_item_panel);
            completedTicketRatio = itemView.findViewById(R.id.completed_ticket_ratio);
            activate = itemView.findViewById(R.id.restore_user);
        }

        public void setCustomer(Customer customer) {
            String name = customer.getUsername();
            String phone=customer.getPhoneNumber();
            String email = customer.getEmail();
            nameTextView.setText(name);
            phoneTextView.setText(phone);
            emailTextView.setText(email);
            activate.setOnClickListener(t->{
                Map<String, Object> update = new HashMap<>();
                update.put(ACTIVATED,true);
                FirebaseDatabase.getInstance().getReference().child(CUSTOMER).child(customer.getUserId()).updateChildren(update);
            });

            double timesTicketCompleted = customer.getTimesTicketCompleted();
            double timesCustomerOutRangeAfterBookTicket = customer.getTimesCustomerOutRangeAfterBookTicket();
            double timesTicketCanceled = customer.getTimesTicketCanceled();
            double allTimesTicketBooked = timesTicketCompleted+timesCustomerOutRangeAfterBookTicket+timesTicketCanceled;

            double completeRatio = (timesTicketCompleted/allTimesTicketBooked)*100;
            completeRatio = Math.floor(completeRatio * 100)/100;

            completedTicketRatio.setText("%"+completeRatio);
        }
    }

    public class CompanyHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView,numberOfBranchesTextView,emailTextView;
        private Button delete;
        private ImageView logo;

        public CompanyHolder(View itemView) {
            super(itemView);
            this.nameTextView = itemView.findViewById(R.id.item_company_name_admin);
            this.emailTextView=itemView.findViewById(R.id.item_company_email_admin);
            this.numberOfBranchesTextView=itemView.findViewById(R.id.item_company_number_of_branches_admin);
            delete = itemView.findViewById(R.id.delete_company_button_item);
            logo = itemView.findViewById(R.id.logo_company_admin);
        }

        public void setCompany(Company company) {
            String name = company.getCompany_name();
            String email = company.getEmail();
            int numberOfBranches = company.getNumberOfBranches();
            nameTextView.setText(name);
            emailTextView.setText(email);
            numberOfBranchesTextView.setText(String.valueOf(numberOfBranches));
            loadLogo(company.getUserId());
            delete.setOnClickListener(t->{
                Map<String, Object> update = new HashMap<>();
                update.put(ACTIVATED,true);
                FirebaseDatabase.getInstance().getReference().child(COMPANY).child(company.getUserId()).updateChildren(update);

                deactivateBranchesAndBranchAdmins(company.getUserId(),BRANCH);
                deactivateBranchesAndBranchAdmins(company.getUserId(),BRANCH_ADMIN);
            });

        }
        private void loadLogo(String userId) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(COMPANY_LOGO).child(COMPANY_ID).child(userId);
//        StorageReference storageRef = storage.getReferenceFromUrl("gs://smartqueuesystem-438.appspot.com/companyLogo/companyID/lyhNwrgRPEPhPqbXpDX8fdut6pl1");

            final long ONE_MEGABYTE = 1024 * 1024;
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    logo.setImageBitmap(bmp);
//                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(), imageView.getHeight(), false));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        private void deactivateBranchesAndBranchAdmins(String userId, String type) {
            FirebaseDatabase.getInstance().getReference().child(type).orderByChild(COMPANY_ID).equalTo(userId).get()
                    .addOnSuccessListener(t2->{
                        Map<String, Object> updateBranchAdmin = new HashMap<>();
                        updateBranchAdmin.put(ACTIVATED,true);
                        if(type.equals(BRANCH_ADMIN))
                            t2.getChildren().forEach(t3->{
                                BranchAdmin branchAdmin = t3.getValue(BranchAdmin.class);
                                FirebaseDatabase.getInstance().getReference().child(type).child(branchAdmin.getUserId()).updateChildren(updateBranchAdmin);
                            });
                        else
                            t2.getChildren().forEach(t3->{
                                Branch branch = t3.getValue(Branch.class);
                                FirebaseDatabase.getInstance().getReference().child(type).child(branch.getBranchID()).updateChildren(updateBranchAdmin);
                            });
                    });
        }

    }



}