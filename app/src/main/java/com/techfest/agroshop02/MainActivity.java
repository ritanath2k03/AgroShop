package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techfest.agroshop02.databinding.ActivityMainBinding;

import java.util.HashMap;

import Models.FarmersModel;
import Models.PreferanceManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    PreferanceManager preferanceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferanceManager=new PreferanceManager(getApplicationContext());
        loadUserDetails();
getToken();
setListerners();
    }
    private  void loadUserDetails(){
        if(preferanceManager.getString(FarmersModel.KEY_FNAME)!=null)
            binding.TextView.setText(preferanceManager.getString(FarmersModel.KEY_FNAME));
        else if (preferanceManager.getString(FarmersModel.KEY_CNAME)!=null)
            binding.TextView.setText(preferanceManager.getString(FarmersModel.KEY_CNAME));
        else if (preferanceManager.getString(FarmersModel.KEY_DNAME)!=null)
            binding.TextView.setText(preferanceManager.getString(FarmersModel.KEY_DNAME));
    }
    private void updateToken(String token){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER)

                .document(preferanceManager.getString(FarmersModel.KEY_USERID));
        documentReference.update(FarmersModel.KEY_FCM,token)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Token Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Unable to Update Token", Toast.LENGTH_SHORT).show());
    }
    private  void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);

    }
    private void signout(){
        Toast.makeText(this, "Logging out......", Toast.LENGTH_SHORT).show();
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firestore.collection(FarmersModel.KEY_COLLECTION_USER)
                .document(preferanceManager.getString(FarmersModel.KEY_USERID));
        HashMap<String,Object> updates=new HashMap<>();
        updates.put(FarmersModel.KEY_FCM, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused ->
        {preferanceManager.clear();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Unable to Logout", Toast.LENGTH_SHORT).show();
        });
    }

    private void setListerners(){
        binding.TextView.setOnClickListener(view -> signout());
    }

}