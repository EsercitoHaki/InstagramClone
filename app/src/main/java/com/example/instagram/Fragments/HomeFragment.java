package com.example.instagram.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.ChatActivity;
import com.example.instagram.R;
import com.example.instagram.Utils.Constant;
import com.example.instagram.databinding.FragmentHomeBinding;
import com.google.android.material.color.utilities.Contrast;

public class HomeFragment extends Fragment {

    public FragmentHomeBinding binding;
    public HomeFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.materialToolbar2);

        // Lấy ra item "message" từ menu và gắn OnClickListener
        binding.materialToolbar2.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Kiểm tra nếu item được nhấn là "message"
                if (item.getItemId() == R.id.message) {
                    // Log ra khi item "message" được nhấn
                    Log.d(Constant.TAG, "Item 'message' clicked");
                    // Tiến hành chuyển đổi fragment
                    SearchFragment searchFragment = new SearchFragment();
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, searchFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    return true;
                }
                return false;
            }
        });
        //
        return binding.getRoot();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}