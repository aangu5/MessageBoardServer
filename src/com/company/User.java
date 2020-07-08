package com.company;

public class User {
    private String name = "";
    private int userID = 0;
    private String password = "";
    private int messageCount = 0;
    private final String table = "users";

    public User(int inputID, String username, String password, int messageCount) {
        userID = inputID;
        name = username;
        this.password = password;
        this.messageCount = messageCount;
    }
    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public int getUserID() {
        return userID;
    }

    public int getMessageCount() {
        return messageCount;
    }
}
