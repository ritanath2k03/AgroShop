package com.techfest.agroshop02.listeners;

import Models.OrderItem;

public interface OrderListners {
    void OnItemClicked(OrderItem orderItem);

    void onItemSelected(OrderItem orderItem, int quantity, String distributorName, String distributorLocation, String distributorPhoneNumber, String distributorProductName, String distributorQuantity, String distributorAmount);
}
