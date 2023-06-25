package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techfest.agroshop02.adapter.ChatAdapter;
import com.techfest.agroshop02.databinding.ActivityChatBinding;
import com.techfest.agroshop02.databinding.ActivityLoginBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    }

    private void init(){
        preferanceManager=new PreferanceManager(getApplicationContext());
        chatMassages=new ArrayList<>();
        chatAdapter=new ChatAdapter(chatMassages,receiverUser.image,preferanceManager.getString(FarmersModel.KEY_USERID));
        activityChatBinding.chatRecyclerView.setAdapter(chatAdapter);
        firebaseFirestore=FirebaseFirestore.getInstance();
    }
    private void loadReceiverDetails(){
        receiverUser=(User) getIntent().getSerializableExtra(FarmersModel.KEY_USER);
        activityChatBinding.textName.setText(receiverUser.name);
    }
    private void setListerners(){
        activityChatBinding.sendBtn.setOnClickListener(view ->sendMassage() );
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
                .collection(auth.getUid()).document("Chat")
                .collection("ChatList").add(massage);
        activityChatBinding.editText.setText(null);
    }
}