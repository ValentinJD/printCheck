package ru.print.check.image;

import ru.print.check.model.ImageSize;

import java.util.List;

public interface ImageToPageManufacturer {
    void putThreeCheckOnPage(List<ImageSize> threeChecks);
}
