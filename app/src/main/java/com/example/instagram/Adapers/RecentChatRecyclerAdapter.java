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

import com.example.instagram.ChatActivity;
import com.example.instagram.R;
import com.example.instagram.Models.Chatroom;
import com.example.instagram.Models.User;
import com.example.instagram.Utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<Chatroom, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Chatroom> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull Chatroom model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                            User otherUserModel = task.getResult().toObject(User.class);

                            FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                    .addOnCompleteListener(t -> {
                                        if(t.isSuccessful()){
                                            Uri uri  = t.getResult();
                                            FirebaseUtil.setProfilePic(context,uri,holder.profilePic);
                                        }
                                    });

                            holder.usernameText.setText(otherUserModel.getName());
                            if(lastMessageSentByMe)
                                holder.lastMessageText.setText("You : "+model.getLastMessage());
                            else
                                holder.lastMessageText.setText(model.getLastMessage());
                            holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                            if (otherUserModel.getImage() != null && !otherUserModel.getImage().isEmpty()) {
                                Uri profilePicUri = Uri.parse(otherUserModel.getImage());
                                FirebaseUtil.setProfilePic(context, profilePicUri, holder.profilePic);
                            } else {
                                holder.profilePic.setImageResource(R.drawable.person_icon);
                            }

                            holder.itemView.setOnClickListener(v -> {
                                //navigate to chat activity
                                Intent intent = new Intent(context, ChatActivity.class);
                                FirebaseUtil.passUserModelAsIntent(intent,otherUserModel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });

                        }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
