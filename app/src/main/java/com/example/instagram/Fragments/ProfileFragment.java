package com.example.instagram.Fragments;

import static com.example.instagram.Utils.Constant.POST;
import static com.example.instagram.Utils.Constant.USER_NODE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.Adapers.MyPostAdapter;
import com.example.instagram.EditProfileActivity;
import com.example.instagram.LoginActivity;
import com.example.instagram.Models.Post;
import com.example.instagram.Models.User;
import com.example.instagram.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    private MyPostAdapter adapter;
    private List<Post> postList = new ArrayList<>();
    private String userId;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
        } else {
            Log.e("ProfileFragment", "firebaseUser is null");
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        Log.d("ProfileFragment", "onCreate - firebaseUser: " + (firebaseUser != null ? firebaseUser.getUid() : "null"));
        Log.d("ProfileFragment", "onCreate - userId: " + userId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

//        loadPosts();

        binding.options.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        binding.btnEditProfile.setOnClickListener(v -> {
            String buttonText = binding.btnEditProfile.getText().toString();

            if (buttonText.equals("Edit Profile")) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            } else if (buttonText.equals("Follow")) {
                followUser();
            } else if (buttonText.equals("Unfollow")) {
                unfollowUser();
            }
        });

        adapter = new MyPostAdapter(requireContext(), postList);
        binding.recycleView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.recycleView.setAdapter(adapter);

        loadPosts();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("ProfileFragment", "onStart - firebaseUser: " + (firebaseUser != null ? firebaseUser.getUid() : "null"));
        Log.d("ProfileFragment", "onStart - userId: " + userId);

        if (firebaseUser != null && userId != null) {
            userInfo();
            if (userId.equals(firebaseUser.getUid())) {
                binding.btnEditProfile.setText("Edit Profile");
            } else {
                checkFollow();
            }
            getFollower();
            getFollowing();

        } else {
            Log.e("ProfileFragment", "firebaseUser or userId is null");
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


    }

    private void userInfo() {
        db.collection(USER_NODE).document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            binding.txtname.setText(user.getName());
                            binding.txtblo.setText(user.getEmail());
                            if (user.getImage() != null && !user.getImage().isEmpty()) {
                                Picasso.get().load(user.getImage()).into(binding.profileImage);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Failed to fetch user info", e);
                });
    }

    private void followUser() {
        Map<String, Object> followingMap = new HashMap<>();
        followingMap.put(userId, true);

        Map<String, Object> followersMap = new HashMap<>();
        followersMap.put(firebaseUser.getUid(), true);

        db.collection("Follow").document(firebaseUser.getUid()).collection("following")
                .document(userId).set(followingMap, SetOptions.merge());

        db.collection("Follow").document(userId).collection("followers")
                .document(firebaseUser.getUid()).set(followersMap, SetOptions.merge());

        binding.btnEditProfile.setText("Unfollow");
    }

    private void unfollowUser() {
        db.collection("Follow").document(firebaseUser.getUid()).collection("following")
                .document(userId).delete();

        db.collection("Follow").document(userId).collection("followers")
                .document(firebaseUser.getUid()).delete();

        binding.btnEditProfile.setText("Follow");
    }

    private void checkFollow() {
        db.collection("Follow").document(firebaseUser.getUid()).collection("following")
                .document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        binding.btnEditProfile.setText("Unfollow");
                    } else {
                        binding.btnEditProfile.setText("Follow");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Failed to check follow status", e);
                });
    }

    private void getFollower() {
        db.collection("Follow").document(userId).collection("followers").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int followerCount = queryDocumentSnapshots.size();
                    binding.tvFollowers.setText(String.valueOf(followerCount));
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Failed to get followers", e);
                });
    }

    private void getFollowing() {
        db.collection("Follow").document(userId).collection("following").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int followerCount = queryDocumentSnapshots.size();
                    binding.tvFollowing.setText(String.valueOf(followerCount));
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Failed to get followers", e);
                });
    }

    private void loadPosts() {
        Log.d("ProfileFragment", "loadPosts() started");

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("ProfileFragment", "Current User ID: " + currentUserID);

        db.collection(POST)
                .whereEqualTo("uid", currentUserID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ProfileFragment", "loadPosts() - Task is successful");

                        postList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            postList.addAll(querySnapshot.toObjects(Post.class));
                            adapter.notifyDataSetChanged();
                            Log.d("ProfileFragment", "loadPosts() - Post list size: " + postList.size());
                        } else {
                            Log.e("ProfileFragment", "loadPosts() - Query snapshot is null");
                        }
                    } else {
                        Log.e("ProfileFragment", "Error loading posts", task.getException());
                    }
                });
    }

}
