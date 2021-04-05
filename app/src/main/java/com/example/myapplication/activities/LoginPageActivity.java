package com.example.myapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.UserMethods;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class LoginPageActivity extends AppCompatActivity {




    private TextInputEditText Email;
    private TextInputEditText passwordEditText;
    private Button LoginBut;
    private TextView userReg;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUIViews();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(fAuth.getCurrentUser() != null){
//            progDialog.setMessage(getString(R.string.just_a_moment));
//            progDialog.show();
            UserMethods.determineUserTypeAndMoveToCorrectPage(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog != null)
            progressDialog.dismiss();
    }

    public void ResetPass(View view){
        TextView move = findViewById(R.id.reset_password_text_view);
        Intent intent = new Intent(this,ResetPassword.class);
        startActivity(intent);
    }
//    public void Admins(View view){
//        TextView move = findViewById(R.id.Admins);
//        Intent intent = new Intent(this,LoginPanel.class);
//        startActivity(intent);
//    }
    private void validate(String e, String p) {
        progressDialog.setMessage(getString(R.string.just_moment));
        progressDialog.show();

        fAuth.signInWithEmailAndPassword(e, p)
                .addOnSuccessListener(t->{
                    checkEmailVerification();
                    UserMethods.determineUserTypeAndMoveToCorrectPage(this);
                })
                .addOnFailureListener(t -> {
                       Toast.makeText(getApplicationContext(),getText(R.string.emaillogin),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });

    }
    private void checkEmailVerification(){

        if(!fAuth.getCurrentUser().isEmailVerified())
            Toast.makeText(getApplicationContext(), getString(R.string.email_not_verified),Toast.LENGTH_SHORT).show();

    }

    private void setupUIViews() {
        getSupportActionBar().hide();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Email = findViewById(R.id.username_sign_in_customer_editText);
        passwordEditText = findViewById(R.id.password_sign_in_customer_editText);
        LoginBut = findViewById(R.id.login_button);
        userReg = findViewById(R.id.sign_in);
        userReg.setOnClickListener(view -> startActivity(new Intent(LoginPageActivity.this, RegisterCustomerActivity.class)));
        progressDialog = new ProgressDialog(this);
        LoginBut.setOnClickListener(view -> {
            if(Email.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(),getText(R.string.pleaseFillAllFields),Toast.LENGTH_SHORT).show();
            else
                validate(Email.getText().toString(), passwordEditText.getText().toString());
        });
    }
}

