package ru.print.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Objects;

import static ru.print.check.config.ValuesForConfig.*;

public class PrintCheckApp {
    static final Logger LOGGER = LoggerFactory.getLogger(PrintCheckApp.class);

    public static void main(String[] args) throws InterruptedException {
        boolean isCreateDirImages = new File(DIR_FOR_IMAGE_FILES).mkdirs();
        if (isCreateDirImages) {
            LOGGER.info("Создана директория для сохранения images {}", DIR_FOR_IMAGE_FILES);
        } else {
            LOGGER.info("Директория для сохранения images {} существует", DIR_FOR_IMAGE_FILES);
            LOGGER.info("Удаление старых файлов из директории {}", DIR_FOR_IMAGE_FILES);
            deleteAllFilesFolder(DIR_FOR_IMAGE_FILES);
            LOGGER.info("Файлы из директории {} удалены", DIR_FOR_IMAGE_FILES);
        }
        boolean isCreatePageDir = new File(DIR_FOR_PAGE_FOR_PRINT).mkdirs();
        if (isCreatePageDir) {
            LOGGER.info("Создана директория для сохранения pages {}", DIR_FOR_PAGE_FOR_PRINT);
        } else {
            LOGGER.info("Директория для сохранения pages {} существует", DIR_FOR_PAGE_FOR_PRINT);
            LOGGER.info("Удаление старых файлов из директории {}", DIR_FOR_PAGE_FOR_PRINT);
            deleteAllFilesFolder(DIR_FOR_PAGE_FOR_PRINT);
            LOGGER.info("Файлы из директории {} удалены", DIR_FOR_PAGE_FOR_PRINT);
        }
        long start = new Date().getTime();
        LOGGER.info("Имя основного потока {} старт программы", Thread.currentThread().getName());

        ImagePageFactory imagePageFactory = new ImagePageFactory();
        imagePageFactory.createPagesForPrint();

        deleteAllFilesFolder(DIR_FOR_PDF_FILES);
        deleteAllFilesFolder(DIR_FOR_IMAGE_FILES);

        long end = new Date().getTime();
        long result = end - start;
        LOGGER.info("Время пдф в jpeg сек, {}", ((float) imagePageFactory.getTime()) / 1000);
        LOGGER.info("Время цвет в чернобелое сек, {}", ((float) imagePageFactory.getTime2()) / 1000);
        LOGGER.info("Вставка по три штуки в лист сек, {}", ((float) imagePageFactory.getTime3()) / 1000);
        LOGGER.info("Общее время сек, {}", ((float) result) / 1000);
    }

    public static void deleteAllFilesFolder(String path) {
        for (File myFile : Objects.requireNonNull(new File(path).listFiles())){
            if (myFile.isFile()){
                try {
                    Files.deleteIfExists(myFile.toPath());
                } catch (IOException e) {
                    LOGGER.error("При удалении файл с именем {} произошла ошибка", myFile);
                }
            }
        }
    }
}
