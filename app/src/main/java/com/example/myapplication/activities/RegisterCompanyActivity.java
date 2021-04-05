package com.example.myapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Company;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.USER_TYPE;

public class RegisterCompanyActivity extends AppCompatActivity {
    private EditText userName, Pass, Email;
    private Button ButtonRegister;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
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
                            addUserDataToDatabase();
                            firebaseAuth.signOut();
                            startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
                            finish();
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

    }
    private Boolean validate(){
        boolean result = false;

        name = userName.getText().toString();
        password = Pass.getText().toString();
        email = Email.getText().toString();


        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(getApplicationContext(),getText(R.string.pleaseFillAllFields),Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }
    private void sendEmailVerification(){

            firebaseAuth.getCurrentUser().sendEmailVerification()
                    .addOnSuccessListener(task ->                         Toast.makeText(getApplicationContext(),getText(R.string.registerd),Toast.LENGTH_SHORT).show()
)
                    .addOnFailureListener(t->Toast.makeText(getApplicationContext(), "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show());

    }

    private void addUserDataToDatabase(){
        String currentUserId = firebaseAuth.getUid();
        Company company = new Company(currentUserId,name, email);

        companyRef.push().setValue(company);
//        userTypeCompany.child(currentUserId).setValue(currentUserId);


    }
}