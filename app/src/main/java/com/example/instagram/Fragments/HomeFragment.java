package com.example.instagram.Fragments;

import static com.example.instagram.Utils.Constant.POST;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.Adapers.PostAdapter;
import com.example.instagram.ChatRecentActivity1;
import com.example.instagram.Models.Post;
import com.example.instagram.R;

import com.example.instagram.Utils.Constant;
import com.example.instagram.databinding.FragmentHomeBinding;
import com.example.instagram.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ArrayList<Post> postList = new ArrayList<>();
    private PostAdapter adapter;
    public FragmentHomeBinding binding;
    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        adapter = new PostAdapter(requireContext(), postList);
        binding.postRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.postRv.setAdapter(adapter);

        setHasOptionsMenu(true);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.materialToolbar2);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(POST).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Post> tempList = new ArrayList<>();
                postList.clear();

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Post post = document.toObject(Post.class);
                    if (post != null) {
                        tempList.add(post);
                    }
                }

                postList.addAll(tempList);
                adapter.notifyDataSetChanged();

                // Thêm lệnh ghi nhật ký để xem dữ liệu có được lấy và thêm vào postList không
                Log.d("HomeFragment", "Số lượng bài viết đã lấy: " + postList.size());
            }
        }).addOnFailureListener(e -> Log.e("HomeFragment", "Lỗi khi lấy bài viết", e));

        return binding.getRoot();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        // Lấy ra item "message" từ menu và gắn OnClickListener
        MenuItem item = menu.findItem(R.id.message);
//        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                // Kiểm tra nếu item được nhấn là "message"
//                if (item.getItemId() == R.id.message) {
//                    // Log ra khi item "message" được nhấn
//                    Log.d(Constant.TAG, "Item 'message' clicked");
//                    // Tiến hành chuyển đổi fragment
//                    ChatFragment chatFragment = new ChatFragment();
//                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.frameLayout, chatFragment);
//                    transaction.addToBackStack(null);
//                    transaction.commit();
//
//                    return true;
//                }
//                return false;
//            }
//        });
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Kiểm tra nếu item được nhấn là "message"
                if (item.getItemId() == R.id.message) {
                    // Log ra khi item "message" được nhấn
                    Log.d(Constant.TAG, "Item 'message' clicked");

                    Intent intent = new Intent(requireContext(), ChatRecentActivity1.class);

                    // Khởi chạy Activity mới
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });

    }

}