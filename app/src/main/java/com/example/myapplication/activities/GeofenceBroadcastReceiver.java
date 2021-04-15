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
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d("&&&", String.valueOf("GEOFENCE_TRANSITION_ENTER"));
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", CustomerInterfaceActivity.class);
                inArea();
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.d("&&&", String.valueOf("GEOFENCE_TRANSITION_DWELL"));

                removeProgressBar();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d("&&&", String.valueOf("GEOFENCE_TRANSITION_EXIT"));
                goOutRange();
                outArea();

                break;
        }


    }
}
