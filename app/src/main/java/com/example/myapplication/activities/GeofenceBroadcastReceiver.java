package com.example.myapplication.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.activities.customer.CustomerInterfaceActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static com.example.myapplication.activities.customer.CustomerInterfaceActivity.goOutRange;
import static com.example.myapplication.activities.customer.ui.home.MapsFragment.inArea;
import static com.example.myapplication.activities.customer.ui.home.MapsFragment.outArea;
import static com.example.myapplication.activities.customer.ui.home.MapsFragment.removeProgressBar;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";


    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();
        Intent intent1;
        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
//        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
           //     Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                Log.d("&&&", String.valueOf("GEOFENCE_TRANSITION_ENTER"));
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", CustomerInterfaceActivity.class);
                inArea();
//                Intent i = new Intent(context, Dialog.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
             //    intent1 =new Intent(context,MapsFragment.class);
               // intent1.putExtra("name",x);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.d("&&&", String.valueOf("GEOFENCE_TRANSITION_DWELL"));

                //  Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
//                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", CustomerInterfaceActivity.class);
//                goOutRange();
//                outArea();
                removeProgressBar();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d("&&&", String.valueOf("GEOFENCE_TRANSITION_EXIT"));
//                Toast.makeText(context, "you are out of the range!", Toast.LENGTH_SHORT).show();
//                notificationHelper.sendHighPriorityNotification("you are out of the range!", "", CustomerInterfaceActivity.class);
                goOutRange();
                outArea();

                break;
        }
//        removeProgressBar();


    }
}
