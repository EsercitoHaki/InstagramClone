package com.example.instagram;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instagram.databinding.ActivityEditProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "EditProfileActivity";

    private ActivityEditProfileBinding binding;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firestore, FirebaseAuth and FirebaseStorage
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Set listeners
        binding.save.setOnClickListener(v -> saveProfile());
        binding.close.setOnClickListener(v -> finish());
        binding.tvChange.setOnClickListener(v -> openImagePicker());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
        loadUserProfile();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.imageProfile.setImageURI(imageUri);
        }
    }

    private void loadUserProfile() {
        String userId = auth.getCurrentUser().getUid();
        DocumentReference userRef = firestore.collection("User").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String imageUrl = documentSnapshot.getString("image");

                binding.editName.setText(name);
                binding.editEmail.setText(email);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this).load(imageUrl).into(binding.imageProfile);
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error loading profile", e));
    }

    private void saveProfile() {
        String userId = auth.getCurrentUser().getUid();
        String name = binding.editName.getText().toString();
        String email = binding.editEmail.getText().toString();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("email", email);

        if (imageUri != null) {
            uploadImageAndSaveProfile(userId, userProfile);
        } else {
            saveProfileToFirestore(userId, userProfile);
        }
    }

    private void uploadImageAndSaveProfile(String userId, Map<String, Object> userProfile) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("profile_images/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            userProfile.put("image", uri.toString());
                            saveProfileToFirestore(userId, userProfile);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditProfileActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to get image URL", e);
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to upload image", e);
                });
    }

    private void saveProfileToFirestore(String userId, Map<String, Object> userProfile) {
        DocumentReference userRef = firestore.collection("User").document(userId);
        userRef.update(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating profile", e);
                });
    }
}

