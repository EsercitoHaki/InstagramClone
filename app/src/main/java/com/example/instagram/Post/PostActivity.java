package com.example.instagram.Post;

import static com.example.instagram.Utils.Constant.POST_FOLDER;
import static com.example.instagram.Utils.Constant.USER_PROFILE_FOLDER;
import static com.example.instagram.Utils.Utils.uploadImage;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.Utils.Utils;
import com.example.instagram.databinding.ActivityPostBinding;


public class PostActivity extends AppCompatActivity {
    private ActivityPostBinding binding;
    private ActivityPostBinding getBinding() {
        if (binding == null) {
            binding = ActivityPostBinding.inflate(getLayoutInflater());
        }
        return binding;
    }

    private ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        uploadImage(uri, POST_FOLDER, new Utils.ImageUploadCallback() {
                            @Override
                            public void onImageUploaded(String imageUrl1) {
                                if (imageUrl1 != null) {
                                    binding.selectImage.setImageURI(uri);
                                }
                            }
                        });
                    }
                }
            });

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

        binding.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("image/*");
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}