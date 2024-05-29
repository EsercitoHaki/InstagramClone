package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.Models.User;
import com.example.instagram.Utils.Constant;
import com.example.instagram.Utils.FirebaseUtil;
import com.example.instagram.databinding.ActivityLoginBinding;
import com.example.instagram.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    private ActivityLoginBinding getBinding()
    {
        if (binding == null) {
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
        }
        return binding;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edtEmail.getEditText().getText().toString().equals("") || binding.edtPassword.getEditText().getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                }else {
                    User user = new User(binding.edtEmail.getEditText().getText().toString(), binding.edtPassword.getEditText().getText().toString());

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (firebaseUser != null) {
                                    // Lấy userId của người dùng hiện tại
                                    String userId = firebaseUser.getUid();
                                    Log.d(Constant.TAG, "User ID: " + userId);
                                    // Lấy thông tin người dùng từ Firestore
                                    DocumentReference userDocument = FirebaseUtil.currentUserDetails();
                                    userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            //code cu:
//                                                    if (task.isSuccessful())
//                                                    {
//                                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                                                        finish();
//                                                    }
                                            //
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    // Tạo một đối tượng User mới từ dữ liệu hiện có
                                                    User currentUser = document.toObject(User.class);
                                                    // Cập nhật userId
                                                    currentUser.setUserId(userId);
                                                    // Lưu thông tin người dùng cập nhật vào Firestore
                                                    userDocument.set(currentUser)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(Constant.TAG, "User info updated in Firestore successfully");
                                                                    // Chuyển đến HomeActivity
                                                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.e(Constant.TAG, "Error updating user info in Firestore", e);
                                                                    // Đăng nhập thất bại, hiển thị thông báo lỗi
                                                                    Toast.makeText(LoginActivity.this, "Error updating user info in Firestore", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Log.d(Constant.TAG, "No such document");
                                                }
                                            } else {
                                                Log.e(Constant.TAG, "Error getting user document", task.getException());
                                                // Đăng nhập thất bại, hiển thị thông báo lỗi
                                                Toast.makeText(LoginActivity.this, "Error getting user document", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(LoginActivity.this, "userId not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        binding.btnCNA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }
}