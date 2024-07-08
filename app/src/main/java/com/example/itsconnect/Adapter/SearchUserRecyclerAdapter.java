package com.example.itsconnect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itsconnect.ChatActitvity;
import com.example.itsconnect.Model.UserModel;
import com.example.itsconnect.R;
import com.example.itsconnect.utilities.Androidutil;
import com.example.itsconnect.utilities.Firebaseutil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.usernameText.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());
        if(model.getUserId().equals(Firebaseutil.currentUserId())){
            holder.usernameText.setText(model.getUsername()+" (Me)");
        }

//        Firebaseutil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
//                .addOnCompleteListener(t -> {
//                    if(t.isSuccessful()){
//                        Uri uri  = t.getResult();
//                        Androidutil.setProfilePic(context,uri,holder.profilePic);
//                    }
//                });

        holder.itemView.setOnClickListener(v -> {
            //navigate to chat activity
            Intent intent = new Intent(context, ChatActitvity.class);
            Androidutil.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_recycle,parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }

    }
}
