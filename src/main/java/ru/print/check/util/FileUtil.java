package ru.print.check.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.print.check.config.ValuesForConfig.HEIGHT_PAGE;
import static ru.print.check.config.ValuesForConfig.WIDTH_PAGE;

public class FileUtil {
    public synchronized static List<File> getFilesInDir(String path) {
        File dir = new File(path); //path указывает на директорию

        List<File> files = new ArrayList<>();

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            System.out.println(file.getName());

            if (file.isFile()) {
                files.add(file);
            }
        }

        return files;
    }

    private static int countPage = 0;

    public static String getDestFile() {
        String path = "images" + File.separator + "" + countPage;
        countPage++;
        return path;
    }
}
