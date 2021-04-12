package com.example.myapplication.activities.customer.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.objects.Company;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import static com.example.myapplication.activities.MainLoadingPage.ACTIVATED;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_CLASS;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_NAME;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_FRAGMENT;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_ID;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_LOGO;
import static com.example.myapplication.activities.MainLoadingPage.COMPANY_NAME;

public class CompanyFragment extends Fragment implements SearchView.OnQueryTextListener {

    /*private GoogleMap mMap;
    PendingIntent pendingIntent;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;*/


//    FirebaseRecyclerAdapter<Branch, HomeViewModel.CustomerBranchHolder> firebaseRecyclerAdapter;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();;
//    RecyclerView recyclerView;
    private HomeViewModel homeViewModel;
    private FirebaseRecyclerAdapter<Company, CompanyHolder> firebaseRecyclerAdapter;
    private View root;
    RelativeLayout progressBar;
    RecyclerView recyclerView;


    @Override
    public void onStop() {
        super.onStop();
        if(firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.startListening();
        FragmentManager fragmentManager = getParentFragmentManager();
        if(fragmentManager.getBackStackEntryCount()!=0) {
            fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
         root = inflater.inflate(R.layout.fragment_company, container, false);
//        homeViewModel.setBranchRecyclerView(root,getParentFragmentManager());
        setBranchRecyclerView(null);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
//        backButtonBehavior();
        return root;
    }

    void setBranchRecyclerView(String search){
        progressBar = root.findViewById(R.id.progress_bar_branches_customer);
        recyclerView = root.findViewById(R.id.branches_list_customer_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query query;
        if (search == null)
             query = rootRef.child(COMPANY).orderByChild(ACTIVATED).equalTo(true);
        else
            query = rootRef.child(COMPANY).orderByChild(COMPANY_NAME).startAt(search);

        FirebaseRecyclerOptions<Company> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Company>()
                .setQuery(query, Company.class)
                .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Company, CompanyHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CompanyHolder companyHolder, int position, @NonNull Company company) {
                companyHolder.setCompany(company);

                progressBar.setVisibility(View.GONE);
                companyHolder.itemView.setOnClickListener(t ->
                {
                    BranchFragment branchFragment = new BranchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(COMPANY_ID,getItem(position).getUserId());
                    branchFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .addToBackStack(COMPANY_FRAGMENT)
                            .setReorderingAllowed(true)
//                            .hide(homeFragment)
                            .replace(R.id.nav_host_fragment, branchFragment)
                            .commit();
//                    Intent i = new Intent(getActivity(), MapsActivity.class);
//                    i.putExtra(BRANCH_CLASS,getItem(position));
//                    startActivity(i);
//                    ((Activity) getActivity()).overridePendingTransition(0, 0);

                });
            }


            @NonNull
            @Override
            public CompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_company_item_customer, parent, false);
                return new CompanyHolder(view);
            }

            @NonNull
            @Override
            public Company getItem(int position) {
                return super.getItem(position);
            }

            @NonNull
            @Override
            public DatabaseReference getRef(int position) {
                return super.getRef(position);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount() == 0)
                    progressBar.setVisibility(View.GONE);
            }


        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }




    public class CompanyHolder extends RecyclerView.ViewHolder{
        private TextView branchID, nameTextView,location,companyID, numberOfBranchesTextView;
        private ImageView logo;
        public CompanyHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_branch_name_customer);
            numberOfBranchesTextView = itemView.findViewById(R.id.short_description);
            logo = itemView.findViewById(R.id.logo_item_customer);
        }

        public void setCompany(Company company) {
            String branchName = company.getCompany_name();
            int branchQueueNumber = company.getNumberOfBranches();
            nameTextView.setText(branchName);
            numberOfBranchesTextView.setText(String.valueOf(branchQueueNumber));
            loadLogo(company.getUserId());


        }

        private void loadLogo(String userId) {
            Log.d("%&#%",userId);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(COMPANY_LOGO).child(COMPANY_ID).child(userId);
//        StorageReference storageRef = storage.getReferenceFromUrl("gs://smartqueuesystem-438.appspot.com/companyLogo/companyID/lyhNwrgRPEPhPqbXpDX8fdut6pl1");

            final long ONE_MEGABYTE = 1024 * 1024;
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    logo.setImageBitmap(bmp);
//                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(), imageView.getHeight(), false));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.top_home_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.home_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
//        SearchManager searchManager =
//                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        setBranchRecyclerView(newText);
        return true;
    }


    /*public PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(getActivity(), GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }



    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }*/

    private void backButtonBehavior() {
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    try {
                        requireActivity().finish();
                    }catch (Exception ignored){}

                    return true;
                }
                return false;
            }
        } );
    }

}






