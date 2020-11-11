package com.sinhvien.appchatsocketio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.helper.ChatHelper;
import com.sinhvien.appchatsocketio.model.User;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private ListView lvShowUser;
    private User user;
    private Socket socket;

    public SearchFragment(User user) {
        this.user = user;
    }

    private void Init(View view) {
        searchView = view.findViewById(R.id.searchView);
        lvShowUser = view.findViewById(R.id.lvShowUser);
        socket = ChatHelper.getInstace(getContext()).GetSocket();
        if(socket.connected()) {
            Toast.makeText(getContext(), "connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "disconnected", Toast.LENGTH_SHORT).show();
            socket.connect();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
    }
}
