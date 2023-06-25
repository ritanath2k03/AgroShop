package com.techfest.agroshop02.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.techfest.agroshop02.databinding.ItemContainerReceiveMassageBinding;
import com.techfest.agroshop02.databinding.ItemContainerSentMassageBinding;

import java.util.List;

import Models.ChatMassage;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
 private final    List<ChatMassage> chatMassages;
 private final String senderId;
 private final String receiverProfileImage;

 public static final int VIEW_TYPE_SENT=1;
 public static final int VIEW_TYPE_RECEIVE=2;

    public ChatAdapter(List<ChatMassage> chatMassages, String senderId, String receiverProfileImage) {
        this.chatMassages = chatMassages;
        this.senderId = senderId;
        this.receiverProfileImage = receiverProfileImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
if(viewType==VIEW_TYPE_SENT){
    return  new SentMassageViewHolder(ItemContainerSentMassageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));

}else{
    return new ReceivedMassageViewHolder(ItemContainerReceiveMassageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
}

}

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
if(getItemViewType(position)==VIEW_TYPE_SENT){
    ((SentMassageViewHolder) holder).setData(chatMassages.get(position));
}else{
    ((ReceivedMassageViewHolder)holder).setData(chatMassages.get(position),receiverProfileImage);
}
    }

    @Override
    public int getItemCount() {
        return chatMassages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMassages.get(position).senderId.equals(senderId)){

            return VIEW_TYPE_SENT;
        }
        else {
            return VIEW_TYPE_RECEIVE;
        }
    }

    static class SentMassageViewHolder extends RecyclerView.ViewHolder{

        private  final ItemContainerSentMassageBinding binding;

         SentMassageViewHolder(ItemContainerSentMassageBinding itemContainerSentMassageBinding){
            super(itemContainerSentMassageBinding.getRoot());
            binding=itemContainerSentMassageBinding;

        }
        void setData(ChatMassage chatMassage){
            binding.sentMassage.setText(chatMassage.massage);
            binding.sentTime.setText(chatMassage.dateTime);
        }
    }

    static class ReceivedMassageViewHolder extends RecyclerView.ViewHolder{
private final ItemContainerReceiveMassageBinding binding;
        public ReceivedMassageViewHolder(ItemContainerReceiveMassageBinding itemContainerReceiveMassageBinding) {
            super(itemContainerReceiveMassageBinding.getRoot());
            binding=itemContainerReceiveMassageBinding;
        }

        void setData(ChatMassage chatMassage,String profilepicUrl){
            binding.receiveMassage.setText(chatMassage.massage);
            binding.receiveTime.setText(chatMassage.dateTime);
            Picasso.get().load(profilepicUrl).into(binding.chatprofileImage);
        }
    }
}
