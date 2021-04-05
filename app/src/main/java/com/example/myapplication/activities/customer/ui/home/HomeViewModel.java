package com.example.myapplication.activities.customer.ui.home;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private SavedStateHandle state;
//    private FirebaseRecyclerAdapter<Branch, CustomerBranchHolder> firebaseRecyclerAdapter;
    public HomeViewModel(){
    }



//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        if(firebaseRecyclerAdapter != null)
//            firebaseRecyclerAdapter.stopListening();
//    }

//    void setBranchRecyclerView(View root, FragmentManager parentFragmentManager){
//        RelativeLayout progressBar = root.findViewById(R.id.progress_bar_branches_customer);
//        RecyclerView recyclerView = root.findViewById(R.id.branches_list_customer_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        Query query = rootRef.child(BRANCH);
//        FirebaseRecyclerOptions<Branch> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Branch>()
//                .setQuery(query, Branch.class)
//                .build();
//
//        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Branch, CustomerBranchHolder>(firebaseRecyclerOptions) {
//            @Override
//            protected void onBindViewHolder(@NonNull CustomerBranchHolder customerBranchHolder, int position, @NonNull Branch branch) {
//                CustomerBranchHolder.setBranch(branch);
//
//                progressBar.setVisibility(View.GONE);
//                customerBranchHolder.itemView.setOnClickListener(t ->
//                {
//                    BranchDetailsFragment branchDetailsFragment = new BranchDetailsFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(BRANCH,getItem(position));
//                    branchDetailsFragment.setArguments(bundle);
//                    parentFragmentManager.beginTransaction()
////                            .addToBackStack(HOME_FRAGMENT)
////                            .hide(homeFragment)
//                            .replace(R.id.nav_host_fragment, branchDetailsFragment)
//                            .commit();
//                });
//            }
//
//            @NonNull
//            @Override
//            public CustomerBranchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_branch_item_customer, parent, false);
//                return new CustomerBranchHolder(view);
//            }
//
//            @NonNull
//            @Override
//            public Branch getItem(int position) {
//                return super.getItem(position);
//            }
//
//            @NonNull
//            @Override
//            public DatabaseReference getRef(int position) {
//                return super.getRef(position);
//            }
//
//            @Override
//            public void onDataChanged() {
//                super.onDataChanged();
//                if(getItemCount() == 0)
//                    progressBar.setVisibility(View.GONE);
//            }
//        };
//        recyclerView.setAdapter(firebaseRecyclerAdapter);
//
//        if(firebaseRecyclerAdapter != null)
//            firebaseRecyclerAdapter.startListening();
//
//    }
//
//
//    public static class CustomerBranchHolder extends RecyclerView.ViewHolder{
//        private static TextView branchID, branchNameTextView,location,companyID, numberOfQueuesTextView;
//
//        public CustomerBranchHolder(View itemView) {
//            super(itemView);
//            branchNameTextView = itemView.findViewById(R.id.item_branch_name_customer);
//            numberOfQueuesTextView = itemView.findViewById(R.id.short_description);
//        }
//
//        public static void setBranch(Branch branch) {
//            String branchName = branch.getBranchName();
//            int branchQueueNumber = branch.getNumberOfQueues();
//            branchNameTextView.setText(branchName);
//            numberOfQueuesTextView.setText(String.valueOf(branchQueueNumber));
//
//        }
//    }
}

