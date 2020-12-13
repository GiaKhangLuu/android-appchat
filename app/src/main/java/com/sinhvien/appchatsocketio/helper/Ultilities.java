package com.sinhvien.appchatsocketio.helper;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.sinhvien.appchatsocketio.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

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
