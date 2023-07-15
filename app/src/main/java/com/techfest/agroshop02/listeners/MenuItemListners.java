package com.techfest.agroshop02.listeners;

import Models.MenuItem;

public interface MenuItemListners {
    void onItemClicked(MenuItem menuItem);


    void onItemSelected(MenuItem menuItem, int quentity, String productName, String productPrice, String productId);
}
