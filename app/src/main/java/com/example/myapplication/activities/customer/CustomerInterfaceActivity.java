package com.example.myapplication.activities.customer;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.R;
import com.example.myapplication.activities.GeofenceBroadcastReceiver;
import com.example.myapplication.activities.GeofenceHelper;
import com.example.myapplication.activities.LoginPageActivity;
import com.example.myapplication.activities.customer.ui.home.HomeFragment;
import com.example.myapplication.activities.customer.ui.home.MapsFragment;
import com.example.myapplication.activities.customer.ui.ticket.TicketFragment;
import com.example.myapplication.objects.Branch;
import com.example.myapplication.objects.Customer;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_BRANCH_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_ID;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_QUEUE_NUMBER;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.NOTIFICATION;
import static com.example.myapplication.activities.MainLoadingPage.STATISTIC;
import static com.example.myapplication.activities.MainLoadingPage.TIMES_CUSTOMER_OUT_RANGE;
import static com.example.myapplication.activities.MainLoadingPage.TIMES_TICKET_CANCELED;

public class CustomerInterfaceActivity extends AppCompatActivity {
    private HomeFragment homeFragment;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private GoogleMap mMap;
    PendingIntent pendingIntent;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    public static Customer customer;
    private Branch branch;
    private static boolean identifyCustomer;
    private GeofenceHelper geofenceHelper;
    private GeofencingClient geofencingClient;
    private DatabaseReference reference;
    private float GEOFENCE_RADIUS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_interface_list);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        geofencingClient = LocationServices.getGeofencingClient(this);

//        getPendingIntent();
        geofenceHelper = new GeofenceHelper(this);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child(CUSTOMER).child(firebaseAuth.getUid()).get()
                .addOnSuccessListener(t-> {
                    customer = t.getValue(Customer.class);
                    reference.child(CUSTOMER).child(firebaseAuth.getUid()).addChildEventListener(new ChildEventListener(){
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                            Log.d("&^&^&","customer.getCurrentBranchId  "+customer.getCurrentBranchId());
//                            reference.child(CUSTOMER).child(firebaseAuth.getUid()).child(snapshot.getKey()).setValue(snapshot.getValue());
//                            reference.child(CUSTOMER).child(firebaseAuth.getUid()).get().addOnSuccessListener(t->customer = t.getValue(Customer.class));

                            if(snapshot.getKey().equals(CURRENT_BRANCH_ID))
                                if (String.valueOf(snapshot.getValue())  != null) {
                                    reference.child(BRANCH).child(String.valueOf(snapshot.getValue())).get()
                                            .addOnSuccessListener(t -> {
                                                branch = t.getValue(Branch.class);
                                                LatLng latLng = new LatLng(branch.getLatitude(), branch.getLongitude());
                                                setLocation();
                                                addGeofence(latLng, branch.getRadius());
                                            });
                                }

                        }

                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Log.d("*&&*","onChildChanged");
                            if (snapshot.getKey().equals(NOTIFICATION)) {
                                if(snapshot.getValue(Boolean.TYPE)) {
                                    sendNotification();
                                }
                            }
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            Log.d("*&&*","onChildRemoved "+snapshot);
                            if(snapshot.getKey().equals(CURRENT_BRANCH_ID)){
                                MapsFragment.doHaveTicket = false;
                                TicketFragment.removeTicket();
//                                startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Log.d("*&&*","onChildMoved");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("*&&*","onCancelled");
                        }
                    });
                })
                .addOnFailureListener(t-> Toast.makeText(this,"ERROR\nplease contact help center",Toast.LENGTH_SHORT).show());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            NotificationChannel notificationChannel = new NotificationChannel("111", "turn", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            mNotificationManager.createNotificationChannel(notificationChannel);


        }
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "111")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("Smart queue System") // title for notification
                .setContentText("Come to branch, its your turn!")// message for notification
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), CustomerInterfaceActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());


        reference.child(CUSTOMER).child(firebaseAuth.getUid()).child(NOTIFICATION).removeValue();
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * getActivity() callback is triggered when the map is ready to be used.
         * getActivity() is where we can add markers or lines, add listeners or move the camera.
         * In getActivity() case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. getActivity() method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);

            // Add a marker in Sydney and move the camera
            LatLng eiffel = new LatLng(48.8589, 2.29365);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel, 16));
            mMap.getUiSettings().setZoomControlsEnabled(true);

            enableUserLocation();
            setCurrentLocationCircle();
        }
    };
    private void setCurrentLocationCircle(){
        //get current location from intent

        if(branch == null)
            return;

        LatLng latLng = new LatLng(branch.getLatitude(),branch.getLongitude());
        addCircle(latLng,branch.getRadius());
        addGeofence(latLng, GEOFENCE_RADIUS);
    }
    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    private void setLocation() {
        getPendingIntent();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapHome);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
        }
    }


    public PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    public static void goOutRange(){
        try {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child(CUSTOMER).child(firebaseAuth.getUid()).child(CURRENT_QUEUE_ID).get()
                    .addOnSuccessListener(t -> {
                        if (t.getValue() != null) {
                            Map<String, Object> customerList = new HashMap<>();
                            customerList.put(firebaseAuth.getUid(), null);

                            reference.child(CUSTOMER).child(firebaseAuth.getUid()).get()
                                    .addOnSuccessListener(t2->{
                                        Customer customer = t2.getValue(Customer.class);
                                        reference.child(BRANCH).child(customer.getCurrentBranchId()).child(customer.getCurrentQueueNumber()).updateChildren(customerList);
                                        reference.child(CUSTOMER).child(customer.getUserId()).child(CURRENT_QUEUE_ID).removeValue();
                                        reference.child(CUSTOMER).child(customer.getUserId()).child(CURRENT_QUEUE_NUMBER).removeValue();
                                        reference.child(CUSTOMER).child(customer.getUserId()).child(CURRENT_BRANCH_ID).removeValue();
                                        MapsFragment.doHaveTicket = false;



                                        setOutRangeAfterBookTicket(customer.getUserId());



                                    });



                        }

                    });

        }catch (Exception ignored){}
    }

    private static void setOutRangeAfterBookTicket(String userId) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child(CUSTOMER).child(userId).child(TIMES_CUSTOMER_OUT_RANGE).get().addOnSuccessListener(t-> {
            if(t.getValue()==null)
                rootRef.child(CUSTOMER).child(userId).child(TIMES_CUSTOMER_OUT_RANGE).setValue(1);
            else
                rootRef.child(CUSTOMER).child(userId).child(TIMES_CUSTOMER_OUT_RANGE).setValue(t.getValue(Integer.TYPE) + 1);
        });

        rootRef.child(STATISTIC).child(TIMES_CUSTOMER_OUT_RANGE).get().addOnSuccessListener(t-> {
            if(t.getValue()==null)
                rootRef.child(STATISTIC).child(TIMES_CUSTOMER_OUT_RANGE).setValue(1);
            else
                rootRef.child(STATISTIC).child(TIMES_CUSTOMER_OUT_RANGE).setValue(t.getValue(Integer.TYPE) + 1);
        });
    }

    private void addGeofence(LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getGeofence("000", latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                    }
                });
    }
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }


  /*  private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }
*/
}