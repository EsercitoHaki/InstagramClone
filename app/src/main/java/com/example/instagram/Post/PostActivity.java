package com.example.instagram.Post;

import static com.example.instagram.Utils.Constant.POST;
import static com.example.instagram.Utils.Constant.POST_FOLDER;
import static com.example.instagram.Utils.Utils.uploadImage;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.instagram.Models.Post;
import com.example.instagram.Utils.Utils;
import com.example.instagram.databinding.ActivityPostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


public class PostActivity extends AppCompatActivity {
    private ActivityPostBinding binding;
    private String imageUrl = null;

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
                                    imageUrl = imageUrl1;
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

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = new Post(imageUrl, binding.caption.getText().toString());
                FirebaseFirestore.getInstance().collection(POST).document().set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseFirestore.getInstance().collection(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .document().set(post)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        finish();
                                    }
                                });
                    }
                });

            }
        });
    }
}