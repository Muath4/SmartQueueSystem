package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    private EditText email;
    private MaterialButton resetPass;
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = (EditText)findViewById(R.id.editTextTextPersonName);
        resetPass = findViewById(R.id.textView19);
        Auth = FirebaseAuth.getInstance();

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uemail = email.getText().toString().trim();

                if(uemail.equals("")){
                    Toast.makeText(getApplicationContext(),getText(R.string.remail),Toast.LENGTH_SHORT).show();
                }else{
                    Auth.sendPasswordResetEmail(uemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),getText(R.string.resetemail),Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ResetPassword.this, LoginPageActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(),getText(R.string.erremail),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



    }

    public void resetThePassword(View view){

        EditText email = findViewById(R.id.editTextTextPersonName);
        TextView resetPass = findViewById(R.id.textView19);
        FirebaseAuth Auth = FirebaseAuth.getInstance();
        String uemail = email.getText().toString().trim();

        if(uemail.equals("")){
            Toast.makeText(ResetPassword.this, "Please enter your registered email ID", Toast.LENGTH_SHORT).show();
        }else{
            Auth.sendPasswordResetEmail(uemail)
                    .addOnSuccessListener(t->{
                        Toast.makeText(ResetPassword.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ResetPassword.this, LoginPageActivity.class));})
                    .addOnFailureListener(task -> {

                    Toast.makeText(ResetPassword.this, task.getMessage(), Toast.LENGTH_SHORT).show();

            });
        }
    }
}