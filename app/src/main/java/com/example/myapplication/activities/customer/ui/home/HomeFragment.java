package com.example.myapplication.activities.customer.ui.home;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.objects.Branch;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.activities.MainLoadingPage.BRANCH;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_CLASS;
import static com.example.myapplication.activities.MainLoadingPage.BRANCH_NAME;

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener {

    /*private GoogleMap mMap;
    PendingIntent pendingIntent;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;*/


//    FirebaseRecyclerAdapter<Branch, HomeViewModel.CustomerBranchHolder> firebaseRecyclerAdapter;

//    RecyclerView recyclerView;
    private HomeViewModel homeViewModel;
    private FirebaseRecyclerAdapter<Branch, CustomerBranchHolder> firebaseRecyclerAdapter;
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
         root = inflater.inflate(R.layout.fragment_home, container, false);
//        homeViewModel.setBranchRecyclerView(root,getParentFragmentManager());
        setBranchRecyclerView(null);

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
             query = rootRef.child(BRANCH);
        else
            query = rootRef.child(BRANCH).orderByChild(BRANCH_NAME).startAt(search);

        FirebaseRecyclerOptions<Branch> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Branch>()
                .setQuery(query, Branch.class)
                .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Branch, CustomerBranchHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CustomerBranchHolder customerBranchHolder, int position, @NonNull Branch branch) {
                CustomerBranchHolder.setBranch(branch);

                progressBar.setVisibility(View.GONE);
                customerBranchHolder.itemView.setOnClickListener(t ->
                {
                    MapsFragment mapsFragment = new MapsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BRANCH_CLASS,getItem(position));
                    mapsFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction()
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .addToBackStack(null)
//                            .hide(homeFragment)
                            .replace(R.id.nav_host_fragment, mapsFragment)
                            .commit();
//                    Intent i = new Intent(getActivity(), MapsActivity.class);
//                    i.putExtra(BRANCH_CLASS,getItem(position));
//                    startActivity(i);
//                    ((Activity) getActivity()).overridePendingTransition(0, 0);

                });
            }


            @NonNull
            @Override
            public CustomerBranchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_branch_item_customer, parent, false);
                return new CustomerBranchHolder(view);
            }

            @NonNull
            @Override
            public Branch getItem(int position) {
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




    public static class CustomerBranchHolder extends RecyclerView.ViewHolder{
        private static TextView branchID, branchNameTextView,location,companyID, numberOfQueuesTextView;

        public CustomerBranchHolder(View itemView) {
            super(itemView);
            branchNameTextView = itemView.findViewById(R.id.item_branch_name_customer);
            numberOfQueuesTextView = itemView.findViewById(R.id.short_description);
        }

        public static void setBranch(Branch branch) {
            String branchName = branch.getBranchName();
            int branchQueueNumber = branch.getNumberOfQueues();
            branchNameTextView.setText(branchName);
            numberOfQueuesTextView.setText(String.valueOf(branchQueueNumber));

        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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


}






