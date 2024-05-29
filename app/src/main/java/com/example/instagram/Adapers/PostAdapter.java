package com.example.instagram.Adapers;

import static com.example.instagram.Utils.Constant.USER_NODE;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Models.LikeStatus;
import com.example.instagram.Models.Post;
import com.example.instagram.Models.User;
import com.example.instagram.R;
import com.example.instagram.databinding.PostRvBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyHolder> {
    private Context context;
    private ArrayList<Post> postList;

    public PostAdapter(Context context, ArrayList<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostRvBinding binding = PostRvBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        LikeStatus likeStatus = new LikeStatus();
        try {
            String uid = postList.get(holder.getAdapterPosition()).getUid();
            if (uid != null && !uid.isEmpty()) {
                FirebaseFirestore.getInstance().collection(USER_NODE).document(uid).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                if (user != null) {
                                    Glide.with(context).load(user.getImage()).placeholder(R.drawable.user).into(holder.binding.profileImage);
                                    holder.binding.txtname.setText(user.getName());
                                } else {
                                    Log.e("PostAdapter", "User is null at position: " + holder.getAdapterPosition());
                                }
                            }
                        });
            } else {
                Log.e("PostAdapter", "Uid is null or empty at position: " + holder.getAdapterPosition());
            }
        } catch (Exception e) {
            Log.e("PostAdapter", "Exception: " + e.getMessage());
        }

        Glide.with(context).load(postList.get(holder.getAdapterPosition()).getPostUrl()).placeholder(R.drawable.loading).into(holder.binding.postImage);
        try {
            String text = TimeAgo.using(Long.parseLong(postList.get(holder.getAdapterPosition()).getTime()));
            holder.binding.txttime.setText(text);
        } catch (Exception e) {
            holder.binding.txttime.setText("");
        }
        holder.binding.caption.setText(postList.get(holder.getAdapterPosition()).getCaption());

        holder.binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!likeStatus.isLiked()) {
                    holder.binding.like.setImageResource(R.drawable.heart);
                    likeStatus.setLiked(true);
                } else {
                    holder.binding.like.setImageResource(R.drawable.love);
                    likeStatus.setLiked(false);
                }
            }
        });

        holder.binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, postList.get(holder.getAdapterPosition()).getPostUrl());
                context.startActivity(i);
            }
        });
    }



    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private PostRvBinding binding;

        public MyHolder(PostRvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
