package com.techfest.agroshop02;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techfest.agroshop02.adapter.RecentConversionsAdapter;
import com.techfest.agroshop02.databinding.ActivityMainBinding;
import com.techfest.agroshop02.listeners.ConversionListner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import Models.ChatMessage;
import Models.FarmersModel;
import Models.PreferanceManager;
import Models.User;

public class MainActivity extends BaseActivity implements ConversionListner {
    private ActivityMainBinding binding;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private List<ChatMessage> conversation;

    private RecentConversionsAdapter conversionsAdapter;
    private FirebaseFirestore database;

    PreferanceManager preferanceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferanceManager=new PreferanceManager(getApplicationContext());
        init();
        loadUserDetails();
getToken();
setListerner();
        listenConvertation();
        binding.swappableRefreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listenConvertation();
                binding.swappableRefreshMain.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void init() {
        conversation = new ArrayList<>();
        conversionsAdapter = new RecentConversionsAdapter(conversation,this);
        binding.conversationsRecyclerView.setAdapter(conversionsAdapter);
        database = FirebaseFirestore.getInstance();
    }
    private  void setListerner(){
        binding.AddContact.setOnClickListener(v -> {startActivity(new Intent(getApplicationContext(),UserActivity.class));});
        binding.signout.setOnClickListener(view -> {signout();});
        binding.mainBack.setOnClickListener(view -> {   Intent intent=new Intent(getApplicationContext(),FarmerDashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);startActivity(intent);});

    }
    private  void loadUserDetails(){

        if(preferanceManager.getString(FarmersModel.KEY_FNAME)!=null)
            binding.TextView.setText(preferanceManager.getString(FarmersModel.KEY_FNAME));

         if (preferanceManager.getString(FarmersModel.KEY_CNAME)!=null)
            binding.TextView.setText(preferanceManager.getString(FarmersModel.KEY_CNAME));
        if (preferanceManager.getString(FarmersModel.KEY_DNAME)!=null)
            binding.TextView.setText(preferanceManager.getString(FarmersModel.KEY_DNAME));
//        Toast.makeText(this, preferanceManager.getString(FarmersModel.KEY_DNAME), Toast.LENGTH_SHORT).show();
    }

    private void listenConvertation(){
        conversation.clear();
        database.collection(FarmersModel.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(FarmersModel.KEY_SENDER_ID,preferanceManager.getString(FarmersModel.KEY_USERID))
                .addSnapshotListener(eventListener);
        database.collection(FarmersModel.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(FarmersModel.KEY_RECEIVER_ID,preferanceManager.getString(FarmersModel.KEY_USERID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener=((value, error) -> {

        if(error!=null){
            return;
        }
        if(value!=null){


            for(DocumentChange documentChange:value.getDocumentChanges()){
                if(documentChange.getType()==DocumentChange.Type.ADDED){
                    String senderId=documentChange.getDocument().getString(FarmersModel.KEY_SENDER_ID);
                    String receiverId=documentChange.getDocument().getString(FarmersModel.KEY_RECEIVER_ID);
ChatMessage ChatMessage=new ChatMessage();
ChatMessage.senderId=senderId;
ChatMessage.receiverId=receiverId;

if(preferanceManager.getString(FarmersModel.KEY_USERID).equals(senderId)){

    ChatMessage.conversionImage=documentChange.getDocument().getString(FarmersModel.KEY_RECEIVER_IMAGE);
    ChatMessage.conversionId=documentChange.getDocument().getString(FarmersModel.KEY_RECEIVER_ID);
    ChatMessage.conversionName=documentChange.getDocument().getString(FarmersModel.KEY_RECEIVER_NAME);

}else{
    ChatMessage.conversionImage=documentChange.getDocument().getString(FarmersModel.KEY_SENDER_IMAGE);
    ChatMessage.conversionId=documentChange.getDocument().getString(FarmersModel.KEY_SENDER_ID);
    ChatMessage.conversionName=documentChange.getDocument().getString(FarmersModel.KEY_SENDER_NAME);

}
        ChatMessage.message=documentChange.getDocument().getString(FarmersModel.KEY_LAST_MESSAGE);
        ChatMessage.dateObject=documentChange.getDocument().getDate(FarmersModel.KEY_TIMESTAMP);
conversation.add(ChatMessage);
                }
                else if(documentChange.getType()==DocumentChange.Type.MODIFIED){

                    for(int i=0;i<conversation.size();i++){
                        String senderId=documentChange.getDocument().getString(FarmersModel.KEY_SENDER_ID);
                        String receiverId=documentChange.getDocument().getString(FarmersModel.KEY_RECEIVER_ID);
                        if(conversation.get(i).equals(senderId)&& conversation.get(i).equals(receiverId)){

                            conversation.get(i).message=documentChange.getDocument().getString(FarmersModel.KEY_LAST_MESSAGE);
                            conversation.get(i).dateObject=documentChange.getDocument().getDate(FarmersModel.KEY_TIMESTAMP);
                            break;
                        }
                    }

                }
            }

           Collections.sort(conversation,(obj1,obj2)->obj2.dateObject.compareTo(obj1.dateObject));
            conversionsAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
            binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
            binding.Progressbarmain.setVisibility(View.GONE);



        }

    });

    private void updateToken(String token){
        preferanceManager.putString(FarmersModel.KEY_FCM, token);
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

        DocumentReference documentReference = firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER)

                .document(preferanceManager.getString(FarmersModel.KEY_USERID));
        documentReference.update(FarmersModel.KEY_FCM, token)

                .addOnFailureListener(e -> Toast.makeText(this, "Unable to Update Token"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

    }

    private  void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);

    }

    private void signout(){
        Toast.makeText(this, "Logging out......", Toast.LENGTH_SHORT).show();
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection(FarmersModel.KEY_COLLECTION_USER)

                .document(preferanceManager.getString(FarmersModel.KEY_USERID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(FarmersModel.KEY_FCM, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused ->
        {
            preferanceManager.clear();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Unable to Logout", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent=new Intent(getApplicationContext(),chatActivity.class);
        intent.putExtra(FarmersModel.KEY_USER,user);
        startActivity(intent);
    }


}