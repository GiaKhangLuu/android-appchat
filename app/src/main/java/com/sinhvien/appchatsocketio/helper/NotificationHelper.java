package com.sinhvien.appchatsocketio.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.activity.MessageActivity;
import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

public class NotificationHelper extends ContextWrapper {
    private final String CHANNEL_ID = "My Channel";
    private final String CHANNEL_NAME = "AppChatSoketIO";

    private NotificationManager notificationManager;
    private User user;
    private Room room;

    public NotificationHelper(Context base, User user, Room room) {
        super(base);
        this.user = user;
        this.room = room;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CreateNotiChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private NotificationManager GetNotiManager() {
        if(notificationManager == null) {
            notificationManager = getSystemService(NotificationManager.class);
        }
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void CreateNotiChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        GetNotiManager().createNotificationChannel(notificationChannel);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void SendNoti(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this,
                CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_baseline_message_24)
                .setContentTitle(room.getName())
                .setContentText(content)
                .setContentIntent(CreatePendingIntent())
                .setAutoCancel(true);
        GetNotiManager().notify(room.getIdRoom().hashCode(), builder.build());
    }

    private PendingIntent CreatePendingIntent() {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("User", user);
        intent.putExtra("Room", room);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                room.getIdRoom().hashCode(),
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );
        return pendingIntent;
    }
}
