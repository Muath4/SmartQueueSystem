package com.example.myapplication.activities.customer.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.GeofenceHelper;
import com.example.myapplication.objects.Branch;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.myapplication.activities.MainLoadingPage.BRANCH_CLASS;
import static com.example.myapplication.activities.MainLoadingPage.CURRENT_BRANCH_ID;
import static com.example.myapplication.activities.MainLoadingPage.CUSTOMER;
import static com.example.myapplication.activities.MainLoadingPage.MAPS_FRAGMENT;

public class MapsFragment extends Fragment {
    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private DatabaseReference reference;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private float GEOFENCE_RADIUS = 1000;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    static Branch branch;
    private static FragmentManager fragmentManager;
    private static Button branchDetailButton;
    private static TextView outRange,haveTicket;
    private static ContentLoadingProgressBar progressBar;
    private static boolean inArea = false;
    public static boolean doHaveTicket = false;
    private View root;
    LocationManager locationManager;
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
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);

            // Add a marker in Sydney and move the camera
            LatLng riyadh = new LatLng(24.733134, 46.668786);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(riyadh, 16));
            mMap.getUiSettings().setZoomControlsEnabled(true);


            enableUserLocation();
            setCurrentLocationCircle();
            FirebaseDatabase.getInstance().getReference().child(CUSTOMER).child(FirebaseAuth.getInstance().getUid()).child(CURRENT_BRANCH_ID).get()
                    .addOnSuccessListener(t->{
                        if(t.getValue()!=null){
                            doHaveTicket = true;
                            branchDetailButton.setVisibility(View.GONE);
                            outRange.setVisibility(View.GONE);
                            haveTicket.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //  String S = getActivity().getIntent().getExtras().getString("name");
        fragmentManager = getParentFragmentManager();

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        root = inflater.inflate(R.layout.fragment_maps, container, false);
        geofencingClient = LocationServices.getGeofencingClient(getActivity());
        geofenceHelper = new GeofenceHelper(getActivity());
        outRange = root.findViewById(R.id.out_range_text_view);
        haveTicket = root.findViewById(R.id.have_ticket_before_text_view);
        branchDetailButton = root.findViewById(R.id.book_ticket_button);
        progressBar = root.findViewById(R.id.progressBar_maps);
        inArea = false;
        branchDetailButton.setOnClickListener(t -> {
            BranchDetailsFragment branchDetailsFragment = new BranchDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(BRANCH_CLASS, branch);
            branchDetailsFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .addToBackStack(MAPS_FRAGMENT)
                    .setReorderingAllowed(true)
//                            .hide(homeFragment)
                    .replace(R.id.nav_host_fragment, branchDetailsFragment)
                    .commit();
        });
        AsyncTask.execute(() -> new Timer().schedule(new TimerTask(){
            @Override
            public void run() {
                try {
                    if(!inArea)
                        getActivity().runOnUiThread(MapsFragment::outArea);
                }catch (Exception ignored){
                }
            }
        },4500));


        return root;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            //Ask for permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            enableUserLocation();
        }
    }



 //   @Override
   /* public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                // mMap.setMyLocationEnabled(true);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(getActivity(), "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(getActivity(), "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }
*/
    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
   /* private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }*/
    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }


    private void setCurrentLocationCircle(){
        //get current location from intent
        Bundle bundle = getArguments();

        if (bundle != null)
            branch = (Branch) bundle.getSerializable(BRANCH_CLASS);

        if(branch == null)
            return;

        if(branch.getLatitude() == 0)// no previews location indicated
            return;

        LatLng latLng = new LatLng(branch.getLatitude(),branch.getLongitude());
        addCircle(latLng,branch.getRadius());
        addGeofence(latLng, GEOFENCE_RADIUS);
    }

//    private void returnWithInfo(LatLng latLng, float radius){
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra(LATITUDE,latLng.latitude);
//        returnIntent.putExtra(LONGITUDE,latLng.longitude);
//        returnIntent.putExtra(RADIUS,radius);
//        setResult(Activity.RESULT_OK,returnIntent);
//        finish();
//    }

    public static void inArea(){
        if(!doHaveTicket)
        try{
            branchDetailButton.setVisibility(View.VISIBLE);
            outRange.setVisibility(View.GONE);
            haveTicket.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            inArea = true;
        }catch (Exception ignored){

        }

    }

    public static void outArea(){
        if(!doHaveTicket)
        try{
            branchDetailButton.setVisibility(View.GONE);
            outRange.setVisibility(View.VISIBLE);
            haveTicket.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }catch (Exception ignored){}

    }

    public static void removeProgressBar(){
        try{
            progressBar.setVisibility(View.GONE);
        }catch (Exception ignored){}

    }

}