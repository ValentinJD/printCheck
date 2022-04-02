package ru.print.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class PrintCheckApp {
    static final Logger LOGGER = LoggerFactory.getLogger(PrintCheckApp.class);

    public static void main(String[] args) throws InterruptedException {

        Long start = new Date().getTime();
        LOGGER.info("Имя основного потока {} старт программы", Thread.currentThread().getName());

        ImagePageFactory imagePageFactory = new ImagePageFactory();
        imagePageFactory.createPagesForPrint();

        Long end = new Date().getTime();
        Long result = end - start;
        LOGGER.info("Время пдф в jpeg сек, {}", ((float)imagePageFactory.getTime())/1000);
        LOGGER.info("Время цвет в чернобелое сек, {}", ((float)imagePageFactory.getTime2())/1000);
        LOGGER.info("Вставка по три штуки в лист сек, {}", ((float)imagePageFactory.getTime3())/1000);
        LOGGER.info("Общее время сек, {}", ((float)result)/1000);

    }
}
