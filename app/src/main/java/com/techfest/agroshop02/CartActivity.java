package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.projection.MediaProjection;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.techfest.agroshop02.databinding.ActivityCartBinding;
import com.techfest.agroshop02.databinding.ActivityMenuBinding;

import java.util.Date;
import java.util.HashMap;

import Models.FarmersModel;
import Models.PreferanceManager;
import kotlin.jvm.internal.PackageReference;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
PreferanceManager preferanceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

preferanceManager=new PreferanceManager(getApplicationContext());
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getData();
        setListners();
    }

    private void setListners() {
        binding.proceedBtn.setOnClickListener(v -> {sendData();});
        binding.back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(intent);
        });
    }

    private void sendData() {

        HashMap<String, Object> map=new HashMap<>();
        map.put(FarmersModel.KEY_DISTRIBUTOR_ID,preferanceManager.getString(FarmersModel.KEY_USERID));
        map.put(FarmersModel.KEY_FARMER_ID,preferanceManager.getString(FarmersModel.KEY_FARMER_ID));
        map.put(FarmersModel.KEY_FARMER_NAME,preferanceManager.getString(FarmersModel.KEY_FARMER_NAME));
        map.put(FarmersModel.KEY_DISTRIBUTOR_NAME,preferanceManager.getString(FarmersModel.KEY_DISTRIBUTOR_NAME));
        map.put(FarmersModel.KEY_FARMER_PHONENUMBER,preferanceManager.getString(FarmersModel.KEY_FARMER_PHONENUMBER));
        map.put(FarmersModel.KEY_DISTRIBUTOR_PHONE_NUMBER,preferanceManager.getString(FarmersModel.KEY_PHONE_NUMBER));
        map.put(FarmersModel.KEY_FARMER_LOCATION,preferanceManager.getString(FarmersModel.KEY_FARMER_LOCATION));
        map.put(FarmersModel.KEY_DISTRIBUTOR_LOCATION,preferanceManager.getString(FarmersModel.KEY_PERSON_LOCATION));
        map.put(FarmersModel.KEY_ITEM_DATE,preferanceManager.getString(FarmersModel.KEY_ITEM_DATE));
        map.put(FarmersModel.KEY_PRODUCT_ID,preferanceManager.getString(FarmersModel.KEY_PRODUCT_ID));
        map.put(FarmersModel.ORDER_QUANTITY,preferanceManager.getString(FarmersModel.ORDER_QUANTITY));
        map.put(FarmersModel.ORDERID,null);
        map.put(FarmersModel.TOTAL_ORDER_AMOUNT,preferanceManager.getString(FarmersModel.TOTAL_ORDER_AMOUNT));

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(FarmersModel.KEY_ORDER_COLLECTION).add(map).addOnCompleteListener(task -> {
            if(task.isSuccessful()&& task.getResult()!=null){
                Log.d("OrderId",task.getResult().getId());
                map.put(FarmersModel.KEY_DISTRIBUTOR_ID,preferanceManager.getString(FarmersModel.KEY_USERID));
                map.put(FarmersModel.KEY_FARMER_ID,preferanceManager.getString(FarmersModel.KEY_FARMER_ID));
                map.put(FarmersModel.KEY_FARMER_NAME,preferanceManager.getString(FarmersModel.KEY_FARMER_NAME));
                map.put(FarmersModel.KEY_DISTRIBUTOR_NAME,preferanceManager.getString(FarmersModel.KEY_DISTRIBUTOR_NAME));
                map.put(FarmersModel.KEY_FARMER_PHONENUMBER,preferanceManager.getString(FarmersModel.KEY_FARMER_PHONENUMBER));
                map.put(FarmersModel.KEY_DISTRIBUTOR_PHONE_NUMBER,preferanceManager.getString(FarmersModel.KEY_PHONE_NUMBER));
                map.put(FarmersModel.KEY_FARMER_LOCATION,preferanceManager.getString(FarmersModel.KEY_FARMER_LOCATION));
                map.put(FarmersModel.KEY_DISTRIBUTOR_LOCATION,preferanceManager.getString(FarmersModel.KEY_PERSON_LOCATION));
                map.put(FarmersModel.KEY_ITEM_DATE,new Date());
                map.put(FarmersModel.KEY_PRODUCT_ID,preferanceManager.getString(FarmersModel.KEY_PRODUCT_ID));
                map.put(FarmersModel.ORDER_QUANTITY,preferanceManager.getString(FarmersModel.ORDER_QUANTITY));
                map.put(FarmersModel.TOTAL_ORDER_AMOUNT,preferanceManager.getString(FarmersModel.TOTAL_ORDER_AMOUNT));
                map.put(FarmersModel.ORDERID,task.getResult().getId());
                firebaseFirestore.collection(FarmersModel.KEY_ORDER_COLLECTION).document(task.getResult().getId())
                         .update(map);
                Intent intent=new Intent(getApplicationContext(),MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }


        });






    }

    private void getData() {

        binding.itemQuantity.setText(preferanceManager.getString(FarmersModel.ORDER_QUANTITY)+" TON");
        binding.itemMenu.setText(preferanceManager.getString(FarmersModel.KEY_ITEM_NAME));
       int a= amount(Integer.parseInt(preferanceManager.getString(FarmersModel.ORDER_QUANTITY)));
        binding.itemAmount.setText(String.valueOf(a));
        binding.taxStateAmount.setText(String.valueOf((a*9)/100));
        binding.taxCentralAmount.setText(String.valueOf((a*9)/100));
binding.totalAmount.setText(String.valueOf(a+2*(a*9)/100));
preferanceManager.putString(FarmersModel.TOTAL_ORDER_AMOUNT,String.valueOf(a+2*(a*9)/100));

    }

    private int amount(int quantity) {

    int price=Integer.parseInt(preferanceManager.getString(FarmersModel.KEY_ITEM_PRICE));

    return price*quantity;

    }
}