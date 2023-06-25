package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.techfest.agroshop02.adapter.ChatAdapter;
import com.techfest.agroshop02.databinding.ActivityChatBinding;
import com.techfest.agroshop02.databinding.ActivityLoginBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import Models.ChatMassage;
import Models.FarmersModel;
import Models.PreferanceManager;
import Models.User;

public class chatActivity extends AppCompatActivity {
    ActivityChatBinding activityChatBinding;
   private User receiverUser;
   private List<ChatMassage>  chatMassages;
   private ChatAdapter chatAdapter;
   private PreferanceManager preferanceManager;
   private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());
loadReceiverDetails();
setListerners();
init();
listenMassages();
    }

    private void init(){
        preferanceManager=new PreferanceManager(getApplicationContext());
        chatMassages=new ArrayList<>();
        chatAdapter=new ChatAdapter(chatMassages,preferanceManager.getString(FarmersModel.KEY_USERID),receiverUser.image);
        activityChatBinding.chatRecyclerView.setAdapter(chatAdapter);
        firebaseFirestore=FirebaseFirestore.getInstance();
    }
    private void loadReceiverDetails(){
        receiverUser=(User) getIntent().getSerializableExtra(FarmersModel.KEY_USER);
        activityChatBinding.textName.setText(receiverUser.name);
        Picasso.get().load(receiverUser.image).into(activityChatBinding.ChatDp);
    }

    private void setListerners(){
       {
            activityChatBinding.sendBtn.setOnClickListener(view -> {
                        if (activityChatBinding.editText.getText().length()>0) {
                            sendMassage();
                        }
                    }
            );
        }
        activityChatBinding.ChatBack.setOnClickListener(v -> {onBackPressed();});
    }
    private void sendMassage(){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        HashMap<String,Object> massage=new HashMap<>();
        massage.put(FarmersModel.KEY_SENDER_ID,preferanceManager.getString(FarmersModel.KEY_USERID));
        massage.put(FarmersModel.KEY_RECEIVER_ID,receiverUser.id);
        massage.put(FarmersModel.KEY_MASSAGE,activityChatBinding.editText.getText().toString());
        massage.put(FarmersModel.KEY_TIMESTAMP,new Date());
        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER).document(preferanceManager.getString(FarmersModel.KEY_DESIGNATION))
                .collection(auth.getUid()).document("List")
                .collection("ChatList").add(massage);
        activityChatBinding.editText.setText(null);
//        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CHAT).add(massage);
//        activityChatBinding.editText.setText(null);
    }

    private  void listenMassages(){
FirebaseAuth auth=FirebaseAuth.getInstance();
        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER).document(preferanceManager.getString(FarmersModel.KEY_DESIGNATION))
                .collection(auth.getUid()).document("List")
             .collection("ChatList").whereEqualTo(FarmersModel.KEY_SENDER_ID,preferanceManager.getString(FarmersModel.KEY_USERID))
                .whereEqualTo(FarmersModel.KEY_RECEIVER_ID,receiverUser.id)
                .addSnapshotListener(eventListener);
//        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CHAT)
//                .whereEqualTo(FarmersModel.KEY_SENDER_ID,preferanceManager.getString(FarmersModel.KEY_USERID))
//                .whereEqualTo(FarmersModel.KEY_RECEIVER_ID,receiverUser.id)
//                .addSnapshotListener(eventListener);

        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER).document(preferanceManager.getString(FarmersModel.KEY_DESIGNATION))
                .collection(auth.getUid()).document("List")
             .collection("ChatList") .whereEqualTo(FarmersModel.KEY_SENDER_ID,receiverUser.id)
                .whereEqualTo(FarmersModel.KEY_RECEIVER_ID,preferanceManager.getString(FarmersModel.KEY_USERID))
                .addSnapshotListener(eventListener);

//        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CHAT)
//                .whereEqualTo(FarmersModel.KEY_SENDER_ID,receiverUser.id)
//                .whereEqualTo(FarmersModel.KEY_RECEIVER_ID,preferanceManager.getString(FarmersModel.KEY_USERID))
//                .addSnapshotListener(eventListener);

    }

private final EventListener<QuerySnapshot> eventListener=((value, error) -> {
    if(error!=null)return;
    if(value!=null){
        int count=chatMassages.size();
        for(DocumentChange documentChange: value.getDocumentChanges()){
            if(documentChange.getType()==DocumentChange.Type.ADDED)
            {
                ChatMassage chatMassage=new ChatMassage();
                chatMassage.senderId=documentChange.getDocument().getString(FarmersModel.KEY_SENDER_ID);
                chatMassage.receiverId=documentChange.getDocument().getString(FarmersModel.KEY_RECEIVER_ID);
                chatMassage.massage=documentChange.getDocument().getString(FarmersModel.KEY_MASSAGE);
                chatMassage.dateTime=getReadableDateTime(documentChange.getDocument().getDate(FarmersModel.KEY_TIMESTAMP));
                chatMassage.dateObject=documentChange.getDocument().getDate(FarmersModel.KEY_TIMESTAMP);
                chatMassages.add(chatMassage);
            }
        }
        Collections.sort(chatMassages,(obj1,obj2)->obj1.dateObject.compareTo(obj2.dateObject));
        if(count==0){
            chatAdapter.notifyDataSetChanged();
        }else{
            chatAdapter.notifyItemRangeInserted(chatMassages.size(),chatMassages.size());
            activityChatBinding.chatRecyclerView.smoothScrollToPosition(chatMassages.size()-1);

        }
        activityChatBinding.chatRecyclerView.setVisibility(View.VISIBLE);

    }
    activityChatBinding.ProgressBar.setVisibility(View.GONE);

});

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("hh:mm:a", Locale.getDefault()).format(date);
    }
}