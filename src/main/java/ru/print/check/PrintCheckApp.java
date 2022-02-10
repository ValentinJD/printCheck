package ru.print.check;

import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class PrintCheckApp {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Long start = new Date().getTime();
        System.out.println("Имя основного потока" + "\n" +
                Thread.currentThread().getName()  + "\n" + "старт программы");
        ImagePageFactory imagePageFactory = new ImagePageFactory();
        imagePageFactory.createPagesForPrint();
        Long end = new Date().getTime();
        Long result = end - start;
        System.out.println("время пдф в jpeg сек," + ((float)imagePageFactory.getTime())/1000);
        System.out.println("время цвет в чернобелое сек," + ((float)imagePageFactory.getTime2())/1000);
        System.out.println("вставка по три штуки в лист сек," + ((float)imagePageFactory.getTime3())/1000);
        System.out.println("общее время сек," + ((float)result)/1000);

    }
}
