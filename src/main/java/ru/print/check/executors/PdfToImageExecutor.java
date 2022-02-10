package ru.print.check.executors;

import ru.print.check.converter.ConverterPdf;
import ru.print.check.converter.ConverterPdfToImage;
import ru.print.check.tasks.PdfToImageTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static ru.print.check.util.FileUtil.getFilesInDir;

public class PdfToImageExecutor {

    private final ConverterPdf converterPdf = new ConverterPdfToImage();
    private final List<File> imageList = new CopyOnWriteArrayList<>(getFilesInDir("pdfs/"));
    private final List<PdfToImageTask> queueTask = new ArrayList<>();
    private CountDownLatch countDownLatch;

    public void execute() throws InterruptedException {
        addAllPdfToImageTaskInList();

        ExecutorService service = Executors.newFixedThreadPool(2);

        for (PdfToImageTask task: queueTask) {
            service.submit(task);
        }

        countDownLatch.await();
        service.shutdown();
    }

    private void addAllPdfToImageTaskInList() {
        countDownLatch = new CountDownLatch(imageList.size());
        for (File fileName : imageList) {
            queueTask.add(getPdfToImageTask(fileName));
        }
    }

    private PdfToImageTask getPdfToImageTask(File fileName) {
        return new PdfToImageTask(converterPdf, fileName, countDownLatch);
    }
}
