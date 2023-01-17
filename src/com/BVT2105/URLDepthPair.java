package com.BVT2105;

import java.util.LinkedList;
import java.net.MalformedURLException;
import java.net.URL;
public class URLDepthPair {
    public static final String URL_PREFIX = "<a href=\"http";
    public String URL;
    private int depth;
    URL host_path;
    public URLDepthPair (String URL, int depth){
        this.URL=URL;
        this.depth=depth;
        try {
            this.host_path= new URL(URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //get метод для хоста
    public String getHost(){
        return host_path.getHost();
    }

    //get метод для пути
    public String getPath(){
        return host_path.getPath();
    }

    //get метод для глубины
    public int getDepth() {
        return depth;
    }

    //get метод для полной URL
    public String getURL() {
        return URL;
    }

    //проверка на уже посещенную страницу
    public static boolean check(LinkedList<URLDepthPair> resultLink, URLDepthPair pair) {
        boolean isAlready = true;
        for (URLDepthPair c : resultLink)
            if (c.getURL().equals(pair.getURL()))
                isAlready=false;
        return isAlready;
    }
}