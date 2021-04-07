package com.example.myapplication.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.objects.Company;
import com.example.myapplication.objects.Customer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_NAME;
import static com.example.myapplication.activities.MainLoadingPage.PHONE_NUMBER;
import static com.example.myapplication.activities.MainLoadingPage.USER_NAME;

public class editCompanyAdminActivity extends AppCompatActivity {
    Company company;
    String ref;
    DatabaseReference companyReference;
    TextInputEditText nameEditText;
    Button save,cancel;
    Map<String, Object> update = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_company_admin);

        setViews();

        ref = getIntent().getExtras().getString(COMPANY);
        companyReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ref);


        companyReference.get()
                .addOnSuccessListener(t-> {
                    company = t.getValue(Company.class);
                    nameEditText.setText(company.getCompany_name());
                })
                .addOnFailureListener(t-> Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT));
    }

    private void setViews() {
        nameEditText = findViewById(R.id.company_name_edit_customer_admin);
        save = findViewById(R.id.save_changes_company_edit_admin);
        save.setOnClickListener(t->{
            if(nameEditText.getText().toString().equals(""))
                Toast.makeText(this,getString(R.string.pleaseFillAllFields),Toast.LENGTH_SHORT).show();
            else{
                update.put(COMPANY_NAME,nameEditText.getText().toString());
                companyReference.updateChildren(update);
                Toast.makeText(getApplicationContext(),getString(R.string.changes_saved),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        cancel = findViewById(R.id.cancel_changes_company_edit_admin);
        cancel.setOnClickListener(t->finish());

    }

}