package ru.print.check.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtil {
    public static List<File> getFilesInDir(String path) {
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
}
