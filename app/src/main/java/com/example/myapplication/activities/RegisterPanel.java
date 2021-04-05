/*
package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Panel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.myapplication.activities.MainLoadingPage.PANEL;
import static com.example.myapplication.activities.MainLoadingPage.UserType;

public class RegisterPanel extends AppCompatActivity {
    private EditText userName, Pass, Email;
    private Button ButtonRegister;
    private FirebaseAuth firebaseAuth;
    String email, name, password;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference companyRef = firebaseDatabase.getReference(PANEL);
    DatabaseReference userTypeCompany = firebaseDatabase.getReference(UserType).child(PANEL);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerpanel);
        setupUIViews();

        ButtonRegister.setOnClickListener(view -> {
            if (validate()) {
                //Upload data to the database
                String uemail = Email.getText().toString().trim();
                String upass = Pass.getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(uemail, upass)
                        .addOnSuccessListener(task -> sendEmailVerification())
                        //.addOnSuccessListener(task -> startActivity(new Intent(getApplicationContext(),BranchesActivity.class)))
                        .addOnFailureListener(task -> Toast.makeText(getApplicationContext(),task.getMessage(),Toast.LENGTH_LONG).show());
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

    }
    private Boolean validate(){
        boolean result = false;

        name = userName.getText().toString();
        password = Pass.getText().toString();
        email = Email.getText().toString();


        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }
    private void sendEmailVerification(){
        Log.d("****","sendEmailVerification");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        Toast.makeText(getApplicationContext(), "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();

                        startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData(){
        String currentUserId = firebaseAuth.getUid();
        Panel company = new Panel(currentUserId,name, email);

        companyRef.child(currentUserId).setValue(company);
        userTypeCompany.push().setValue(currentUserId);


    }
}*/
