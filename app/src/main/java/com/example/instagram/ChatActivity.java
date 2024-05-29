package com.example.instagram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapers.ChatRecyclerAdapter;
import com.example.instagram.Models.ChatMessage;
import com.example.instagram.Models.Chatroom;
import com.example.instagram.Models.User;
import com.example.instagram.Utils.Constant;
import com.example.instagram.Utils.FirebaseUtil;
import com.example.instagram.databinding.ActivityChatBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private User otherUser;
    private String chatroomId;
    private Chatroom chatroomModel;
    private ChatRecyclerAdapter adapter;

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //quan ly activity_chat.xml
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get UserModel
        otherUser = FirebaseUtil.getUserModelFromIntent(getIntent());
        if (otherUser != null) {
            Log.d(Constant.TAG, "Name from item: " + otherUser.getName());
            Log.d(Constant.TAG, "userId from item: " + otherUser.getUserId());
            Log.d(Constant.TAG, " image " + otherUser.getImage());
        } else {
            Log.d(Constant.TAG, "No user data received from Intent");
        }

        Log.d(Constant.TAG, "currentuserId from Firebase:  " + FirebaseUtil.currentUserId());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());
        Log.d(Constant.TAG, "ChatroomId: " + chatroomId);

        if (otherUser.getImage() != null && !otherUser.getImage().isEmpty()) {
            FirebaseUtil.setProfilePic(this, Uri.parse(otherUser.getImage()), binding.profilePicLayout.profilePicImageView);
        } else {
            binding.profilePicLayout.profilePicImageView.setImageResource(R.drawable.person_icon);
        }
//        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Uri uri = task.getResult();
//                        if (uri != null) {
//                            Log.d(Constant.TAG, "Download URL: " + uri.toString());
//                            FirebaseUtil.setProfilePic(this, uri, binding.profilePicLayout.profilePicImageView);
//                        } else {
//                            Log.e(Constant.TAG, "Download URL is null");
//                        }
//                    } else {
//                        Log.e(Constant.TAG, "Failed to retrieve download URL: " + task.getException());
//                    }
//                });

//        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
//                .addOnCompleteListener(t -> {
//                    if (t.isSuccessful()) {
//                        Uri uri = t.getResult();
//                        FirebaseUtil.setProfilePic(this, uri, binding.profilePicLayout.profilePicImageView);
//                    }
//                });







        binding.backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });
        binding.otherUsername.setText(otherUser.getName());

        binding.messageSendBtn.setOnClickListener((v -> {
            String message = binding.chatMessageInput.getText().toString().trim();
            if (message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    private void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(Chatroom.class);
                if (chatroomModel == null) {
                    // First time chat
                    chatroomModel = new Chatroom(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        binding.chatRecyclerView.setLayoutManager(manager);
        binding.chatRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                binding.chatRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessageToUser(String message) {
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessage chatMessageModel = new ChatMessage(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            binding.chatMessageInput.setText("");
                            // sendNotification(message);
                        }
                    }
                });
    }
    //ngat o day

//    User otherUser;
//    String chatroomId;
//    Chatroom chatroomModel;
//    ChatRecyclerAdapter adapter;
//
//    EditText messageInput;
//    ImageButton sendMessageBtn;
//    ImageButton backBtn;
//    TextView otherUsername;
//    RecyclerView recyclerView;
//    ImageView imageView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_chat);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        //get UserModel
//        otherUser = FirebaseUtil.getUserModelFromIntent(getIntent());
//
//        if (otherUser != null) {
//            Log.d(Constant.TAG, "Name from item: " + otherUser.getName());
//            Log.d(Constant.TAG, "userId from item: " + otherUser.getUserId());
//        } else {
//            Log.d(Constant.TAG, "No user data received from Intent");
//        }
//
//        Log.d(Constant.TAG, "currentuserId from Firebase:  " + FirebaseUtil.currentUserId());
//        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId());
//        Log.d(Constant.TAG, "ChatroomId: " + chatroomId);
//
//        messageInput = findViewById(R.id.chat_message_input);
//        sendMessageBtn = findViewById(R.id.message_send_btn);
//        backBtn = findViewById(R.id.back_btn);
//        otherUsername = findViewById(R.id.other_username);
//        recyclerView = findViewById(R.id.chat_recycler_view);
//        imageView = findViewById(R.id.profile_pic_image_view);
//
//        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
//                .addOnCompleteListener(t -> {
//                    if(t.isSuccessful()){
//                        Uri uri  = t.getResult();
//                        FirebaseUtil.setProfilePic(this,uri,imageView);
//                    }
//                });
//
//        backBtn.setOnClickListener((v)->{
//            onBackPressed();
//        });
//        otherUsername.setText(otherUser.getName());
//
//        sendMessageBtn.setOnClickListener((v -> {
//            String message = messageInput.getText().toString().trim();
//            if(message.isEmpty())
//                return;
//            sendMessageToUser(message);
//        }));
//
//        getOrCreateChatroomModel();
//        setupChatRecyclerView();
//
//    }
//    void getOrCreateChatroomModel(){
//        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                chatroomModel = task.getResult().toObject(Chatroom.class);
//                if(chatroomModel==null){
//                    //first time chat
//                    chatroomModel = new Chatroom(
//                            chatroomId,
//                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
//                            Timestamp.now(),
//                            ""
//                    );
//                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
//                }
//            }
//        });
//    }
//    void setupChatRecyclerView(){
//        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
//                .orderBy("timestamp", Query.Direction.DESCENDING);
//
//        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
//                .setQuery(query,ChatMessage.class).build();
//
//        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setReverseLayout(true);
//        recyclerView.setLayoutManager(manager);
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();
//        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                recyclerView.smoothScrollToPosition(0);
//            }
//        });
//    }
//
//    void sendMessageToUser(String message){
//
//        chatroomModel.setLastMessageTimestamp(Timestamp.now());
//        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
//        chatroomModel.setLastMessage(message);
//        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
//
//        ChatMessage chatMessageModel = new ChatMessage(message,FirebaseUtil.currentUserId(),Timestamp.now());
//        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
//                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        if(task.isSuccessful()){
//                            messageInput.setText("");
////                            sendNotification(message);
//                        }
//                    }
//                });
//    }

    //ngat o day
//    void sendNotification(String message){
//
//        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                User currentUser = task.getResult().toObject(User.class);
//                try{
//                    JSONObject jsonObject  = new JSONObject();
//
//                    JSONObject notificationObj = new JSONObject();
//                    notificationObj.put("title",currentUser.getName());
//                    notificationObj.put("body",message);
//
//                    JSONObject dataObj = new JSONObject();
//                    dataObj.put("userId",currentUser.getUserId());
//
//                    jsonObject.put("notification",notificationObj);
//                    jsonObject.put("data",dataObj);
//                    jsonObject.put("to",otherUser.getFcmToken());
//
//                    callApi(jsonObject);
//
//
//                }catch (Exception e){
//
//                }
//
//            }
//        });
//
//    }
//    void callApi(JSONObject jsonObject){
//        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//        OkHttpClient client = new OkHttpClient();
//        String url = "https://fcm.googleapis.com/fcm/send";
//        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .header("Authorization","Bearer YOUR_API_KEY")
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//
//            }
//        });
//
//    }
}

