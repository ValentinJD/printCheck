package ru.print.check.tasks;

import ru.print.check.image.ImageToPageManufacturer;
import ru.print.check.model.ImageSize;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class ImageToPageManufacturerTask implements Callable<Void> {
    ImageToPageManufacturer imageToPageManufacturer;
    CountDownLatch countDownLatch;
    List<ImageSize> threeChecks;

    public ImageToPageManufacturerTask(ImageToPageManufacturer imageToPageManufacturer,
                                       CountDownLatch countDownLatch,
                                       List<ImageSize> threeChecks) {
        this.imageToPageManufacturer = imageToPageManufacturer;
        this.countDownLatch = countDownLatch;
        this.threeChecks = threeChecks;
    }

    @Override
    public Void call() {
        imageToPageManufacturer.putThreeCheckOnPage(threeChecks);
        countDownLatch.countDown();
        return null;
    }

}
