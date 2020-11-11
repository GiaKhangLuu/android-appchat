package com.sinhvien.appchatsocketio.model;

import com.sinhvien.appchatsocketio.R;
import com.sinhvien.appchatsocketio.adapter.SearchedUserAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class Ultilities {
    private static LinkedList<User> userSelection = new LinkedList<>();
    private static ArrayList<User> searchedUsers = new ArrayList<>();

    public static LinkedList<User> GetUserSelection() {
        return userSelection;
    }

    public static void AddToUserSelections(User user) {
        userSelection.add(user);
    }

    public static void RemoveFromUserSelections(User user) {
        int index = GetPositionOfUserInUserSelection(user);
        if(index > -1) {
            userSelection.remove(index);
        }
    }

    public static int GetPositionOfUserInSearchedUsers(User user) {
        for(int i = 0; i < searchedUsers.size(); i++) {
            if(searchedUsers.get(i).getIdUser().equals(user.getIdUser())) {
                return i;
            }
        }
        return -1;
    }


    public static int GetPositionOfUserInUserSelection(User user) {
        for(int i = 0; i < userSelection.size(); i++) {
            if(userSelection.get(i).getIdUser().equals(user.getIdUser())) {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<User> GetSearchedUser() {
        return searchedUsers;
    }

    public static void SetSearchedUser(JSONArray jsonArray) {
        searchedUsers.clear();
        try {
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                User user = new User();
                user.setIdUser(object.getString("_id"));
                user.setDisplayName(object.getString("displayName"));
                searchedUsers.add(user);
            }
        } catch(Exception err) {
            err.printStackTrace();
        }
    }
}
