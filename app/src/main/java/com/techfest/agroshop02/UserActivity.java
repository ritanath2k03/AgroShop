package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.techfest.agroshop02.adapter.UsersAdapter;
import com.techfest.agroshop02.databinding.ActivityLoginBinding;
import com.techfest.agroshop02.databinding.ActivityUserBinding;

import java.util.ArrayList;
import java.util.List;

import Models.FarmersModel;
import Models.PreferanceManager;
import Models.User;

public class UserActivity extends AppCompatActivity {
ActivityUserBinding activityUserBinding;
PreferanceManager preferanceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      activityUserBinding= ActivityUserBinding.inflate(getLayoutInflater());

        setContentView(activityUserBinding.getRoot());
preferanceManager=new PreferanceManager(getApplicationContext());

getUsers();

    }

    private void setListerner(){
        activityUserBinding.imageView.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        });
    }
    private  void getUsers(){
        loading(true);
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER).get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUser =preferanceManager.getString(FarmersModel.KEY_USERID);
                    if(task.isSuccessful()&&task.getResult()!=null){
                        List<User> users=new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if(currentUser.equals((queryDocumentSnapshot.getId()))){
                                continue;
                            }
                            User user=new User();
                           if(queryDocumentSnapshot.getString(FarmersModel.KEY_CNAME)!=null){
                               user.name=queryDocumentSnapshot.getString(FarmersModel.KEY_CNAME);
                           }
                           else if (queryDocumentSnapshot.getString(FarmersModel.KEY_FNAME)!=null){
                               user.name=queryDocumentSnapshot.getString(FarmersModel.KEY_FNAME);
                           }
                           else if (queryDocumentSnapshot.getString(FarmersModel.KEY_DNAME)!=null){
                               user.name=queryDocumentSnapshot.getString(FarmersModel.KEY_DNAME);
                           }
                           user.email=queryDocumentSnapshot.getString(FarmersModel.KEY_EMAIL);
                           user.image=queryDocumentSnapshot.getString(FarmersModel.KEY_PICTURE_URI);
                           user.token=queryDocumentSnapshot.getString(FarmersModel.KEY_FCM);
                           users.add(user);
                        }
                        if(users.size()>0){
                            UsersAdapter usersAdapter=new UsersAdapter(users);
                            activityUserBinding.UsersRecyclerView.setAdapter(usersAdapter);
                            activityUserBinding.UsersRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMassage();
                        }
                    }
                    else {
                        showErrorMassage();
                    }
                });
    }

private void showErrorMassage(){
        activityUserBinding.textErrorMassage.setText(String.format("%s","No User available"));
        activityUserBinding.textErrorMassage.setVisibility(View.VISIBLE);
    }
    private  void loading(Boolean isLoading){
        if(isLoading){
            activityUserBinding.ProgressBar.setVisibility(View.VISIBLE);
        }
        else{
            activityUserBinding.ProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}