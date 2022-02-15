package ru.print.check.executors;

import ru.print.check.image.ImageSizeFitter;
import ru.print.check.image.ImageSizeFitterImpl;
import ru.print.check.image.ImageToPageManufacturer;
import ru.print.check.image.ImageToPageManufacturerImpl;
import ru.print.check.model.ImageSize;
import ru.print.check.tasks.ImageToPageManufacturerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.print.check.config.ValuesForConfig.COUNT_THREADS;

public class ImageToPageManufacturerExecutor {

    private final ImageToPageManufacturer imageToPageManufacturer = new ImageToPageManufacturerImpl();
    private final List<ImageToPageManufacturerTask> queueTask = new CopyOnWriteArrayList<>();
    private CountDownLatch countDownLatch;
    private final ImageSizeFitter imageSizeFitter =  new ImageSizeFitterImpl();
    private final Map<Integer, List<ImageSize>> groupsOnThreeCheck = imageSizeFitter.getGroupByThreeImageSizeMap();

    public void execute() throws InterruptedException {
        addAllTasks(groupsOnThreeCheck);
        ExecutorService service = Executors.newFixedThreadPool(COUNT_THREADS);
        for (ImageToPageManufacturerTask task : queueTask) {
            service.submit(task);
        }
        countDownLatch.await();
        service.shutdown();
    }

    private void addAllTasks(Map<Integer, List<ImageSize>> groupsOnThreeCheck) {
        countDownLatch = new CountDownLatch(groupsOnThreeCheck.size());
        for (List<ImageSize> threeChecks : groupsOnThreeCheck.values()) {
            queueTask.add(getTask(threeChecks));
        }
    }

    private ImageToPageManufacturerTask getTask(List<ImageSize> threeChecks) {
        return new ImageToPageManufacturerTask(imageToPageManufacturer, countDownLatch,
                threeChecks);
    }
}
