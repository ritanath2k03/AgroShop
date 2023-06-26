package com.techfest.agroshop02.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.techfest.agroshop02.databinding.ItemContainerReceiveMassageBinding;
import com.techfest.agroshop02.databinding.ItemContainerRecentConversionBinding;

import java.util.List;

import Models.ChatMassage;

public class RecentConversionsAdapter extends RecyclerView.Adapter<RecentConversionsAdapter.ConversionViewHolder>{

    private final List<ChatMassage> chatMassages;

    public RecentConversionsAdapter(List<ChatMassage> chatMassages) {
        this.chatMassages = chatMassages;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(ItemContainerRecentConversionBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
    holder.setData(chatMassages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMassages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{


        private final
        ItemContainerRecentConversionBinding binding;
        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding){
            super(itemContainerRecentConversionBinding.getRoot());
            binding=itemContainerRecentConversionBinding;

        }
        void setData(ChatMassage chatMassage){
            Picasso.get().load(chatMassage.conversionImage);
            binding.textRecentName.setText(chatMassage.conversionName);
            binding.textMassage.setText(chatMassage.massage);
        }
    }

































}
