package com.example.instagram.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.Post.PostActivity;
import com.example.instagram.Post.ReelActivity;
import com.example.instagram.R;
import com.example.instagram.databinding.FragmentAddBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddFragment extends BottomSheetDialogFragment {
    private FragmentAddBinding binding;
    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(inflater, container, false);

        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), PostActivity.class));
            }
        });

        binding.reel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), ReelActivity.class));
            }
        });


        return binding.getRoot();
    }
}