package ru.print.check.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.print.check.config.ValuesForConfig.DIR_FOR_IMAGE_FILES;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
    }

    public static synchronized List<File> getFilesInDir(String path) {
        File dir = new File(path); //path указывает на директорию

        List<File> files = new ArrayList<>();

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            logger.info(file.getName());

            if (file.isFile()) {
                files.add(file);
            }
        }

        return files;
    }

    private static int countPage = 0;

    public static String getDestFile() {
        String path = DIR_FOR_IMAGE_FILES + "" + countPage;
        countPage++;
        return path;
    }
}
