package com.BVT2105;

import java.util.LinkedList;

public class Crawler {
    //Вывод результатов
    public static void showResult(LinkedList<URLDepthPair> resultLink) {
        for (URLDepthPair c : resultLink)
            System.out.println("Depth :" + c.getDepth() + "\tLink :" + c.getURL());
    }

    //Старт
    public static void main(String[] args) {
        //http://crawler-test.com/

        args = new String[]{"http://www.all-met.narod.ru", "5", "3"};

        try {
            //обработка аргументов
            String lineUrl = args[0];
            int numThreads = Integer.parseInt(args[2]);
            URLPool pool = new URLPool(Integer.parseInt(args[1]));
            //создание экземпляра пула юрл-адресов
            pool.addPair(new URLDepthPair(lineUrl, 0));
            //создаем указанное количество задач и потоков для веб-сканера
            for (int i = 0; i < numThreads; i++) {
                CrawlerTask c = new CrawlerTask(pool);
                Thread t = new Thread(c);
                t.start();
            }

            //если у нас не все запущенные потоки в режиме ожидания, то пусть поток "заснет"
            // на 500 млск
            while (pool.getWait() != numThreads) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException is ignored");
                }
            }
            try {
                //показываем результат
                showResult(pool.getResult());
                ;
            } catch (NullPointerException e) {
                System.out.println("Error! " + e);
            }
            System.exit(0);
        } catch (Exception err) {
            System.out.println("Error!\n" + err);
            System.out.println("Usage: java crawler <site> <depth> <threads>");
        }
    }
}