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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageToPageManufacturerExecutor {

    private final ImageToPageManufacturer imageToPageManufacturer = new ImageToPageManufacturerImpl();
    private final List<ImageToPageManufacturerTask> queueTask = new ArrayList<>();
    private CountDownLatch countDownLatch;
    ImageSizeFitter imageSizeFitter =  new ImageSizeFitterImpl();
    Map<Integer, List<ImageSize>> groupsOnThreeCheck = imageSizeFitter.getGroupByThreeImageSizeMap();

    public void execute() throws InterruptedException {
        addAllTasks(groupsOnThreeCheck);
        ExecutorService service = Executors.newFixedThreadPool(3);
        for (ImageToPageManufacturerTask task : queueTask) {
            service.submit(task);
        }
        countDownLatch.await();
        service.shutdown();
    }

    private ImageToPageManufacturerTask getTask(List<ImageSize> threeChecks) {
        return new ImageToPageManufacturerTask(imageToPageManufacturer, countDownLatch,
                threeChecks);
    }

    private void addAllTasks(Map<Integer, List<ImageSize>> groupsOnThreeCheck) {
        countDownLatch = new CountDownLatch(groupsOnThreeCheck.size());
        for (List<ImageSize> threeChecks : groupsOnThreeCheck.values()) {
            queueTask.add(getTask(threeChecks));
        }
    }
}
