package com.techfest.agroshop02;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.techfest.agroshop02.databinding.ActivityUpdateProfileBinding;

import java.util.HashMap;
import java.util.jar.Attributes;

import Models.FarmersModel;
import Models.PreferanceManager;

public class UpdateProfile extends AppCompatActivity {

    //  EditText


    PreferanceManager preferanceManager;
    ActivityUpdateProfileBinding binding;
    String image;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference(FarmersModel.KEY_COLLECTION_USER);
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ActivityResultLauncher<String> resultLauncher;

    HashMap<String, Object> updatingmap = new HashMap<>();
    String uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferanceManager=new PreferanceManager(getApplicationContext());

        getImage();
     setListners();
getUserData();

    }

    private void getImage() {
       binding.editProfile.setOnClickListener(view -> { resultLauncher.launch("image/*");});
        resultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                binding.editProfile.setImageURI(result);
                Toast.makeText(UpdateProfile.this, "Wait Image Uploading..", Toast.LENGTH_SHORT).show();
                final StorageReference storageReference = storage.getReference(FarmersModel.KEY_COLLECTION_USER).child(binding.emailEdit.getText().toString());
                storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                binding.updateBtn.setVisibility(View.VISIBLE);
                                image =uri.toString();
                                Toast.makeText(UpdateProfile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });


            }
        });

    }

    private void getUserData() {
        binding.nameEditText.setText(preferanceManager.getString(FarmersModel.KEY_USERNAME));
        Picasso.get().load(preferanceManager.getString(FarmersModel.KEY_PICTURE_URI)).into(binding.editProfile);
              binding.emailEdit.setText(preferanceManager.getString(FarmersModel.KEY_EMAIL));
              binding.ageEdit.setText(preferanceManager.getString(FarmersModel.KEY_PERSON_AGE));
              binding.addressEdit.setText(preferanceManager.getString(FarmersModel.KEY_PERSON_LOCATION));
             binding.phoneEdit.setText(preferanceManager.getString(FarmersModel.KEY_PHONE_NUMBER));
              binding.bioEdit.setText(preferanceManager.getString(FarmersModel.KEY_PERSON_BIO));
              image=preferanceManager.getString(FarmersModel.KEY_PICTURE_URI);
              preferanceManager.putString(FarmersModel.KEY_PICTURE_URI,image);

    }

    private void setListners() {
        binding.updateBtn.setOnClickListener(view -> { checkDetails();});
        binding.imageProfile.setOnClickListener(view -> {startActivity(new Intent(getApplicationContext(),FarmerDashboard.class));});

    }


    private void checkDetails() {
        if (TextUtils.isEmpty(binding.addressEdit.getText().toString())) {
            binding.addressEdit.setError("Enter Valid address");
            return;
        } else if (TextUtils.isEmpty(binding.nameEditText.getText().toString())) {
            binding.nameEditText.setError("Enter Valid Name");
            return;
        } else if (TextUtils.isEmpty(binding.phoneEdit.getText().toString())) {
            binding.phoneEdit.setError("Enter Valid PhoneNumber");
            return;

        } else if (TextUtils.isEmpty(binding.bioEdit.getText().toString())) {
            binding.bioEdit.setError("Enter Valid Bio");
            return;
        } else if (TextUtils.isEmpty(binding.ageEdit.getText().toString())) {
            binding.ageEdit.setError("Enter Valid Age");
            return;
        } else {

            setValueintoFirebase();

        }



    }

    private void setValueintoFirebase() {
        updatingmap.put(FarmersModel.KEY_PERSON_LOCATION, binding.addressEdit.getText().toString());
        updatingmap.put(FarmersModel.KEY_PHONE_NUMBER, binding.phoneEdit.getText().toString());
        updatingmap.put(FarmersModel.KEY_PERSON_BIO, binding.bioEdit.getText().toString());
        updatingmap.put(FarmersModel.KEY_PERSON_AGE, binding.ageEdit.getText().toString());
updatingmap.put(FarmersModel.KEY_USERNAME,binding.nameEditText.getText().toString());
updatingmap.put(FarmersModel.KEY_PICTURE_URI,image);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Log.d("UserId", preferanceManager.getString(FarmersModel.KEY_USERID));

        // UpdateProfile();




        firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER).document(preferanceManager.getString(FarmersModel.KEY_USERID))
                .update(updatingmap).addOnCompleteListener(task -> {
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                    preferanceManager.putString(FarmersModel.KEY_PHONE_NUMBER,binding.phoneEdit.getText().toString());
                    preferanceManager.putString(FarmersModel.KEY_PERSON_BIO,binding.bioEdit.getText().toString());
                    preferanceManager.putString(FarmersModel.KEY_PERSON_AGE,binding.ageEdit.getText().toString());
                    preferanceManager.putString(FarmersModel.KEY_PERSON_LOCATION,binding.addressEdit.getText().toString());
                    preferanceManager.putString(FarmersModel.KEY_USERNAME,binding.nameEditText.getText().toString());
                    preferanceManager.putString(FarmersModel.KEY_PICTURE_URI,image);
                    binding.phoneEdit.setText("");
                    binding.bioEdit.setText("");
                    binding.ageEdit.setText("");
                    binding.nameEditText.setText("");
                   binding.addressEdit.setText("");




                });
  //  private void UpdateProfile() {

      // FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        // firebaseFirestore.collection("user").whereEqualTo("name",)

    }
}


