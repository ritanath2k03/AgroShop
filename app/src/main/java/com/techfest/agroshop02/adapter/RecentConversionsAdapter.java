package com.techfest.agroshop02.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import com.techfest.agroshop02.databinding.ItemContainerRecentConversionBinding;
import com.techfest.agroshop02.listeners.ConversionListner;

import java.util.List;

import Models.ChatMessage;
import Models.User;

public class RecentConversionsAdapter extends RecyclerView.Adapter<RecentConversionsAdapter.ConversionViewHolder>{

    private final List<ChatMessage> ChatMessages;
    private final ConversionListner conversionListner;

    public RecentConversionsAdapter(List<ChatMessage> ChatMessages,ConversionListner conversionListner) {
        this.ChatMessages = ChatMessages;
        this.conversionListner=conversionListner;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(ItemContainerRecentConversionBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
    holder.setData(ChatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return ChatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{


        private final
        ItemContainerRecentConversionBinding binding;
        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding){
            super(itemContainerRecentConversionBinding.getRoot());
            binding=itemContainerRecentConversionBinding;

        }
        void setData(ChatMessage ChatMessage){
            Picasso.get().load(ChatMessage.conversionImage).into(binding.imageProfile);
            binding.textName.setText(ChatMessage.conversionName);
            binding.message.setText(ChatMessage.message);
            binding.textDesignation.setVisibility(View.INVISIBLE);

            binding.getRoot().setOnClickListener(v->{
                User user=new User();
                user.id=ChatMessage.conversionId;
                user.name=ChatMessage.conversionName;
                user.image=ChatMessage.conversionImage;
                conversionListner.onConversionClicked(user);
            });
        }
    }

































}
