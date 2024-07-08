package com.example.itsconnect.Adapter;

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

import com.example.itsconnect.ChatActitvity;
import com.example.itsconnect.R;
import com.example.itsconnect.Model.ChatroomModel;
import com.example.itsconnect.Model.UserModel;
import com.example.itsconnect.utilities.Androidutil;
import com.example.itsconnect.utilities.Firebaseutil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentChatRecyler extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyler.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyler(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {

        Firebaseutil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        holder.usernameText.setText(otherUserModel.getUsername());
                        holder.lastMessageText.setText(model.getLastMessage());
                        holder.lastMessageTime.setText(Firebaseutil.timestampToString(model.getLastMessageTimestamp()));

                        // Uncomment the following lines if you want to set the profile picture as well
                        // Firebaseutil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                        //         .addOnCompleteListener(t -> {
                        //             if (t.isSuccessful()) {
                        //                 Uri uri = t.getResult();
                        //                 Androidutil.setProfilePic(context, uri, holder.profilePic);
                        //             }
                        //         });

                        holder.itemView.setOnClickListener(v -> {
                            // Navigate to chat activity
                            Intent intent = new Intent(context, ChatActitvity.class);
                            Androidutil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
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
