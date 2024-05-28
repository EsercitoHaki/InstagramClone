package com.example.instagram.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;

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

    public static void uploadVideo(Uri uri, String folderName, Context context, ProgressDialog progressDialog, final VideoUploadCallback callback) {
        progressDialog.setTitle("Upload . . .");
        progressDialog.show();

        FirebaseStorage.getInstance().getReference(folderName)
                .child(UUID.randomUUID().toString())
                .putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri1 -> {
                        String imageUrl = uri1.toString();
                        progressDialog.dismiss();
                        callback.onVideoUploaded(imageUrl);
                    });
                })
                .addOnProgressListener(taskSnapshot -> {
                    long uploadedValue = (taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + uploadedValue + " %");
                });
    }



    public interface ImageUploadCallback {
        void onImageUploaded(String imageUrl);
    }

    public interface VideoUploadCallback {
        void onVideoUploaded(String videoUrl);
    }
}
