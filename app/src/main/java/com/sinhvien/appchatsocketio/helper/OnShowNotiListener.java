package com.sinhvien.appchatsocketio.helper;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.sinhvien.appchatsocketio.model.Room;
import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class OnShowNotiListener implements Emitter.Listener {
    User user;
    Context context;

    public OnShowNotiListener(User user, Context context) {
        this.user = user;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void call(Object... args) {
        JSONObject data = (JSONObject) args[0];
        try {
            String roomName = data.getString("name");
            String content = data.getString("content");
            String roomId = data.getString("roomId");
            Room room = new Room();
            room.setIdRoom(roomId);
            room.setName(roomName);
            NotificationHelper notificationHelper = new NotificationHelper(
                    context,
                    user,
                    room
            );
            notificationHelper.SendNoti(content);
        } catch (JSONException ex) {
            Toast.makeText(context, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
