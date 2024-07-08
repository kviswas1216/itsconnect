package com.example.itsconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.itsconnect.Adapter.ChatRecyclerAdapter;
import com.example.itsconnect.Model.ChatMessageModel;
import com.example.itsconnect.Model.ChatroomModel;
import com.example.itsconnect.Model.UserModel;
import com.example.itsconnect.utilities.Androidutil;
import com.example.itsconnect.utilities.Firebaseutil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActitvity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;

    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_actitvity);

        //get UserModel
        otherUser = Androidutil.getUserModelFromIntent(getIntent());
        chatroomId = Firebaseutil.getChatroomId(Firebaseutil.currentUserId(),otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);

//        Firebaseutil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
//                .addOnCompleteListener(t -> {
//                    if(t.isSuccessful()){
//                        Uri uri  = t.getResult();
//                        AndroidUtil.setProfilePic(this,uri,imageView);
//                    }
//                });
//

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        }));
        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        otherUsername.setText(otherUser.getUsername());

        getOrCreateChatroomModel();
        setupChatRecyclerView();


    }
    void getOrCreateChatroomModel(){
        Firebaseutil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(Firebaseutil.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    Firebaseutil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }
    void sendMessageToUser(String message){

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(Firebaseutil.currentUserId());
        chatroomModel.setLastMessage(message);
        Firebaseutil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,Firebaseutil.currentUserId(),Timestamp.now());
        Firebaseutil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
//                            sendNotification(message);
                        }
                    }
                });
    }
    void setupChatRecyclerView(){
        Query query = Firebaseutil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }
}