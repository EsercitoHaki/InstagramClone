package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.Models.User;
import com.example.instagram.databinding.ActivitySignUpBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (binding.edtEmail.getEditText().getText().toString().equals("") || binding.edtPassword.getEditText().getText().toString().equals("")){
//                    Toast.makeText(LoginActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
//                }else {
//                    String user = User(binding.edtEmail.getEditText().getText().toString(), binding.edtPassword.getEditText().getText().toString());
//                }
//            }
//        });
    }
}