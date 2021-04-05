package com.example.myapplication.activities;

    import android.content.Intent;
    import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

    import com.example.myapplication.activities.customer.ui.home.BranchDetailsFragment;
    import com.example.myapplication.activities.customer.ui.home.HomeFragment;
    import com.example.myapplication.activities.customer.ui.home.MapsFragment;


// ALERT DIALOG
// Sources : http://techblogon.com/alert-dialog-with-edittext-in-android-example-with-source-code/

    public class Dialog extends Activity
    {

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build
                    .setTitle("you are in the area")
                    .setMessage(" Do you want to book ticket?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Intent intent = new Intent(Dialog.this, BranchDetails.class);
                            startActivity(intent);                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                          dialog.dismiss();
                          finish();
                        }
                    });
            AlertDialog alert = build.create();
            alert.show();
        }
    }

