package ru.print.check.tasks;

import ru.print.check.converter.ConverterPdf;
import ru.print.check.image.ColourImageEditor;
import ru.print.check.util.FileUtil;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class ColourImageEditorTask implements Callable<Void> {
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
        System.out.println(Thread.currentThread().getName()
                + "Цветной в ч/б " + file.getPath() + "начало работы");
        colourImageEditor.editColourImage(file.getPath(), FileUtil.getDestFile());
        countDownLatch.countDown();
        System.out.println(Thread.currentThread().getName()
                + "Цветной в ч/б " + file.getPath() + "окончание работы");

        return null;
    }
}
