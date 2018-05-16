package com.youtubers;

public class UserPassword {
    private String user;
    private String password;

    UserPassword (String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUserName () {
        return this.user;
    }

    public String getUserPassword () {
        return this.password;
    }
}
