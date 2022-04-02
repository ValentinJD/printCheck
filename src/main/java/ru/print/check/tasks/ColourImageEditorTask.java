package ru.print.check.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.print.check.image.ColourImageEditor;
import ru.print.check.util.FileUtil;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class ColourImageEditorTask implements Callable<Void> {
    static final Logger logger = LoggerFactory.getLogger(ColourImageEditorTask.class);
    ColourImageEditor colourImageEditor;
    CountDownLatch countDownLatch;

    private File file;

    public ColourImageEditorTask(ColourImageEditor colourImageEditor,
                                 File file, CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.colourImageEditor = colourImageEditor;
        this.file = file;
    }


    @Override
    public Void call() {
        logger.info("{} Цветной в ч/б {} начало работы", Thread.currentThread().getName(), file.getPath());
        colourImageEditor.editColourImage(file.getPath(), FileUtil.getDestFile());
        countDownLatch.countDown();
        logger.info("{} Цветной в ч/б {} окончание работы", Thread.currentThread().getName(), file.getPath());
        return null;
    }
}
