package com.techfest.agroshop02.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import  com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techfest.agroshop02.R;
import com.techfest.agroshop02.chatActivity;

import java.util.Random;

import Models.FarmersModel;
import Models.User;

public class MassagingService extends FirebaseMessagingService{
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        User user = new User();
        user.id = message.getData().get(FarmersModel.KEY_USERID);
        if(message.getData().get(FarmersModel.KEY_FNAME)!=null){
            user.name=message.getData().get(FarmersModel.KEY_FNAME);
        }
        if(message.getData().get(FarmersModel.KEY_DNAME)!=null){
            user.name=message.getData().get(FarmersModel.KEY_DNAME);
        }
        if(message.getData().get(FarmersModel.KEY_CNAME)!=null){
            user.name=message.getData().get(FarmersModel.KEY_CNAME);
        }
        user.token = message.getData().get(FarmersModel.KEY_FCM);

        int notificationId = new Random().nextInt();
        String channelId = "chat_message";

        Intent intent = new Intent(this, chatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(FarmersModel.KEY_USER, user);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(user.name);
        builder.setContentText(message.getData().get(FarmersModel.KEY_message));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                message.getData().get(FarmersModel.KEY_message)
        ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Chat Message";
            String channelDescription = "This notification channel is used for chat message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel= new  NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());
    }
}
