package com.techfest.agroshop02.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.techfest.agroshop02.databinding.ItemContainerUserBinding;
import com.techfest.agroshop02.listeners.UserListeners;

import java.util.List;

import Models.User;

public class UsersAdapter extends  RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private  final List<User> users;
    private  final UserListeners userListeners;

    public UsersAdapter(List<User> users,UserListeners userListeners) {
        this.users = users;
        this.userListeners=userListeners;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       ItemContainerUserBinding itemContainerUserBinding=ItemContainerUserBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
         holder.setUserData(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    class UserViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;

        }

        void setUserData(User user){
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.PhoneNumber.setText(user.phone);
            binding.textDesignation.setText(user.designation);
            Picasso.get().load(user.image).into(binding.imageProfile);
            binding.getRoot().setOnClickListener(v -> userListeners.onUserClicked(user) );
        }
    }

//    private Bitmap getUserImage(String encodedImage){
//        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//    }

}
