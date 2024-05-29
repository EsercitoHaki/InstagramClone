package com.example.instagram;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapers.RecentChatRecyclerAdapter;
import com.example.instagram.Models.Chatroom;
import com.example.instagram.Utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatRecentActivity1 extends AppCompatActivity {


        RecyclerView recyclerView;
        RecentChatRecyclerAdapter adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat_recent1);

            EdgeToEdge.enable(this);

            recyclerView = findViewById(R.id.recyler_view);
            findViewById(R.id.back_btn).setOnClickListener(v -> onBackPressed());
            setupRecyclerView();
        }

        void setupRecyclerView() {
            Query query = FirebaseUtil.allChatroomCollectionReference()
                    .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                    .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<Chatroom> options = new FirestoreRecyclerOptions.Builder<Chatroom>()
                    .setQuery(query, Chatroom.class).build();

            adapter = new RecentChatRecyclerAdapter(options, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }

        @Override
        protected void onStart() {
            super.onStart();
            if (adapter != null)
                adapter.startListening();
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (adapter != null)
                adapter.stopListening();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onResume() {
            super.onResume();
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }
