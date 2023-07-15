package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.techfest.agroshop02.adapter.MenuListAdpater;
import com.techfest.agroshop02.databinding.ActivityMenuBinding;
import com.techfest.agroshop02.databinding.BottomFarmerDetailsLayoutBinding;
import com.techfest.agroshop02.listeners.MenuItemListners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.FarmersModel;
import Models.MenuItem;
import Models.PreferanceManager;

public class MenuActivity extends AppCompatActivity implements MenuItemListners {
    ActivityMenuBinding binding;
    PreferanceManager preferanceManager;
    BottomFarmerDetailsLayoutBinding bottomsheetlayoutBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.swappableRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMenuList();

            }
        });
        preferanceManager = new PreferanceManager(getApplicationContext());
        getMenuList();
        loadUserDetails();
        setListners();

    }

    private void getDialog(MenuItem menuItem) {
        final Dialog dialog = new Dialog( this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_farmer_details_layout);
        TextView farmernamelayout = dialog.findViewById(R.id.farmerName);
        TextView farmerlocationlayout = dialog.findViewById(R.id.farmerLocation);
        TextView availablequantitylayout = dialog.findViewById(R.id.availableQuantity);
        TextView datelayout = dialog.findViewById(R.id.date);
        TextView farmercontactnolayout = dialog.findViewById(R.id.farmerContact);
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();

        firestore.collection(FarmersModel.KEY_MENU_COLLECTION).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult()!=null){
                        for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if(queryDocumentSnapshot.getId().matches(menuItem.productId)){

                                availablequantitylayout.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_REMAINING_PRODUCT_QUANTITY)+" TON");
                                Log.d("AvailableProduct",queryDocumentSnapshot.getString(FarmersModel.KEY_REMAINING_PRODUCT_QUANTITY));
                                 datelayout.setText(getReadableDateTime(queryDocumentSnapshot.getDate(FarmersModel.KEY_ITEM_DATE)));
                                 farmerlocationlayout.setText(queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_LOCATION));
                                 preferanceManager.putString(FarmersModel.KEY_FARMER_LOCATION, queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_LOCATION));

                                 FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                                 firebaseFirestore.collection(FarmersModel.KEY_COLLECTION_USER).get()
                                         .addOnCompleteListener(task1 -> {
                                             if(task1.isSuccessful()&&task1.getResult()!=null){
                                                 for(QueryDocumentSnapshot snapshot:task1.getResult()){
                                                     if(snapshot.getId().matches(queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_ID))){
                                                         farmernamelayout.setText(snapshot.getString(FarmersModel.KEY_FNAME));
                                                         preferanceManager.putString(FarmersModel.KEY_FARMER_NAME,snapshot.getString(FarmersModel.KEY_FNAME));
                                                         preferanceManager.putString(FarmersModel.KEY_FARMER_ID,queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_ID));
                                                         farmercontactnolayout.setText(snapshot.getString(FarmersModel.KEY_PHONE_NUMBER));
                                                         preferanceManager.putString(FarmersModel.KEY_FARMER_PHONENUMBER,snapshot.getString(FarmersModel.KEY_PHONE_NUMBER));
                                                         LinearLayout bottomView=dialog.findViewById(R.id.BottomMenuView);
                                                         bottomView.setVisibility(View.VISIBLE);
                                                     }
                                                 }
                                             }

                                         });

                                 }

                        }


                    }
                });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations =R.style.animation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void setListners() {
        binding.menuSearchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchdata(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchdata(newText);
                return false;
            }
        });
    }

    private void searchdata(String query) {
        binding.recyclerViewMenuItem.setVisibility(View.GONE);
        if(query.isEmpty()){
            getMenuList();

        }

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        if(preferanceManager.getString(FarmersModel.KEY_DESIGNATION).matches("Farmer")){
            firebaseFirestore.collection(FarmersModel.KEY_MENU_COLLECTION)
                    .whereEqualTo(FarmersModel.KEY_ITEM_NAME,query)
                    .get()
                    .addOnCompleteListener(task -> {
                        String currrntUser=preferanceManager.getString(FarmersModel.KEY_USERID);
                        if(task.isSuccessful()&&task.getResult()!=null){
                            List<MenuItem> lists=new ArrayList<>();
                            lists.clear();
                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                MenuItem menuItem=new MenuItem();
                                if(snapshot.getString(FarmersModel.KEY_FARMER_ID).matches(currrntUser)){
//                                    menuItem.productdate=snapshot.getString(FarmersModel.KEY_ITEM_DATE);
                                    menuItem.productStatus=snapshot.getString(FarmersModel.KEY_ITEM_STATUS);
                                    menuItem.productId=snapshot.getId();
                                    menuItem.productPrice=snapshot.getString(FarmersModel.KEY_ITEM_PRICE);
                                    menuItem.productName=snapshot.getString(FarmersModel.KEY_ITEM_NAME);
                                   menuItem.productImage=snapshot.getString(FarmersModel.KEY_ITEM_PICTURE);
                                   menuItem.personDesignation=snapshot.getString(FarmersModel.KEY_DESIGNATION);
                                   menuItem.productDesciption=snapshot.getString(FarmersModel.KEY_ITEM_DESCRIPTION);
                                   lists.add(menuItem);

                                }
                            }
                            if(lists.size()>0) {
                           MenuListAdpater adapter = new MenuListAdpater(lists, this);
                           binding.recyclerViewMenuItem.setAdapter(adapter);
                                binding.recyclerViewMenuItem.setVisibility(View.VISIBLE);
 }

                        }
                    });

        }
        else if (preferanceManager.getString(FarmersModel.KEY_DESIGNATION).matches("Distributor")) {
            firebaseFirestore.collection(FarmersModel.KEY_MENU_COLLECTION)

                                      .whereEqualTo(FarmersModel.KEY_ITEM_NAME,query)
                                       .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()&&task.getResult()!=null){
                            List<MenuItem> lists=new ArrayList<>();
                            lists.clear();
                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                MenuItem menuItem=new MenuItem();
                                if(snapshot.getString(FarmersModel.KEY_ITEM_STATUS).matches("1")){
                                    menuItem.productStatus = snapshot.getString(FarmersModel.KEY_ITEM_STATUS);
                                   menuItem.productId = snapshot.getId();
                                   menuItem.personDesignation = snapshot.getString(FarmersModel.KEY_DESIGNATION);
                                   menuItem.productPrice = snapshot.getString(FarmersModel.KEY_ITEM_PRICE);
//                                   menuItem.productdate = snapshot.getString(FarmersModel.KEY_ITEM_PRICE);
                                   menuItem.productName = snapshot.getString(FarmersModel.KEY_ITEM_NAME);
                                   menuItem.productImage = snapshot.getString(FarmersModel.KEY_ITEM_PICTURE);
                                   menuItem.productDesciption = snapshot.getString(FarmersModel.KEY_ITEM_DESCRIPTION);
                                   lists.add(menuItem);

                                }
                            }
                            if(lists.size()>0){
                                MenuListAdpater adpater=new MenuListAdpater(lists,this);
                               binding.recyclerViewMenuItem.setAdapter(adpater);
                                binding.recyclerViewMenuItem.setVisibility(View.VISIBLE);
                             }

                        }
                    });

        }

    }


    private String getReadableDateTime(Date date){

        return new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault()).format(date);
    }
    private void loadUserDetails() {
        Long date=System.currentTimeMillis();
        SimpleDateFormat dateFormat =new SimpleDateFormat("dd-MMMM-yyyy - HH:mm a", Locale.getDefault());
        String dateStr = dateFormat.format(date);

        binding.MenuTime.setText(dateStr);
        getName();
    }

    private void getName() {
        if(preferanceManager.getString(FarmersModel.KEY_FNAME)!=null){
            binding.NameMenu.setText(preferanceManager.getString(FarmersModel.KEY_FNAME));
        }
       else if(preferanceManager.getString(FarmersModel.KEY_DNAME)!=null){
            binding.NameMenu.setText(preferanceManager.getString(FarmersModel.KEY_DNAME));
        }
    }

    private void getMenuList() {
        binding.recyclerViewMenuItem.setVisibility(View.VISIBLE);
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
                          {
                              MenuItem item=new MenuItem();

                              Log.d("ItemName",queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_NAME));
                              item.productdate=getReadableDateTime(queryDocumentSnapshot.getDate(FarmersModel.KEY_ITEM_DATE));
                              item.productDesciption=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_DESCRIPTION);
                              item.productId=queryDocumentSnapshot.getId();
                              item.productStatus=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_STATUS);
                              item.productImage=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_PICTURE);
                              item.productName=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_NAME);
                              item.productPrice=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_PRICE);
                              item.personDesignation=queryDocumentSnapshot.getString(FarmersModel.KEY_DESIGNATION);

                         lists.add(item);
                          }
                          else if (preferanceManager.getString(FarmersModel.KEY_DESIGNATION).matches("Distributor"))
                          { MenuItem item=new MenuItem();

                            if(queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_STATUS).matches("1")){
                                Log.d("ItemName",queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_ID));
                                item.productdate=getReadableDateTime(queryDocumentSnapshot.getDate(FarmersModel.KEY_ITEM_DATE));
                                item.productDesciption=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_DESCRIPTION);
                                item.productId=queryDocumentSnapshot.getId();
                                item.productImage=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_PICTURE);
                                item.productName=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_NAME);
                                item.productPrice=queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_PRICE);
                                preferanceManager.putString(FarmersModel.KEY_ITEM_NAME,item.productName);
                                preferanceManager.putString(FarmersModel.KEY_ITEM_PRICE,item.productPrice);
                               lists.add(item);
                            }
                          }
                      }
                      if(lists.size()>0){
                          LinearLayoutManager llm=new LinearLayoutManager(this);
                          llm.setOrientation(LinearLayoutManager.VERTICAL);
                          MenuListAdpater adapter=new MenuListAdpater(lists,this);
                          binding.recyclerViewMenuItem.setLayoutManager(llm);
                          binding.recyclerViewMenuItem.setHasFixedSize(true);
                          binding.recyclerViewMenuItem.setAdapter(adapter);


                      }
                      else {
                          Toast.makeText(this, "Empty list", Toast.LENGTH_SHORT).show();
                      }
                  }
                });
        binding.swappableRefresh.setRefreshing(false);
    }

    @Override
    public void onItemClicked(MenuItem menuItem) {
        int count=0;

        getDialog(menuItem);
    }

    @Override
    public void onItemSelected(MenuItem menuItem, int quentity, String productName, String productPrice, String productId) {
        preferanceManager.putString(FarmersModel.ORDER_QUANTITY,String.valueOf(quentity));
        preferanceManager.putString(FarmersModel.KEY_ITEM_NAME,productName);
        preferanceManager.putString(FarmersModel.KEY_ITEM_PRICE,productPrice);
preferanceManager.putString(FarmersModel.KEY_PRODUCT_ID,productId);
if (quentity>0)startActivity(new Intent(getApplicationContext(),CartActivity.class));


    }
}