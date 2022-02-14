package ru.print.check.config;

import java.util.concurrent.atomic.AtomicInteger;

public class ValuesForConfig {

    public static final int HEIGHT_PAGE = 3508;//3508;//1754
    public static final int WIDTH_PAGE = 2480;//2480;//1240
    public static AtomicInteger numberPageForPrint = new AtomicInteger(1);
    public static float padding = 0.9f;
    public static float brightness = 4f;
    public static float contrast = -700f;
}
