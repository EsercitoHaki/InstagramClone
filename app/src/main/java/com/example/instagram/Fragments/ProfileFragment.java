package com.example.instagram.Fragments;

import static com.example.instagram.Utils.Constant.USER_NODE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.Adapers.ViewPagerAdapter;
import com.example.instagram.Models.User;
import com.example.instagram.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ViewPagerAdapter viewPagerAdapter;
    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewPagerAdapter = new ViewPagerAdapter(requireActivity().getSupportFragmentManager());
        viewPagerAdapter.addFragment(new MyPostFragment(), "My Post");
        viewPagerAdapter.addFragment(new MyReelFragment(), "My Reel");
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        return binding.getRoot();
    }

    @Override
    public void onStart() {

        super.onStart();
        FirebaseFirestore.getInstance().collection(USER_NODE).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null)
                            {
                                binding.txtname.setText(user.getName());
                                binding.txtblo.setText(user.getPassword());
                            }
                            if (user.getImage() != null && !user.getImage().isEmpty()){
                                Picasso.get().load(user.getImage()).into(binding.profileImage);
                            }
                        }
                    }
                });
    }
}