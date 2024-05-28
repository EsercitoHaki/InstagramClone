package com.example.instagram.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.Models.User;


public class AndroidUtil {

   public static  void showToast(Context context,String message){
       Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, User model){
       intent.putExtra("name",model.getName());
       intent.putExtra("email",model.getEmail());
       intent.putExtra("image",model.getImage());
//        intent.putExtra("fcmToken",model.getFcmToken());

    }

    public static User getUserModelFromIntent(Intent intent){
        User userModel = new User();
        userModel.setName(intent.getStringExtra("name"));
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setImage(intent.getStringExtra("image"));
//        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
