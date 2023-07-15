package com.techfest.agroshop02;
import static java.util.Objects.requireNonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.techfest.agroshop02.adapter.OrderAdapter;
import com.techfest.agroshop02.databinding.ActivityOrderBinding;
import com.techfest.agroshop02.listeners.OrderListners;

import java.util.ArrayList;
import java.util.List;

import Models.FarmersModel;
import Models.OrderItem;
import Models.PreferanceManager;

public class Order extends AppCompatActivity implements OrderListners {

    ActivityOrderBinding binding;

    PreferanceManager preferanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
setListners();
        binding.swappableRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrderList();

            }
        });

        preferanceManager = new PreferanceManager(getApplicationContext());

        getOrderList();


    }

    private void setListners() {
        binding.currentOrder.setOnClickListener(v -> {getOrderList();});
        binding.previousOrder.setOnClickListener(v -> {getPreviousOrderList();});
    }

    private void getOrderList() {
        binding.recyclerViewOrderItem.setVisibility(View.VISIBLE);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(FarmersModel.KEY_ORDER_COLLECTION).get()
                .addOnCompleteListener(task -> {
                    List<OrderItem> orderLists = new ArrayList<>();
                    String currentUser = preferanceManager.getString(FarmersModel.KEY_USERID);
                    if(task.isSuccessful() && task.getResult()!=null) {
                       orderLists.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Log.d("UserId", preferanceManager.getString(FarmersModel.KEY_USERID));
                          //  Log.d("FarmerId",queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_ID));
                            Log.d("DId",queryDocumentSnapshot.getString(FarmersModel.KEY_DISTRIBUTOR_ID));
                            if(queryDocumentSnapshot.getString(FarmersModel.ORDER_STATUS).matches("1")||queryDocumentSnapshot.getString(FarmersModel.ORDER_STATUS).matches("2")){

                               getCurrentOrder(queryDocumentSnapshot,currentUser,orderLists);
                            }

                        }


                        if (orderLists.size() > 0) {


              LinearLayoutManager llm=new LinearLayoutManager(this);
                              binding.recyclerViewOrderItem.setLayoutManager(llm);
                              llm.setOrientation(LinearLayoutManager.VERTICAL);

                              OrderAdapter orderAdapter=new OrderAdapter(orderLists,this);
                              Log.d("ADAPTER",orderAdapter.toString());
                            binding.recyclerViewOrderItem.setAdapter(orderAdapter);
                            binding.recyclerViewOrderItem.setVisibility(View.VISIBLE);

                        }
                        else {
                            Toast.makeText(this, "Empty Order List", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        binding.swappableRefresh.setRefreshing(false);

    }
    private void getPreviousOrderList() {
        binding.recyclerViewOrderItem.setVisibility(View.VISIBLE);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(FarmersModel.KEY_ORDER_COLLECTION).get()
                .addOnCompleteListener(task -> {
                    List<OrderItem> orderLists = new ArrayList<>();
                    String currentUser = preferanceManager.getString(FarmersModel.KEY_USERID);
                    if(task.isSuccessful() && task.getResult()!=null) {
                        orderLists.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Log.d("UserId", preferanceManager.getString(FarmersModel.KEY_USERID));
                            //  Log.d("FarmerId",queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_ID));
                            Log.d("DId",queryDocumentSnapshot.getString(FarmersModel.KEY_DISTRIBUTOR_ID));
                            if(queryDocumentSnapshot.getString(FarmersModel.ORDER_STATUS).matches("3")){

                                getCurrentOrder(queryDocumentSnapshot,currentUser,orderLists);
                            }

                        }


                        if (orderLists.size() > 0) {


                            LinearLayoutManager llm=new LinearLayoutManager(this);
                            binding.recyclerViewOrderItem.setLayoutManager(llm);
                            llm.setOrientation(LinearLayoutManager.VERTICAL);

                            OrderAdapter orderAdapter=new OrderAdapter(orderLists,this);
                            Log.d("ADAPTER",orderAdapter.toString());
                            binding.recyclerViewOrderItem.setAdapter(orderAdapter);
                            binding.recyclerViewOrderItem.setVisibility(View.VISIBLE);

                        }
                        else {
                            Toast.makeText(this, "Empty Order List", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        binding.swappableRefresh.setRefreshing(false);

    }


    private void getCurrentOrder(QueryDocumentSnapshot queryDocumentSnapshot, String currentUser, List<OrderItem> orderLists) {
        if (currentUser.matches(queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_ID))) {
            OrderItem item = new OrderItem();
            item.currentUser=currentUser;
            item.distributorName = queryDocumentSnapshot.getString(FarmersModel.KEY_DISTRIBUTOR_NAME);
            item.distributorLocation = queryDocumentSnapshot.getString(FarmersModel.KEY_DISTRIBUTOR_LOCATION);
            item.distributorPhoneNumber = queryDocumentSnapshot.getString(FarmersModel.KEY_DISTRIBUTOR_PHONE_NUMBER);
            item.distributorQuantity = queryDocumentSnapshot.getString(FarmersModel.ORDER_QUANTITY);
            item.ProductName = queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_NAME);
            item.orderStatus=queryDocumentSnapshot.getString(FarmersModel.ORDER_STATUS);
            item.distributorAmount = queryDocumentSnapshot.getString(FarmersModel.TOTAL_ORDER_AMOUNT);
            item.currentFarmerUserId = queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_ID);
            item.orderId=queryDocumentSnapshot.getString(FarmersModel.ORDERID);
            orderLists.add(item);
        }
        if(currentUser.matches(queryDocumentSnapshot.getString(FarmersModel.KEY_DISTRIBUTOR_ID))) {
            Log.d("ItemName", queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_ID));
            OrderItem item = new OrderItem();
            //convert  dis to farmer ...
            item.currentUser=currentUser;
            item.distributorName = queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_NAME);
            item.distributorLocation = queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_LOCATION);
            item.distributorPhoneNumber = queryDocumentSnapshot.getString(FarmersModel.KEY_FARMER_PHONENUMBER);
            item.distributorProductName = queryDocumentSnapshot.getString(FarmersModel.KEY_ITEM_NAME);
            item.distributorQuantity = queryDocumentSnapshot.getString(FarmersModel.ORDER_QUANTITY);
            item.distributorAmount = queryDocumentSnapshot.getString(FarmersModel.TOTAL_ORDER_AMOUNT);
            item.orderStatus=queryDocumentSnapshot.getString(FarmersModel.ORDER_STATUS);
            item.currentDistributorUserId = queryDocumentSnapshot.getString(FarmersModel.KEY_DISTRIBUTOR_ID);
            orderLists.add(item);
            preferanceManager.putString(FarmersModel.KEY_FARMER_NAME, item.distributorName);
            preferanceManager.putString(FarmersModel.KEY_FARMER_LOCATION, item.distributorLocation);
            preferanceManager.putString(FarmersModel.KEY_FARMER_PHONENUMBER, item.distributorPhoneNumber);
            preferanceManager.putString(FarmersModel.KEY_ITEM_NAME, item.distributorProductName);
            preferanceManager.putString(FarmersModel.ORDER_QUANTITY, item.distributorQuantity);
            preferanceManager.putString(FarmersModel.TOTAL_ORDER_AMOUNT, item.distributorAmount);

        }
    }

    @Override
    public void OnItemClicked(OrderItem orderItem) {

    }

    @Override
    public void onItemSelected(OrderItem orderItem, int quantity, String distributorName, String distributorLocation, String distributorPhoneNumber,String distributorProductName,String distributorQuantity, String distributorAmount){
        preferanceManager.putString(FarmersModel.KEY_DISTRIBUTOR_NAME,distributorName);
        preferanceManager.putString(FarmersModel.KEY_DISTRIBUTOR_LOCATION,distributorLocation);
        preferanceManager.putString(FarmersModel.KEY_DISTRIBUTOR_PHONE_NUMBER,distributorPhoneNumber);
        preferanceManager.putString(FarmersModel.KEY_ITEM_NAME,distributorProductName);
        preferanceManager.putString(FarmersModel.ORDER_QUANTITY,distributorQuantity);
        preferanceManager.putString(FarmersModel.TOTAL_ORDER_AMOUNT,distributorAmount);


    }
}