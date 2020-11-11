package com.sinhvien.appchatsocketio.adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.model.Message;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context;
    private int layout;
    private ArrayList<Message> messages;

    public MessageAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Message> objects) {
        super(context, resource, objects);
        this.context = context;
        layout = resource;
        messages = objects;
    }

    private void SetView(Message mess, View view) {
        TextView tvMessage, tvDisplayName;
        tvMessage = view.findViewById(R.id.tvMessage);
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        tvMessage.setText(mess.getMessage());
        tvDisplayName.setText(mess.getDisplayName());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(layout, parent, false);
        Message message = messages.get(position);
        SetView(message, convertView);
        return convertView;
    }


}
