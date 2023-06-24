package com.techfest.agroshop02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.techfest.agroshop02.databinding.ActivityChatBinding;
import com.techfest.agroshop02.databinding.ActivityLoginBinding;

public class chatActivity extends AppCompatActivity {
    ActivityChatBinding activityChatBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());


    }
}