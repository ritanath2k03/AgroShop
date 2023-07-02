package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.techfest.agroshop02.adapter.MenuListAdpater;
import com.techfest.agroshop02.databinding.ActivityMainBinding;
import com.techfest.agroshop02.databinding.ActivityMenuBinding;
import com.techfest.agroshop02.listeners.MenuItemListners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.FarmersModel;
import Models.MenuItem;
import Models.PreferanceManager;

public class MenuActivity extends AppCompatActivity implements MenuItemListners{
    ActivityMenuBinding binding;
    PreferanceManager preferanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferanceManager=new PreferanceManager(getApplicationContext());
        getMenuList();
loadUserDetails();
    }
    private String getReadableDateTime(Date date){

        return new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault()).format(date);
    }
    private void loadUserDetails() {
        Long date=System.currentTimeMillis();
        SimpleDateFormat dateFormat =new SimpleDateFormat("dd-MMMM-yyyy - HH:mm a", Locale.getDefault());
        String dateStr = dateFormat.format(date);
        binding.NameMenu.setText(preferanceManager.getString(FarmersModel.KEY_FNAME));
        binding.MenuTime.setText(dateStr);
    }

    private void getMenuList() {
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection(FarmersModel.KEY_MENU_COLLECTION).get()
                .addOnCompleteListener(task -> {
                    List<MenuItem> lists=new ArrayList<>();
                  String currentUser=preferanceManager.getString(FarmersModel.KEY_USERID);
                  if(task.isSuccessful()&&task.getResult()!=null){

                      lists.clear();
                      for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                          Log.d("UserId",preferanceManager.getString(FarmersModel.KEY_USERID));
                          if(currentUser.matches(queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_ID)))
                          { MenuItem item=new MenuItem();
                              Log.d("ItemName",queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_NAME));


                              item.productdate=getReadableDateTime(queryDocumentSnapshot.getDate(FarmersModel.KEY_ITEM_DATE));
                              item.productDesciption=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_DESCRIPTION);
                              item.productId=queryDocumentSnapshot.getId();

                              item.productImage=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_PICTURE);
                              item.productName=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_NAME);
                              item.productPrice=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_PRICE);
                              item.personDesignation=queryDocumentSnapshot.getString(FarmersModel.KEY_DESIGNATION);

                         lists.add(item);
                          }
                          else if (currentUser.matches(queryDocumentSnapshot.getString(FarmersModel.KEY_DISTRIBUTOR_ID)))  { MenuItem item=new MenuItem();
                              Log.d("ItemName",queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_NAME));


                              item.productdate=getReadableDateTime(queryDocumentSnapshot.getDate(FarmersModel.KEY_ITEM_DATE));
                              item.productDesciption=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_DESCRIPTION);
                              item.productId=queryDocumentSnapshot.getId();

                              item.productImage=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_PICTURE);
                              item.productName=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_NAME);
                              item.productPrice=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_PRICE);
                              lists.add(item);
                          }
                      }
                      if(lists.size()>0){
                          LinearLayoutManager llm=new LinearLayoutManager(this);
                          llm.setOrientation(LinearLayoutManager.VERTICAL);
                          MenuListAdpater adpater=new MenuListAdpater(lists,this);
                          binding.recyclerViewMenuItem.setLayoutManager(llm);
                          binding.recyclerViewMenuItem.setHasFixedSize(true);
                          binding.recyclerViewMenuItem.setAdapter(adpater);


                      }
                      else {
                          Toast.makeText(this, "Empty list", Toast.LENGTH_SHORT).show();
                      }
                  }
                });
    }

    @Override
    public void onItemClicked(MenuItem menuItem) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        int count=0;

    }
}