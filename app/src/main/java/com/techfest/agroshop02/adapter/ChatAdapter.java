package com.techfest.agroshop02.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.techfest.agroshop02.databinding.ItemContainerReceiveMessegeBinding;
import com.techfest.agroshop02.databinding.ItemContainerSentMessegeBinding;


import java.util.List;
import java.util.zip.Inflater;

import Models.ChatMessage;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
 private final    List<ChatMessage> ChatMessages;
 private final String senderId;
 private final String receiverProfileImage;

 public static final int VIEW_TYPE_SENT=1;
 public static final int VIEW_TYPE_RECEIVE=2;

    public ChatAdapter(List<ChatMessage> ChatMessages, String senderId, String receiverProfileImage) {
        this.ChatMessages = ChatMessages;
        this.senderId = senderId;
        this.receiverProfileImage = receiverProfileImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
if(viewType==VIEW_TYPE_SENT){

    return  new SentmessageViewHolder(ItemContainerSentMessegeBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));

}else{
    return new ReceivedmessageViewHolder(ItemContainerReceiveMessegeBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
}

}

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
if(getItemViewType(position)==VIEW_TYPE_SENT){
    ((SentmessageViewHolder) holder).setData(ChatMessages.get(position));
}else{
    ((ReceivedmessageViewHolder)holder).setData(ChatMessages.get(position),receiverProfileImage);
}
    }

    @Override
    public int getItemCount() {
        return ChatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(ChatMessages.get(position).senderId.equals(senderId)){

            return VIEW_TYPE_SENT;
        }
        else {
            return VIEW_TYPE_RECEIVE;
        }
    }

    static class SentmessageViewHolder extends RecyclerView.ViewHolder{

        private  final  ItemContainerSentMessegeBinding binding;

         SentmessageViewHolder(ItemContainerSentMessegeBinding itemContainerSentmessageBinding){
            super(itemContainerSentmessageBinding.getRoot());
            binding=itemContainerSentmessageBinding;

        }
        void setData(ChatMessage ChatMessage){
            binding.sentmessage.setText(ChatMessage.message);
            binding.sentTime.setText(ChatMessage.dateTime);
        }
    }

    static class ReceivedmessageViewHolder extends RecyclerView.ViewHolder{
private final ItemContainerReceiveMessegeBinding binding;
        public ReceivedmessageViewHolder(ItemContainerReceiveMessegeBinding itemContainerReceivemessageBinding) {
            super(itemContainerReceivemessageBinding.getRoot());
            binding=itemContainerReceivemessageBinding;
        }

        void setData(ChatMessage ChatMessage,String profilepicUrl){
            binding.receivemessage.setText(ChatMessage.message);
            binding.receiveTime.setText(ChatMessage.dateTime);
            Picasso.get().load(profilepicUrl).into(binding.chatprofileImage);
        }
    }
}
