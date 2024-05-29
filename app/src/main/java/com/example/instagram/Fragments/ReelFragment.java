package com.example.instagram.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.instagram.Adapers.ReelAdapter;
import com.example.instagram.Models.Reel;
import com.example.instagram.databinding.FragmentReelBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import javax.annotation.Nullable;

public class ReelFragment extends Fragment {
    private FragmentReelBinding binding;
    private ReelAdapter adapter;
    private ArrayList<Reel> reelList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReelBinding.inflate(inflater, container, false);

        reelList = new ArrayList<>();
        adapter = new ReelAdapter(requireContext(), reelList);
        binding.viewpager.setAdapter(adapter); // Use viewpager to match the ID in your main layout

        FirebaseFirestore.getInstance().collection("REEL").get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Reel> tempList = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Reel reel = document.toObject(Reel.class);
                tempList.add(reel);
            }
            reelList.addAll(tempList);
            Collections.reverse(reelList);
            if (reelList.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                Log.d("ReelFragment", "No data available from Firestore");
                Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("ReelFragment", "Error fetching data from Firestore", e);
        });

        return binding.getRoot();
    }
}
