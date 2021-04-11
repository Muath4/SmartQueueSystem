package com.example.myapplication.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Company;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_ID;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_LOGO;
import static com.example.myapplication.activities.MainLoadingPage.USER_TYPE;

public class RegisterCompanyActivity extends AppCompatActivity {
    private EditText userName, Pass, Email;
    private Button ButtonRegister,uploadLogoButton;
    private FirebaseAuth firebaseAuth;
    private ImageView logoImage;
    private ProgressDialog progressDialog;
    private Uri filepath;
    private Bitmap bitmap;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    String email, name, password;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference companyRef = firebaseDatabase.getReference(COMPANY);
    DatabaseReference userTypeCompany = firebaseDatabase.getReference(USER_TYPE).child(COMPANY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company);
        setupUIViews();


        ButtonRegister.setOnClickListener(view -> {
            if (validate()) {
                //Upload data to the database
                String email = Email.getText().toString().trim();
                String password = Pass.getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(task -> {
                            sendEmailVerification();
                            addUserDataToDatabaseAndGoToLoginPage();
                        })
                        //.addOnSuccessListener(task -> startActivity(new Intent(getApplicationContext(),BranchesActivity.class)))
                        .addOnFailureListener(task ->  Toast.makeText(getApplicationContext(),getText(R.string.emailregester),Toast.LENGTH_SHORT).show());
            }});
    }

//    public void BackTOThePge (View view){
//        ImageView back;
//        back = findViewById(R.id.BackToMain);
//        Intent intent = new Intent(this, MainPageActivity.class);
//        startActivity(intent);
//    }


    private void setupUIViews(){
        getSupportActionBar().hide();
        findViewById(R.id.back_to_sign_in_page_arrow).setOnClickListener(t->finish());
        firebaseAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.username_sign_in_company_editText);
        Pass = findViewById(R.id.password_sign_in_company_editText);
        Email = findViewById(R.id.email_sign_in_company_editText);
        ButtonRegister = findViewById(R.id.login_button);
        uploadLogoButton=findViewById(R.id.upload_register_company);
        uploadLogoButton.setOnClickListener(t->chooseLogo());
        logoImage=findViewById(R.id.logo_company_register);
//        logoImage.setOnClickListener(t->chooseLogo());
    }

    private void chooseLogo() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        Intent intent=new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,"Please select Image"),2);
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==2 && resultCode==RESULT_OK)
        {
            filepath=data.getData();
            try
            {
                InputStream inputStream=getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                logoImage.setImageBitmap(bitmap);
            }catch (Exception ignored){}
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private Boolean validate(){
        boolean result = false;

        name = userName.getText().toString();
        password = Pass.getText().toString();
        email = Email.getText().toString();

        if (name.isEmpty() || password.isEmpty() || email.isEmpty())
            Toast.makeText(getApplicationContext(),getText(R.string.pleaseFillAllFields),Toast.LENGTH_SHORT).show();
         else if(filepath == null)
            Toast.makeText(this, getString(R.string.please_choose_logo), Toast.LENGTH_SHORT).show();
         else
            result = true;


        return result;
    }
    private void sendEmailVerification(){
            firebaseAuth.getCurrentUser().sendEmailVerification()
                    .addOnSuccessListener(task -> Toast.makeText(getApplicationContext(),getText(R.string.registerd),Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(t->Toast.makeText(getApplicationContext(), "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show());
    }

    private void addUserDataToDatabaseAndGoToLoginPage(){
        String currentUserId = firebaseAuth.getUid();
        Company company = new Company(currentUserId,name, email,0);
        companyRef.child(currentUserId).setValue(company);
        uploadLogo();
//        userTypeCompany.child(currentUserId).setValue(currentUserId);
    }
    private void uploadLogo() {

        if(filepath == null) {
//            Toast.makeText(this, "Please choose an image first", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog dialog=new ProgressDialog(this);
//        dialog.setTitle("wait");
        dialog.setMessage(getString(R.string.just_a_moment));
        dialog.show();


        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference uploader=storage.getReference().child(COMPANY_LOGO).child(COMPANY_ID).child(firebaseAuth.getUid());
        uploader.putFile(filepath)
                .addOnSuccessListener(taskSnapshot -> {
                    dialog.dismiss();
                   // Toast.makeText(getApplicationContext(),"File Uploaded",Toast.LENGTH_LONG).show();
                    firebaseAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
                    finish();
                });
//                .addOnProgressListener(taskSnapshot -> dialog.setMessage(getString(R.string.just_a_moment)));

    }


}