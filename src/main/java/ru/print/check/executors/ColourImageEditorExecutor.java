package ru.print.check.executors;

import ru.print.check.image.ColourImageEditorImpl;
import ru.print.check.tasks.ColourImageEditorTask;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.print.check.util.FileUtil.getFilesInDir;

public class ColourImageEditorExecutor {
    private final ColourImageEditorImpl colourImageEditor = new ColourImageEditorImpl();
    private volatile List<File> imageList;
    private final List<ColourImageEditorTask> queueTask = new CopyOnWriteArrayList<>();

    private CountDownLatch countDownLatch;

    public void execute() throws InterruptedException {
        imageList = new CopyOnWriteArrayList<>(getFilesInDir("images/"));
        addAllImageTaskInList();

        ExecutorService service = Executors.newFixedThreadPool(3);

        for (ColourImageEditorTask task : queueTask) {
            service.submit(task);
        }
        countDownLatch.await();
        service.shutdown();
    }

    private void addAllImageTaskInList() {
        countDownLatch = new CountDownLatch(imageList.size());
        for (File fileName : imageList) {
            queueTask.add(getColourImageEditorTask(fileName));
        }
    }

    private ColourImageEditorTask getColourImageEditorTask(File fileName) {
        return new ColourImageEditorTask(colourImageEditor, fileName, countDownLatch);
    }
}
