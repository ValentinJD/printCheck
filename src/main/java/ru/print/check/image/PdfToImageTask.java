package ru.print.check.image;

import ru.print.check.converter.ConverterPdfToImage;
import ru.print.check.util.FileUtil;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class PdfToImageTask implements Runnable {
    ConverterPdfToImage converterPdfToImage;
    CountDownLatch countDownLatch;

    private File file;

    public PdfToImageTask(ConverterPdfToImage converterPdfToImage,
                          File file, CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.converterPdfToImage = converterPdfToImage;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public void run() {
        converterPdfToImage.toJPG(file.getPath(), FileUtil.getDestFile());
        countDownLatch.countDown();
        System.out.println(Thread.currentThread().getName()
                + "закончил работу");
    }
}
