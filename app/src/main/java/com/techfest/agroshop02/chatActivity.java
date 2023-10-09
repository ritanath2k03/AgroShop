package com.techfest.agroshop02;



import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.techfest.agroshop02.network.ApiClient;
import com.techfest.agroshop02.network.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        if (!isReceiverAvailable) {
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.token);

                JSONObject data = new JSONObject();
                data.put(FarmersModel.KEY_USERID, preferanceManager.getString(FarmersModel.KEY_USERID));
                if(preferanceManager.getString(FarmersModel.KEY_FNAME)!=null){
                    data.put(FarmersModel.KEY_FNAME, preferanceManager.getString(FarmersModel.KEY_FNAME));
                }

                if(preferanceManager.getString(FarmersModel.KEY_CNAME)!=null){
                    data.put(FarmersModel.KEY_CNAME, preferanceManager.getString(FarmersModel.KEY_CNAME));
                }
                if(preferanceManager.getString(FarmersModel.KEY_DNAME)!=null){
                    data.put(FarmersModel.KEY_DNAME, preferanceManager.getString(FarmersModel.KEY_DNAME));
                }
                data.put(FarmersModel.KEY_FCM, preferanceManager.getString(FarmersModel.KEY_FCM));
                data.put(FarmersModel.KEY_message, activityChatBinding.editText.getText().toString());

                JSONObject body = new JSONObject();
                body.put(FarmersModel.REMOTE_MSG_DATA, data);
                body.put(FarmersModel.REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());


            }catch (Exception exception) {

//                showToast(exception.getMessage());
            }
        }
        activityChatBinding.editText.setText(null);
//        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CHAT).add(message);
//        activityChatBinding.editText.setText(null);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                FarmersModel.getRemoteMsgHeaders(),messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()){
                    try {
                        if (response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") ==1) {
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                Log.d("Error",error.getString("error"));
                                return;
                            }
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    showToast("Error: " + response.code());
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());

            }
        });
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
                receiverUser.token = value.getString(FarmersModel.KEY_FCM);
                if (receiverUser.image == null) {
                    receiverUser.image = value.getString(FarmersModel.KEY_PICTURE_URI);
                    chatAdapter.setReceiverProfileImage(receiverUser.image);
                    chatAdapter.notifyItemRangeChanged(0,ChatMessages.size());
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
        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_CHAT)
                .whereEqualTo(FarmersModel.KEY_SENDER_ID,preferanceManager.getString(FarmersModel.KEY_USERID))
                .whereEqualTo(FarmersModel.KEY_RECEIVER_ID,receiverUser.id)
                .addSnapshotListener(eventListener);

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