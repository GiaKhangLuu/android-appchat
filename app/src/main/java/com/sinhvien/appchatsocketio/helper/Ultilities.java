package com.sinhvien.appchatsocketio.helper;

import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class Ultilities {

    public static void AddToUserSelections(LinkedList<User> userSelections, User user) {
        if(GetPositionOfUserInUserSelection(userSelections, user) == -1) {
            userSelections.add(user);
        }
    }

    public static int RemoveFromUserSelections(LinkedList<User> userSelections,  User user) {
        int index = GetPositionOfUserInUserSelection(userSelections, user);
        if(index > -1) {
            userSelections.remove(index);
            return index;
        }
        return -1;
    }

    public static int GetPositionOfUserInSearchedUsers(ArrayList<User> searchedUsers, User removeUser) {
        for(User user : searchedUsers) {
            if(user.getIdUser().equals(removeUser.getIdUser())) {
                return searchedUsers.indexOf(user);
            }
        }
        return -1;
    }


    public static int GetPositionOfUserInUserSelection(LinkedList<User> userSelections,
                                                       User theChoosen) {
        for(User user : userSelections) {
            if(user.getIdUser().equals(theChoosen.getIdUser())) {
                return userSelections.indexOf(user);
            }
        }
        return -1;
    }
}
