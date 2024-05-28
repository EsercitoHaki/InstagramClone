package com.example.instagram.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.R;
import com.example.instagram.databinding.FragmentMyPostBinding;

import javax.annotation.Nullable;

public class MyPostFragment extends Fragment {
    private FragmentMyPostBinding binding;
    public MyPostFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMyPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}