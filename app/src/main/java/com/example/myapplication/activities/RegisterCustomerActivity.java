package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.USER_TYPE;
//import static com.example.myapplication.activities.MainLoadingPage.Panel;


public class RegisterCustomerActivity extends AppCompatActivity {
    private EditText userName, Pass, Email;
    private Button ButtonRegister;
    private FirebaseAuth firebaseAuth;
    String email, name, password;




    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference customerRef = firebaseDatabase.getReference(CUSTOMER);
    DatabaseReference userTypeCustomer = firebaseDatabase.getReference(USER_TYPE).child(CUSTOMER);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);
        setupUIViews();
        getSupportActionBar().hide();

        findViewById(R.id.back_to_login_page_arrow).setOnClickListener(t -> finish());
        findViewById(R.id.go_to_company_sign_in_button).setOnClickListener(t -> startActivity(new Intent(getApplicationContext(), RegisterCompanyActivity.class)));
        firebaseAuth = FirebaseAuth.getInstance();

        ButtonRegister.setOnClickListener(view -> {
            name = userName.getText().toString();
            password = Pass.getText().toString();
            email = Email.getText().toString();
            if (validate()) {
                //Upload data to the database
//                    String uemail = Email.getText().toString().trim();
//                    String upass = Pass.getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(task -> sendEmailVerification())
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
        userName = findViewById(R.id.username_sign_in_customer_editText);
        Pass = findViewById(R.id.password_sign_in_customer_editText);
        Email = findViewById(R.id.email_sign_in_customer_editText);
        ButtonRegister = findViewById(R.id.login_button);
    }
    private Boolean validate(){
        Boolean result = false;




        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(getApplicationContext(),getText(R.string.pleaseFillAllFields),Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }
    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        Toast.makeText(getApplicationContext(),getText(R.string.registerd),Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        //startActivity(new Intent(RegisterCostumerActivity.this, CostumerLoginActivity.class));
                    }else{
                        Toast.makeText(RegisterCustomerActivity.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData(){
        String currentUserId = firebaseAuth.getUid();
        Customer customer = new Customer(currentUserId,name, email, 40);

        customerRef.child(currentUserId).setValue(customer);
//        userTypeCustomer.child(currentUserId).setValue(currentUserId);
    }
}

