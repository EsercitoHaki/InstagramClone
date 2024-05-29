package com.example.instagram.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.instagram.Adapers.SearchUserToMessageAdapter;
import com.example.instagram.Models.User;
import com.example.instagram.R;
import com.example.instagram.Utils.FirebaseUtil;
import com.example.instagram.databinding.FragmentSearchUserBinding;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchFragment extends Fragment {

    private FragmentSearchUserBinding binding;
    private SearchUserToMessageAdapter adapter;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchUserBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        binding.seachUsernameInput.requestFocus();

        binding.backBtn.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        binding.searchUserBtn.setOnClickListener(v -> {
            String searchTerm = binding.seachUsernameInput.getText().toString();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                binding.seachUsernameInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });

        return rootView;
    }

    void setupSearchRecyclerView(String searchTerm) {
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("name", searchTerm)
                .whereLessThanOrEqualTo("name", searchTerm + '\uf8ff');

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();

        adapter = new SearchUserToMessageAdapter(options, requireContext());
        binding.searchUserRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.searchUserRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
}

//public class SearchFragment extends Fragment {
//
//
//    EditText searchInput;
//    ImageButton searchButton;
//    ImageButton backButton;
//    RecyclerView recyclerView;
//
//    SearchUserToMessageAdapter adapter;
//
//    public SearchFragment() {
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        FragmentSearchBinding binding = FragmentSearchBinding.inflate(inflater, container, false);
//        View rootView = binding.getRoot();
//
//        searchInput = binding.seachUsernameInput;
//        searchButton = binding.searchUserBtn;
////        backButton = binding.backBtn; // Un-comment this line
//        recyclerView = binding.searchUserRecyclerView;
//
//        searchInput.requestFocus();
//
////        backButton.setOnClickListener(v -> {
////            requireActivity().onBackPressed();
////        });
//
//        searchButton.setOnClickListener(v -> {
//            String searchTerm = searchInput.getText().toString();
//            if(searchTerm.isEmpty() || searchTerm.length() < 3) {
//                searchInput.setError("Invalid Username");
//                return;
//            }
//            setupSearchRecyclerView(searchTerm);
//        });
//
//        return rootView;
//    }
//
//    void setupSearchRecyclerView(String searchTerm) {
//        Query query = FirebaseUtil.allUserCollectionReference()
//                .whereGreaterThanOrEqualTo("name", searchTerm)
//                .whereLessThanOrEqualTo("name", searchTerm + '\uf8ff');
//
//        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
//                .setQuery(query, User.class).build();
//
//        adapter = new SearchUserToMessageAdapter(options, requireContext());
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (adapter != null)
//            adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (adapter != null)
//            adapter.stopListening();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (adapter != null)
//            adapter.startListening();
//    }
//}
