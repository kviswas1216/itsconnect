package com.example.itsconnect.utilities;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Firebaseutil {
    //we need create collection of users in firestore database
    //for that

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails(){

        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
        // in firebase database form users collection get retrieve the userID

    }


    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }


    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(Firebaseutil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate());
    }


    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

//
//    public static StorageReference getCurrentProfilePicStorageRef(){
//        return FirebaseStorage.getInstance().getReference().child("profile_pic")
//                .child(Firebaseutil.currentUserId());
//    }
//
//    public static StorageReference  getOtherProfilePicStorageRef(String otherUserId){
//        return FirebaseStorage.getInstance().getReference().child("profile_pic")
//                .child(otherUserId);
//    }

}
