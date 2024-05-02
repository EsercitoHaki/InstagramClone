package com.example.instagram.Post;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.instagram.databinding.ActivityPostBinding;


public class PostActivity extends AppCompatActivity {
    private ActivityPostBinding binding;
    private ActivityPostBinding getBinding() {
        if (binding == null) {
            binding = ActivityPostBinding.inflate(getLayoutInflater());
        }
        return binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getBinding();
        setContentView(binding.getRoot());

        setSupportActionBar(binding.materialToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        binding.materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}