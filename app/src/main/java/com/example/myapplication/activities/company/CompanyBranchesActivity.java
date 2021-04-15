package com.example.myapplication.activities.company;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.LoginPageActivity;
import com.example.myapplication.objects.Branch;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;

import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_CLASS;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_ID;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_LOGO;


public class CompanyBranchesActivity extends AppCompatActivity {
    private FirebaseRecyclerAdapter<Branch, BranchHolder> firebaseRecyclerAdapter;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private RelativeLayout progressBar;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private ImageView imageView;
    Button browse, upload;
    Uri filepath;
    Bitmap bitmap;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_control);
        setTitle(getString(R.string.branches));
        progressBar = findViewById(R.id.loadingPanel_branches_company);
        Log.d("***", firebaseAuth.getUid());
        loadingBranches();
        imageView=(ImageView)findViewById(R.id.uploadlogo);
        browse=(Button)findViewById(R.id.browse);
        loadLogo();

        browse.setOnClickListener(view -> Dexter.withActivity(CompanyBranchesActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        Intent intent=new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,"Please select Image"),1);
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check());

//        upload.setOnClickListener(view -> uploadToFirebase());

    }

    private void loadLogo() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(COMPANY_LOGO).child(COMPANY_ID).child(firebaseAuth.getUid());
//        StorageReference storageRef = storage.getReferenceFromUrl("gs://smartqueuesystem-438.appspot.com/companyLogo/companyID/lyhNwrgRPEPhPqbXpDX8fdut6pl1");

        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
//                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(), imageView.getHeight(), false));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            filepath=data.getData();
            try
            {
//                InputStream inputStream=getContentResolver().openInputStream(filepath);
//                bitmap= BitmapFactory.decodeStream(inputStream);
//                imageView.setImageBitmap(bitmap);
                if(filepath == null) {
                    Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ProgressDialog dialog=new ProgressDialog(this);
//                dialog.setTitle("File Uploader");
                dialog.show();


                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference uploader=storage.getReference().child(COMPANY_LOGO).child(COMPANY_ID).child(firebaseAuth.getUid());
                uploader.putFile(filepath)
                        .addOnSuccessListener(taskSnapshot -> {
                            dialog.dismiss();
                            loadLogo();
//                            Toast.makeText(getApplicationContext(),"File Uploaded",Toast.LENGTH_LONG).show();
                        })
                        .addOnProgressListener(taskSnapshot -> {
                            dialog.setMessage(getString(R.string.just_a_moment));
                        });
            }catch (Exception ex)
            {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadToFirebase()
    {
        if(filepath == null) {
            Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog dialog=new ProgressDialog(this);
//        dialog.setTitle("File Uploader");
        dialog.show();


        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference uploader=storage.getReference().child(COMPANY_LOGO).child(COMPANY_ID).child(firebaseAuth.getUid());
        uploader.putFile(filepath)
                .addOnSuccessListener(taskSnapshot -> {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"File Uploaded",Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(taskSnapshot -> {
                    dialog.setMessage(getString(R.string.just_a_moment));
                });
    }


    @Override
    protected void onStart() {
        super.onStart();



        if(firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.startListening();

        if(FirebaseAuth.getInstance() == null)
            startActivity(new Intent(this, LoginPageActivity.class));

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    private void loadingBranches() {
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginPageActivity.class));
            return;
        }

//        progressBar.setVisibility(View.VISIBLE);


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query query = rootRef.child(BRANCH).orderByChild(COMPANY_ID).equalTo(firebaseAuth.getCurrentUser().getUid());
        //I change the index to branch name for customer interface, I think its better for performance -- Muath


        RecyclerView recyclerView = findViewById(R.id.branches_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Branch> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Branch>()
                .setQuery(query, Branch.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Branch, BranchHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull BranchHolder branchHolder, int position, @NonNull Branch branch) {
                branchHolder.setBranch(branch);

                progressBar.setVisibility(View.GONE);
                branchHolder.itemView.setOnClickListener(t ->
                {
                    Intent intent = new Intent(getApplicationContext(), EditBranchActivity.class)
                            .putExtra(BRANCH, getRef(position).toString())
                            .putExtra(BRANCH_CLASS, getItem(position));
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public BranchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_branch_item_company, parent, false);
                return new BranchHolder(view);
            }

            @NonNull
            @Override
            public Branch getItem(int position) {
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
        Button move = findViewById(R.id.button_addBranch);
        Intent intent = new Intent(this, AddBranchActivity.class);
        startActivity(intent);
    }


    public void Logout(View view){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(CompanyBranchesActivity.this, LoginPageActivity.class));
    }


    public static class BranchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView branchID, branchNameTextView,location,companyID, numberOfQueuesTextView,email;

        public BranchHolder(View itemView) {
            super(itemView);
            this.branchNameTextView = itemView.findViewById(R.id.itemBranchName);
            this.numberOfQueuesTextView = itemView.findViewById(R.id.itemBranchQueueNumber);
        }


        @Override
        public void onClick(View view) {

        }
        public void setBranch(Branch branch) {
            String branchName = branch.getBranchName();
            int branchQueueNumber = branch.getNumberOfQueues();
            branchNameTextView.setText(branchName);
            numberOfQueuesTextView.setText(String.valueOf(branchQueueNumber));

        }
    }



}
