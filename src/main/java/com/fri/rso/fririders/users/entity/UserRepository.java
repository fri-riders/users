package com.fri.rso.fririders.users.entity;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static List<User> users = new ArrayList<User>();

    static {
        users.add(new User("user1@example.com", "123"));
        users.add(new User("user2@example.com", "123"));
        users.add(new User("user3@example.com", "123"));
        users.add(new User("user4@example.com", "123"));
    }

    public static List<User> getUsers() {
        return users;
    }

    public static User findByUuid(String userUuid) {
        for (User user : getUsers()) {
            if (user.getUuid().equals(userUuid)) return user;
        }

        return  null;
    }

    public static boolean insertUser(User user) {
        return users.add(user);
    }

    public static User findByEmail(String email) {
        for (User user : getUsers()) {
            if (user.getEmail().equals(email)) return user;
        }

        return  null;
    }
}
