package com.youtubers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static sun.util.logging.LoggingSupport.log;


public class Requests {

    public Requests () {
        this.sendGet();
    }

    // HTTP GET request
    public void sendGet() {
        try {
            String url = "http://yttalk.com/threads/who-are-your-favorite-youtubers.93000/page-";
            for (int i = 1; i < 100; i++) {
                String page = String.valueOf(i);
                Document document = Jsoup.connect(url + page).get();
                Elements userNames = document.select("a.username");

                for (Element userName: userNames) {
                    System.out.println(userName.html());
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
