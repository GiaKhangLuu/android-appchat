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

public class ChatHelper{
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
