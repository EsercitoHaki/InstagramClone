package com.example.instagram.Adapers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Models.User;
import com.example.instagram.R;
import com.example.instagram.Utils.AndroidUtil;
import com.example.instagram.Utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserToMessageAdapter extends FirestoreRecyclerAdapter<User, SearchUserToMessageAdapter.SearchUserViewHolder> {
    Context context;
    public SearchUserToMessageAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchUserViewHolder holder, int position, @NonNull User user) {
        holder.usernameText.setText(user.getName());
        holder.phoneText.setText(user.getEmail());

        if (user.getImage() != null && !user.getImage().isEmpty()) {
            Uri profilePicUri = Uri.parse(user.getImage());
            AndroidUtil.setProfilePic(context, profilePicUri, holder.profilePic);
        } else {
            holder.profilePic.setImageResource(R.drawable.person_icon);
        }

//        holder.itemView.setOnClickListener(v -> {
//            // Handle item click event
//            // Navigate to chat activity or any other desired activity
//            Intent intent = new Intent(context, ChatActivity.class);
//            AndroidUtil.passUserModelAsIntent(intent, user);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        });
    }

    @NonNull
    @Override
    public SearchUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);
        return new SearchUserViewHolder(view);
    }

    class SearchUserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;
        public SearchUserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}