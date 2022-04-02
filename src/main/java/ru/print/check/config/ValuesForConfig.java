package ru.print.check.config;

import java.util.concurrent.atomic.AtomicInteger;

public class ValuesForConfig {

    private ValuesForConfig() {
    }

    public static final int HEIGHT_PAGE = 3508;//3508;//1754
    public static final int WIDTH_PAGE = 2480;//2480;//1240
    public static final AtomicInteger numberPageForPrint = new AtomicInteger(1);
    public static final float PADDING = 0.9f;
    public static final float BRIGHTNESS = 4f;
    public static final float CONTRAST = -700f;
    public static final int COUNT_THREADS = 3;
    public static final String DIR_FOR_PDF_FILES = "pdfs/";
    public static final String DIR_FOR_IMAGE_FILES = "imageOUT/";
    public static final String DIR_FOR_PAGE_FOR_PRINT = "pageOUT/";
}
