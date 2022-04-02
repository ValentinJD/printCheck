package ru.print.check.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.print.check.converter.ConverterPdf;
import ru.print.check.util.FileUtil;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class PdfToImageTask implements Callable<Void> {
    private final Logger logger = LoggerFactory.getLogger(PdfToImageTask.class);

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
        logger.info("{} ПДФ в jpeg файла {}", Thread.currentThread().getName(), file.getPath());
        converterPdfToImage.convertPdfToJPG(file.getPath(), FileUtil.getDestFile());
        countDownLatch.countDown();
        logger.info("{} ПДФ в jpeg  {}", Thread.currentThread().getName(), file.getPath());
        return null;
    }
}
