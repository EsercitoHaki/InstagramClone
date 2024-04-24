package com.example.instagram;

import static com.example.instagram.Utils.Constant.USER_PROFILE_FOLDER;
import static com.example.instagram.Utils.Utils.uploadImage;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.Models.User;
import com.example.instagram.Utils.Constant;
import com.example.instagram.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private User user;

    private ActivitySignUpBinding getBinding() {
        if (binding == null) {
            binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        }
        return binding;
    }

    private ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        user.setImage((uploadImage(uri, USER_PROFILE_FOLDER)));
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = new User();


        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edtName.getEditText().getText().toString().equals("") ||
                        binding.edtEmail.getEditText().getText().toString().equals("") ||
                        binding.edtPassword.getEditText().getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all information", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                            binding.edtEmail.getEditText().getText().toString(),
                            binding.edtPassword.getEditText().getText().toString()
                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user.setName(binding.edtName.getEditText().getText().toString());
                                user.setEmail(binding.edtEmail.getEditText().getText().toString());
                                user.setPassword(binding.edtPassword.getEditText().getText().toString());

                                FirebaseFirestore.getInstance().collection(Constant.USER_NODE).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignUpActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }else {
                                Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}