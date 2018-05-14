package com.youtubers;

public class User {
    private String name;
    private String password;

    public User (String name) {
        this.name = name;
    }

    public String getName () {
        return this.name;
    }

    public String getPassword () {
        return this.password;
    }

}
