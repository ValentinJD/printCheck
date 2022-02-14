package ru.print.check.tasks;

import ru.print.check.converter.ConverterPdf;
import ru.print.check.util.FileUtil;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class PdfToImageTask implements Callable<Void> {
    ConverterPdf converterPdfToImage;
    CountDownLatch countDownLatch;

    private File file;

    public PdfToImageTask(ConverterPdf converterPdfToImage,
                          File file, CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.converterPdfToImage = converterPdfToImage;
        this.file = file;
    }


    @Override
    public Void call() {
        System.out.println(Thread.currentThread().getName()
                + "ПДФ в jpeg файла" + file.getPath());
        converterPdfToImage.convertPdfToJPG(file.getPath(), FileUtil.getDestFile());
        countDownLatch.countDown();
        System.out.println(Thread.currentThread().getName()
                + "ПДФ в jpeg файла" + file.getPath() + "выполнено");
        return null;
    }
}
