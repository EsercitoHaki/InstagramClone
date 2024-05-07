package com.example.instagram;

import static com.example.instagram.Utils.Constant.USER_PROFILE_FOLDER;
import static com.example.instagram.Utils.Utils.uploadImage;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.Models.User;
import com.example.instagram.Utils.Constant;
import com.example.instagram.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.instagram.Utils.Utils;
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
                        uploadImage(uri, USER_PROFILE_FOLDER, new Utils.ImageUploadCallback() {
                            @Override
                            public void onImageUploaded(String imageUrl) {
                                if (imageUrl != null) {
                                    user.setImage(imageUrl);
                                    binding.profileImage.setImageURI(uri);
                                }
                            }
                        });
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = new User();
        String text = "<font color=#FF000000>Already have an Account</font> <font color=#1E88E5>Login ?</font>";
        binding.login.setText(Html.fromHtml(text));

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
                                                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
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

        binding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("image/*");
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}