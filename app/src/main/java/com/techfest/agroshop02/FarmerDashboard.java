package com.techfest.agroshop02;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.techfest.agroshop02.databinding.ActivityFarmerDashboardBinding;

import java.util.Date;
import java.util.HashMap;

import Models.FarmersModel;
import Models.PreferanceManager;

public class FarmerDashboard extends AppCompatActivity {
ActivityFarmerDashboardBinding binding;
PreferanceManager preferanceManager;
    String image;
    ActivityResultLauncher<String> resultLauncher;
    FirebaseStorage storage=FirebaseStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFarmerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.DashboardMainLayout.setVisibility(View.GONE);
        preferanceManager=new PreferanceManager(getApplicationContext());
        getDashboardData();
        setListners();
        addProductDetails();
       getImage();
        Log.d("Designation",preferanceManager.getString(FarmersModel.KEY_DESIGNATION));
    }

    private void addProductDetails() {
        HashMap<String,Object> menuMap=new HashMap<>();
        menuMap.put(FarmersModel.KEY_FARMER_ID,preferanceManager.getString(FarmersModel.KEY_USERID));
        menuMap.put(FarmersModel.KEY_ITEM_DATE,new Date());
        menuMap.put(FarmersModel.KEY_FARMER_LOCATION,preferanceManager.getString(FarmersModel.KEY_PERSON_LOCATION));
        menuMap.put(FarmersModel.KEY_ITEM_STATUS,1);
        FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();


        binding.addButton.setOnClickListener(view -> {
            if(TextUtils.isEmpty(binding.productName.getText().toString())){
                binding.productName.setError("Enter Product Name");
                return;

            } else if (TextUtils.isEmpty(binding.productDescription.getText().toString())) {
                binding.productDescription.setError("Enter Product Description");
                return;

            } else if (TextUtils.isEmpty(binding.productPrice.getText().toString())) {
                binding.productPrice.setError("Enter Product Price");
                return;
            }
            else {

                menuMap.put(FarmersModel.KEY_ITEM_NAME,binding.productName.getText().toString());
               menuMap.put(FarmersModel.KEY_ITEM_PICTURE,image);
                menuMap.put(FarmersModel.KEY_ITEM_DESCRIPTION,binding.productDescription.getText().toString());
                menuMap.put(FarmersModel.KEY_ITEM_PRICE,binding.productPrice.getText().toString());
                menuMap.put(FarmersModel.KEY_DESIGNATION,preferanceManager.getString(FarmersModel.KEY_DESIGNATION));
                Log.d("Designation",preferanceManager.getString(FarmersModel.KEY_DESIGNATION));
                firebaseFirestore.collection(FarmersModel.KEY_MENU_COLLECTION)
                        .add(menuMap).addOnCompleteListener(task -> {
                            Toast.makeText(this, "Product Added", Toast.LENGTH_SHORT).show();
                            binding.productPrice.setText("");
                            binding.productName.setText("");
                            binding.productDescription.setText("");


                        });
            }
        });

    }

    private void getImage() {
        final String[] imageuri = {null};
        binding.productImage.setOnClickListener(view -> {
            Toast.makeText(this, "ImageClicked", Toast.LENGTH_SHORT).show();
            if(!(TextUtils.isEmpty(binding.productName.getText().toString())))  {
                resultLauncher.launch("image/*");
                return;}
            else binding.productName.setError("Enter Phone number First");
        });
        resultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                binding.productImage.setImageURI(result);

                final StorageReference storageReference = storage.getReference(FarmersModel.KEY_COLLECTION_USER).child(binding.productName.getText().toString());
                storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
binding.addButton.setVisibility(View.VISIBLE);

                                image =uri.toString();
                            }
                        });
                    }
                });


            }
        });

    }

    private void setListners() {
        binding.chat.setOnClickListener(view -> {startActivity(new Intent(getApplicationContext(),MainActivity.class));});
        binding.EditProfileBtn.setOnClickListener(view -> {startActivity(new Intent(getApplicationContext(),MenuActivity.class));});
    }

    private void getDashboardData() {
        loading(true);
        binding.addButton.setVisibility(View.INVISIBLE);
        String currentUser =preferanceManager.getString(FarmersModel.KEY_USERID);
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER).get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    binding.DashboardMainLayout.setVisibility(View.VISIBLE);
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
                               binding.PersonBio.setText("BIO\n"+queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_BIO));
                               binding.PersonAge.setText("AGE-> "+queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_AGE));
                               binding.PersonLocation.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_LOCATION));

                               //setting details
                               preferanceManager.putString(FarmersModel.KEY_PHONE_NUMBER,queryDocumentSnapshot.getString(FarmersModel.KEY_PHONE_NUMBER));
                               preferanceManager.putString(FarmersModel.KEY_EMAIL,queryDocumentSnapshot.getString(FarmersModel.KEY_EMAIL));
                               preferanceManager.putString(FarmersModel.KEY_PERSON_BIO,queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_BIO));
                               preferanceManager.putString(FarmersModel.KEY_PERSON_AGE,queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_AGE));
                               preferanceManager.putString(FarmersModel.KEY_PERSON_LOCATION,queryDocumentSnapshot.getString(FarmersModel.KEY_PERSON_LOCATION));
                               preferanceManager.putString(FarmersModel.KEY_DESIGNATION,queryDocumentSnapshot.getString(FarmersModel.KEY_DESIGNATION));
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