package ru.print.check.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;
import ru.print.check.image.PdfToImageTask;
import ru.print.check.queue.QueueTask;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static ru.print.check.util.FileUtil.getFilesInDir;

public class ConverterPdfToImage implements ConverterPdf {

    static QueueTask queueTask = new QueueTask();

    public static List<File> imageList;

    Logger LOGGER = Logger.getLogger(getClass().getName());

    public static CountDownLatch countDownLatch;

    @Override
    public void convertPdfToJPG() {
        List<File> filesInDir = getFilesInDir("pdfs/");
        imageList = new CopyOnWriteArrayList<>(filesInDir);
        countDownLatch = new CountDownLatch(imageList.size());
        for (File fileName : imageList) {
            addPdfToImageTask(fileName);
        }
        runThreads();
    }

    public void toJPG(String sourceFileName, String destFileName) {
        try {
            PDDocument document = PDDocument.loadNonSeq(new File(sourceFileName), null);

            List<PDPage> allPages = document.getDocumentCatalog().getAllPages();

            int pages = 0;

            for (PDPage pdPage : allPages) {
                ++pages;

                BufferedImage bin = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 150);

                String filename = destFileName + "-" + pages + ".jpg";

                ImageIOUtil.writeImage(bin, filename, 150);

                LOGGER.info("Преобразование файла пдф в JPG: " + filename);
            }

            document.close();

        } catch (IOException e) {
            LOGGER.info(" Не удалось преобразовать файл пдф в JPG: " + sourceFileName);
            e.printStackTrace();
        }
    }

    private void addPdfToImageTask(File fileName) {
        PdfToImageTask task = new PdfToImageTask(this, fileName, countDownLatch);
        queueTask.addTask(task);
    }

    private void runThreads() {
        ConcurrentLinkedQueue<PdfToImageTask> queue = queueTask.getQueue();

        for (PdfToImageTask task : queue) {
            task = queue.poll();
            Thread thread = new Thread(task);
            thread.setName("Поток pdf to jpg " + task.getFile().getPath());
            thread.start();
        }

        System.out.println("Ждем выполнение всех потоков");
        while (countDownLatch.getCount() != 0) {

        }
        System.out.println("Потоки выполнили работу");
    }
}
