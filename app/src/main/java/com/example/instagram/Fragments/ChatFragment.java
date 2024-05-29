package com.example.instagram.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapers.RecentChatRecyclerAdapter;
import com.example.instagram.Models.Chatroom;
import com.example.instagram.R;
import com.example.instagram.Utils.FirebaseUtil;
import com.example.instagram.databinding.FragmentChatBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {
    FragmentChatBinding binding;
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;


    public ChatFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = view.findViewById(R.id.recyler_view);

        binding.backBtn.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Setup RecyclerView
        setupRecyclerView();

        return view;
    }

    void setupRecyclerView(){

        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds",FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Chatroom> options = new FirestoreRecyclerOptions.Builder<Chatroom>()
                .setQuery(query,Chatroom.class).build();

        adapter = new RecentChatRecyclerAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}