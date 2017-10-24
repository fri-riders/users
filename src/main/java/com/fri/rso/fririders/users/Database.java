package com.fri.rso.fririders.users;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private static List<User> users = new ArrayList<User>();

    public static List<User> getUsers() {
        return users;
    }

    public static User getUser(String userId) {
        for (User user : getUsers()) {
            if (user.getId().equals(userId)) return user;
        }

        return  null;
    }

}
