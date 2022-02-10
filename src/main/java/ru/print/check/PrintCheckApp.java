package ru.print.check;

import java.time.LocalTime;
import java.util.concurrent.ExecutionException;

public class PrintCheckApp {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        LocalTime start = LocalTime.now();
        System.out.println("Имя основного потока" + "\n" +
                Thread.currentThread().getName()  + "\n" + "старт программы");
        ImagePageFactory imagePageFactory = new ImagePageFactory();
        imagePageFactory.createPagesForPrint();


        LocalTime end = LocalTime.now();
        System.out.println("старт" + start);
        System.out.println("финиш" + end);

    }
}
