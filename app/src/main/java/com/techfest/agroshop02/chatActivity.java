package com.techfest.agroshop02;



import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import Models.ChatMessage;
import Models.FarmersModel;
import Models.PreferanceManager;
import Models.User;

public class chatActivity extends BaseActivity {
    ActivityChatBinding activityChatBinding;
   private User receiverUser;
   private List<ChatMessage>  ChatMessages;
   private ChatAdapter chatAdapter;
   private PreferanceManager preferanceManager;
   private FirebaseFirestore firebaseFirestore;
   private String conversionId = null;
   private Boolean isReceiverAvailable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());
loadReceiverDetails();
setListeners();
init();
listenmessages();


    }

    private void init(){
        preferanceManager=new PreferanceManager(getApplicationContext());
        ChatMessages=new ArrayList<>();
        chatAdapter=new ChatAdapter(ChatMessages,preferanceManager.getString(FarmersModel.KEY_USERID),receiverUser.image);
        activityChatBinding.chatRecyclerView.setAdapter(chatAdapter);
        firebaseFirestore=FirebaseFirestore.getInstance();
    }
    private void loadReceiverDetails(){
        receiverUser=(User) getIntent().getSerializableExtra(FarmersModel.KEY_USER);
        activityChatBinding.textName.setText(receiverUser.name);
        Picasso.get().load(receiverUser.image).into(activityChatBinding.ChatDp);
    }

    private void setListeners(){
       {
            activityChatBinding.sendBtn.setOnClickListener(view -> {
                        if (activityChatBinding.editText.getText().length()>0) {
                            sendmessage();
                        }
                    }
            );
        }
        activityChatBinding.ChatBack.setOnClickListener(v -> {onBackPressed();});
    }
    private void sendmessage(){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        HashMap<String,Object> message=new HashMap<>();
        message.put(FarmersModel.KEY_SENDER_ID,preferanceManager.getString(FarmersModel.KEY_USERID));
        message.put(FarmersModel.KEY_RECEIVER_ID,receiverUser.id);
        message.put(FarmersModel.KEY_message,activityChatBinding.editText.getText().toString());
        message.put(FarmersModel.KEY_TIMESTAMP,new Date());
        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CHAT).add(message);

        if (conversionId != null) {
            updateConversion(activityChatBinding.editText.getText().toString());
        } else {
           HashMap<String,Object> conversion  = new HashMap<>();
           conversion.put(FarmersModel.KEY_SENDER_ID, preferanceManager.getString(FarmersModel.KEY_USERID));
   if(preferanceManager.getString(FarmersModel.KEY_FNAME)!=null){
       conversion.put(FarmersModel.KEY_SENDER_NAME,preferanceManager.getString(FarmersModel.KEY_FNAME));
   }
            if(preferanceManager.getString(FarmersModel.KEY_DNAME)!=null){
                conversion.put(FarmersModel.KEY_SENDER_NAME,preferanceManager.getString(FarmersModel.KEY_DNAME));
            }
            if(preferanceManager.getString(FarmersModel.KEY_CNAME)!=null){
                conversion.put(FarmersModel.KEY_SENDER_NAME,preferanceManager.getString(FarmersModel.KEY_CNAME));
            }

            conversion.put(FarmersModel.KEY_SENDER_IMAGE,preferanceManager.getString(FarmersModel.KEY_PICTURE_URI));
            conversion.put(FarmersModel.KEY_RECEIVER_ID,receiverUser.id);
            conversion.put(FarmersModel.KEY_RECEIVER_NAME, receiverUser.name);
            conversion.put(FarmersModel.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(FarmersModel.KEY_LAST_MESSAGE,activityChatBinding.editText.getText().toString());
            conversion.put(FarmersModel.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        activityChatBinding.editText.setText(null);
//        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CHAT).add(message);
//        activityChatBinding.editText.setText(null);
    }

    private void listenAvailabilityOfReceiver() {
        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER).document(
                receiverUser.id
        ).addSnapshotListener(chatActivity.this,(value, error) -> {
            if(error != null) {
                return;
            }
            if(value != null) {
                if (value.getLong(FarmersModel.KEY_AVAILABILITY) != null) {
                    int availability = Objects.requireNonNull(
                            value.getLong(FarmersModel.KEY_AVAILABILITY)
                    ).intValue();
                    isReceiverAvailable = availability == 1;
                }
            }
            if(isReceiverAvailable) {
                activityChatBinding.textAvailability.setVisibility(View.VISIBLE);
            } else {
                activityChatBinding.textAvailability.setVisibility(View.GONE);
            }
        });
    }

    private  void listenmessages(){
FirebaseAuth auth=FirebaseAuth.getInstance();
        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CHAT).whereEqualTo(FarmersModel.KEY_SENDER_ID,preferanceManager.getString(FarmersModel.KEY_USERID))
                .whereEqualTo(FarmersModel.KEY_RECEIVER_ID,receiverUser.id)
                .addSnapshotListener(eventListener);
//        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CHAT)
//                .whereEqualTo(FarmersModel.KEY_SENDER_ID,preferanceManager.getString(FarmersModel.KEY_USERID))
//                .whereEqualTo(FarmersModel.KEY_RECEIVER_ID,receiverUser.id)
//                .addSnapshotListener(eventListener);

        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CHAT).whereEqualTo(FarmersModel.KEY_SENDER_ID,receiverUser.id)
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
        int count=ChatMessages.size();
        for(DocumentChange documentChange: value.getDocumentChanges()){
            if(documentChange.getType()==DocumentChange.Type.ADDED)
            {Log.d("AutoId",documentChange.getDocument().getId());
                ChatMessage ChatMessage=new ChatMessage();
                ChatMessage.senderId=documentChange.getDocument().getString(FarmersModel.KEY_SENDER_ID);
                ChatMessage.receiverId=documentChange.getDocument().getString(FarmersModel.KEY_RECEIVER_ID);
                ChatMessage.message=documentChange.getDocument().getString(FarmersModel.KEY_message);
                ChatMessage.dateTime=getReadableDateTime(documentChange.getDocument().getDate(FarmersModel.KEY_TIMESTAMP));
                ChatMessage.dateObject=documentChange.getDocument().getDate(FarmersModel.KEY_TIMESTAMP);
                ChatMessages.add(ChatMessage);
            }
        }
        Collections.sort(ChatMessages,(obj1,obj2)->obj1.dateObject.compareTo(obj2.dateObject));
        if(count==0){
            chatAdapter.notifyDataSetChanged();
        }else{
            chatAdapter.notifyItemRangeInserted(ChatMessages.size(),ChatMessages.size());
            activityChatBinding.chatRecyclerView.smoothScrollToPosition(ChatMessages.size()-1);

        }
        activityChatBinding.chatRecyclerView.setVisibility(View.VISIBLE);

    }
    activityChatBinding.ProgressBar.setVisibility(View.GONE);
    if (conversionId == null) {
        checkForConversaion();
    }

});

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("hh:mm:a", Locale.getDefault()).format(date);
    }

    private void addConversion(Map<String, Object> conversion) {
        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());


    }
    private void updateConversion(String message) {
        DocumentReference documentReference =

                firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                FarmersModel.KEY_LAST_MESSAGE, message,
                FarmersModel.KEY_TIMESTAMP, new Date()
        );
    }


    private void checkForConversaion(){
        if (ChatMessages.size() != 0) {
            checkForConversionRemotely(
                    preferanceManager.getString(FarmersModel.KEY_USERID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferanceManager.getString(FarmersModel.KEY_USERID)
            );
        }
    }
    private void checkForConversionRemotely(String senderId, String receiverId){
        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(FarmersModel.KEY_SENDER_ID, senderId)
                .whereEqualTo(FarmersModel.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }

    };


    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}