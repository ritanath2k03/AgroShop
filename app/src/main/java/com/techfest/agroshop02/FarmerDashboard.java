package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.techfest.agroshop02.databinding.ActivityFarmerDashboardBinding;

import Models.FarmersModel;
import Models.PreferanceManager;
import Models.User;

public class FarmerDashboard extends AppCompatActivity {
ActivityFarmerDashboardBinding binding;
PreferanceManager preferanceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFarmerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferanceManager=new PreferanceManager(getApplicationContext());
        getDashboardData();
        setListners();
    }

    private void setListners() {
        binding.chat.setOnClickListener(view -> {startActivity(new Intent(getApplicationContext(),MainActivity.class));});
    }

    private void getDashboardData() {
        loading(true);
        String currentUser =preferanceManager.getString(FarmersModel.KEY_USERID);
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER).get()
                .addOnCompleteListener(task -> {
                    loading(false);
                   if(task.isSuccessful()&&task.getResult()!=null){
                       for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){

                           //for skipping the current user
                           if(currentUser.equals((queryDocumentSnapshot.getId()))){
                               //getting details
                               Picasso.get().load(queryDocumentSnapshot.getString(FarmersModel.KEY_PICTURE_URI)).into(binding.profileImage);
                               binding.DashboardUserName.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_USERNAME));
                               binding.PersonDesignation.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_DESIGNATION));
                               binding.PersonPhone.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_PHONE_NUMBER));
                               binding.PersonEmail.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_EMAIL));
                               binding.PersonBio.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_BIO));
                               binding.PersonAge.setText("AGE-> "+queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_AGE));
                               binding.PersonLocation.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_LOCATION));

                               //setting details
                               preferanceManager.putString(FarmersModel.KEY_PHONE_NUMBER,queryDocumentSnapshot.getString(FarmersModel.KEY_PHONE_NUMBER));
                               preferanceManager.putString(FarmersModel.KEY_EMAIL,queryDocumentSnapshot.getString(FarmersModel.KEY_EMAIL));
                               preferanceManager.putString(FarmersModel.KEY_PERSON_BIO,queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_BIO));
                               preferanceManager.putString(FarmersModel.KEY_PERSON_AGE,queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_AGE));
                               preferanceManager.putString(FarmersModel.KEY_PERSON_LOCATION,queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_LOCATION));
                               setttingName(queryDocumentSnapshot);


                           }

                       }
                   }
                });

    }


    private void setttingName(QueryDocumentSnapshot queryDocumentSnapshot) {

        if(queryDocumentSnapshot.getString(FarmersModel.KEY_FNAME)!=null){
            binding.DashboardName.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_FNAME));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getColor(android.R.color.holo_green_dark));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.DashboardMainLayout.setBackgroundColor(getColor(android.R.color.holo_green_light));
            }

        }
        if(queryDocumentSnapshot.getString(FarmersModel.KEY_CNAME)!=null){
            binding.DashboardName.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_CNAME));
            binding.AddproductLayout.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getColor(android.R.color.holo_blue_dark));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.DashboardMainLayout.setBackgroundColor(getColor(android.R.color.holo_blue_bright));
            }

        }
        if(queryDocumentSnapshot.getString(FarmersModel.KEY_DNAME)!=null){
            binding.DashboardName.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_DNAME));
            binding.AddproductLayout.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getColor(android.R.color.holo_orange_dark));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.DashboardMainLayout.setBackgroundColor(getColor(android.R.color.holo_orange_light));
            }

        }
    }

    private  void loading(Boolean isLoading){
        if(isLoading){
            binding.DashboardProgressbar.setVisibility(View.VISIBLE);
        }
        else{
            binding.DashboardProgressbar.setVisibility(View.INVISIBLE);
        }
    }

}