package com.BVT2105;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.*;

//Кравлер-таск реализует интерфейс Runnable, что позволяет выполнить
// веб-сканирование в нескольких потоках.
// Здесь мы реализуем алгоритм, который повторяется до тех пор, пока в пуле не останется пар URL-depth
public class CrawlerTask implements Runnable {
    URLPool urlPool;
    private static final String URL_PREFIX = "<a href=\"http";
    // получение пары из URL-пула (ожидает, если пара недоступна)
    public CrawlerTask(URLPool pool){
        this.urlPool = pool;
    }
    // получаем страницу по URL адресу
    public static void request(PrintWriter out,URLDepthPair pair) throws MalformedURLException {
        String request = "GET " + pair.getPath() + " HTTP/1.1\r\nHost:" + pair.getHost() + "\r\nConnection: Close\r\n";
        out.println(request);
        out.flush();
    }
    // Для каждого найденного URL добавляем новую пару к пулу адресов. Новая пара имеет глубину на 1 больше, чем глубина текущего адреса
    public static void buildNewUrl(String str,int depth,URLPool pool){
        try {
            String currentLink = str.substring(str.indexOf(URL_PREFIX)+9,str.indexOf("\"", str.indexOf(URL_PREFIX)+9));
            pool.addPair(new URLDepthPair(currentLink, depth + 1));
        }
        catch (StringIndexOutOfBoundsException e) {}
    }
    @Override
    public void run(){
        while (true){
            // Открываем сокет, передаем ему имя хоста, порт, время таймаута
            URLDepthPair currentPair = urlPool.getPair();
            try {
                Socket my_socket;
                try {
                    my_socket = new Socket(currentPair.getHost(), 80);
                } catch (UnknownHostException e) {
                    System.out.println("Не удалось разрешить URL: "+currentPair.getURL()+" на depth "+currentPair.getDepth());
                    continue;
                }
                my_socket.setSoTimeout(1000);
                try {
                    System.out.println("Сейчас сканируется: "+currentPair.getURL()+" на depth "+currentPair.getDepth());
                    PrintWriter out = new PrintWriter(my_socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(my_socket.getInputStream()));
                    //делаем запрос
                    request(out,currentPair);
                    String line;
                    //проверяем, что юрл текущей ссылки содержит префикс и запускаем метод buildNewUrl,
                    // добавляя новую пару к пулу юрл-адресов.
                    while ((line = in.readLine()) != null){
                        System.out.println(line);
                        if (line.indexOf(currentPair.URL_PREFIX)!=-1){
                            buildNewUrl(line,currentPair.getDepth(),urlPool);
                        }
                    }
                    //закрываем сокет
                    my_socket.close();
                } catch (SocketTimeoutException e) {
                    my_socket.close();
                }
            }
            catch (IOException e) {}
        }
    }
}