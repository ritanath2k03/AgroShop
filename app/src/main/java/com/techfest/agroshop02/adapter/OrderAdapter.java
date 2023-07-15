package com.techfest.agroshop02.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techfest.agroshop02.databinding.EachOrderListItemBinding;
import com.techfest.agroshop02.listeners.OrderListners;

import java.util.HashMap;
import java.util.List;

import Models.FarmersModel;
import Models.OrderItem;
import Models.PreferanceManager;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    int quantity = 0;
  OrderListners orderListners;
   List<OrderItem> itemList;
   PreferanceManager preferanceManager;

    public OrderAdapter( List<OrderItem> itemList,OrderListners orderListners) {
        this.orderListners = orderListners;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       EachOrderListItemBinding eachOrderListItemBinding=EachOrderListItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new OrderViewHolder(eachOrderListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
holder.setData(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        EachOrderListItemBinding binding;
        public OrderViewHolder(@NonNull EachOrderListItemBinding itemView) {

            super(itemView.getRoot());
            binding=itemView;
        }

        public void setData(OrderItem orderItem) {
            preferanceManager = new PreferanceManager(itemView.getContext());
            binding.dItemName.setText(orderItem.ProductName);
            quantity=0;
            binding.dLocation.setText(orderItem.distributorLocation);
            binding.dPhoneNumber.setText(orderItem.distributorPhoneNumber);
            binding.dAmount.setText(orderItem.distributorAmount);
            binding.dRequested.setText(orderItem.distributorOrder);
            binding.dQuantity.setText(orderItem.distributorQuantity);
            getStatus(orderItem);
        }

        private void getStatus(OrderItem orderItem) {
//farmer or distributor
            Log.d("user",orderItem.currentUser);
//            Log.d("userf",orderItem.currentFarmerUserId);

           if(orderItem.currentFarmerUserId!=null) {
                if (orderItem.currentFarmerUserId.matches(orderItem.currentUser)) {
                    farmerOperation(orderItem);
                }
            }else if(orderItem.currentDistributorUserId!=null){
                if (orderItem.currentDistributorUserId.matches(orderItem.currentUser)) {
                    distributorOperation(orderItem);
                }
            }


        }

        private void distributorOperation(OrderItem orderItem) {
            if(orderItem.orderStatus.matches("1")) binding.StatusButton.setText("CONFIRMED");
            else if (orderItem.orderStatus.matches("2")) {
                binding.StatusButton.setText("DISPATCHED");
            } else if (orderItem.orderStatus.matches("3")) {
                binding.StatusButton.setText("DELIVERED");
            }

        }

        private void farmerOperation(OrderItem orderItem) {
            binding.StatusButton.setBackgroundColor(Color.GRAY);
            if(orderItem.orderStatus.matches("1")) {
                binding.StatusButton.setBackgroundColor(Color.argb(255,201,108,100));
                binding.StatusButton.setText("Yet to \n Dispatched");
            }
            else if(orderItem.orderStatus.matches("2")){
                binding.StatusButton.setBackgroundColor(Color.argb(255,101,148,200));
                binding.StatusButton.setText("Dispatched");
            }
            else if(orderItem.orderStatus.matches("3")){
                binding.StatusButton.setBackgroundColor(Color.GRAY);
                binding.StatusButton.setText("Delivered");
            }
            binding.StatusButton.setOnClickListener(v -> {
                if(orderItem.orderStatus.matches("1"))       {
                    HashMap<String, Object> updates = new HashMap<>();
                    updates.put(FarmersModel.KEY_ITEM_STATUS, String.valueOf(2));
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    firebaseFirestore.collection(FarmersModel.KEY_ORDER_COLLECTION).document(orderItem.orderId).update(FarmersModel.ORDER_STATUS, String.valueOf(2));
                }});


        }
    }
    //1 -> dis confirmed 2 -> despatched 3-> delivered

}
