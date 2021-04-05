package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.company.RestaurantPage;
import com.google.firebase.auth.FirebaseAuth;

public class ListPageActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ImageView logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_page);

        logout = findViewById(R.id.log_out_button_list_page);
        firebaseAuth = FirebaseAuth.getInstance();

//        logout = findViewById(R.id.button11);

        logout.setOnClickListener(view -> Logout());
    }
    private void Logout(){
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
        finish();
    }
    public void ToRestPage(View view){
        TextView move;
        move = findViewById(R.id.sign_in);
        Intent intent = new Intent(this, RestaurantPage.class);
        startActivity(intent);
    }
}