package com.example.instagram.Utils;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class Utils {
    public static String uploadImage(Uri uri, String folderName) {
        final String[] imageUrl = {null};
        FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
                .putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl[0] = uri.toString();
                            }
                        });
                    }
                });

        return imageUrl[0];
    }
}