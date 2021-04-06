package com.example.myapplication.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.objects.Customer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.activities.MainLoadingPage.ACTIVATED;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.PHONE_NUMBER;
import static com.example.myapplication.activities.MainLoadingPage.USER_NAME;

public class editCustomerAdminActivity extends AppCompatActivity {

    Customer customer;
    String ref;
    DatabaseReference customerReference;
    TextInputEditText nameEditText,phoneEditText;
    Button save,cancel,delete;
    Map<String, Object> update = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer_admin);

        setViews();

        ref = getIntent().getExtras().getString(CUSTOMER);
        customerReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ref);


        customerReference.get()
                .addOnSuccessListener(t-> {
            customer = t.getValue(Customer.class);
            nameEditText.setText(customer.getUsername());
            phoneEditText.setText(customer.getPhoneNumber());
        })
                .addOnFailureListener(t-> Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT));
    }

    private void setViews() {
        nameEditText = findViewById(R.id.customer_name_edit_customer_admin);
        phoneEditText = findViewById(R.id.customer_phone_edit_customer_admin);
        save = findViewById(R.id.save_changes_customer_edit_admin);
        save.setOnClickListener(t->{
            if(nameEditText.getText().toString().equals("")||phoneEditText.getText().toString().equals(""))
                Toast.makeText(this,getString(R.string.pleaseFillAllFields),Toast.LENGTH_SHORT).show();
            else{
                update.put(USER_NAME,nameEditText.getText().toString());
                update.put(PHONE_NUMBER,phoneEditText.getText().toString());
                customerReference.updateChildren(update);
                Toast.makeText(getApplicationContext(),getString(R.string.changes_saved),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        cancel = findViewById(R.id.cancel_changes_customer_edit_admin);
        cancel.setOnClickListener(t->finish());

    }
}