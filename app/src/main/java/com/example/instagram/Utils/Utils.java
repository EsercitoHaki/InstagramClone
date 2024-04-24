package com.example.instagram.Utils;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class Utils {
    public static void uploadImage(Uri uri, String folderName, final ImageUploadCallback callback) {
        final String[] imageUrl = {null};
        FirebaseStorage.getInstance().getReference(folderName)
                .child(UUID.randomUUID().toString())
                .putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri1 -> {
                                imageUrl[0] = uri1.toString();
                                callback.onImageUploaded(imageUrl[0]);
                            });
                });
    }

    public interface ImageUploadCallback {
        void onImageUploaded(String imageUrl);
    }
}