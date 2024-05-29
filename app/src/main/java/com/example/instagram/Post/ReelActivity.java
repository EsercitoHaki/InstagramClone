package com.example.instagram.Post;

import static com.example.instagram.Utils.Constant.REEL;
import static com.example.instagram.Utils.Constant.REEL_FOLDER;
import static com.example.instagram.Utils.Constant.USER_NODE;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.instagram.HomeActivity;
import com.example.instagram.Models.Reel;
import com.example.instagram.Models.User;
import com.example.instagram.R;
import com.example.instagram.Utils.Utils;
import com.example.instagram.databinding.ActivityReelBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReelActivity extends AppCompatActivity {
    private ActivityReelBinding binding;

    private ActivityReelBinding getBinding() {
        if (binding == null) {
            binding = ActivityReelBinding.inflate(getLayoutInflater());
        }
        return binding;
    }

    private String videoUrl;

    private ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        ProgressDialog progressDialog = new ProgressDialog(ReelActivity.this);
                        Utils.uploadVideo(uri, REEL_FOLDER, ReelActivity.this, progressDialog, new Utils.VideoUploadCallback() {
                            @Override
                            public void onVideoUploaded(String url) {
                                if (url != null) {
                                    videoUrl = url;
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
                startActivity(new Intent(ReelActivity.this, HomeActivity.class));
                finish();
            }
        });
        binding.selectReel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("video/*");
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReelActivity.this, HomeActivity.class));
                finish();
            }
        });

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection(USER_NODE).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        Reel reel = new Reel(videoUrl, binding.caption.getText().toString(), user.getImage());
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection("REEL").document()
                                .set(reel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        firestore.collection(currentUserUid + "REEL").document()
                                                .set(reel)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        startActivity(new Intent(ReelActivity.this, HomeActivity.class));
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                    }
                });

            }
        });
    }
}
