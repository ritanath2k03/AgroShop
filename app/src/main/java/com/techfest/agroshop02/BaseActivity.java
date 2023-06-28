package com.techfest.agroshop02;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import Models.FarmersModel;
import Models.PreferanceManager;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferanceManager preferanceManager = new PreferanceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(FarmersModel.KEY_COLLECTION_USER)
                .document(preferanceManager.getString(FarmersModel.KEY_USERID));

    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(FarmersModel.KEY_AVAILABILITY,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(FarmersModel.KEY_AVAILABILITY, 1);
    }























}
