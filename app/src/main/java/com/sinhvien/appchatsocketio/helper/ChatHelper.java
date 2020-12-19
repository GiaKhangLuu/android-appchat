package com.sinhvien.appchatsocketio.helper;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.sinhvien.appchatsocketio.R;

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatHelper{
    public static final String EMIT_SETUP_SOCKET = "setUpSocket";
    public static final String EMIT_SEND_MESSAGE = "user_send_message";
    public static final String EMIT_CREATE_NEW_ROOM = "create_new_room";
    public static final String EMIT_NOTIFY_NEW_ROOM = "notify_new_room";
    public static final String EMIT_LEAVE_ROOM = "leave_room";
    public static final String EMIT_TYPING = "typing";
    public static final String EMIT_STOP_TYPING = "stop_typing";

    public static final String ON_NEW_MESSAGE = "new_message";
    public static final String ON_UPDATE_CONVERSATION = "update_conversation";
    public static final String ON_SHOW_NOTIFICATION = "show_notification";
    public static final String ON_SHOW_NOTI_IN_MSG_ACTIVITY = "show_notification";
    public static final String ON_TYPING = "typing";
    public static final String ON_STOP_TYPING = "stop_typing";

    private static ChatHelper chatInstance;
    private Socket socket;

    private ChatHelper(Context context) {
        if(socket == null) {
            try {
                socket = IO.socket(context.getResources().getString(R.string.origin));
            } catch(URISyntaxException ex) {
                Log.i("Socket", ex.getMessage());
            }
        }
    }

    public static ChatHelper getInstace(Context context) {
        if(chatInstance == null) {
            chatInstance = new ChatHelper(context);
        }
        return chatInstance;
    }

    public Socket GetSocket() {
        return socket;
    }
}
