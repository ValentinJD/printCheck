package ru.print.check.image;

import ru.print.check.model.ImageSize;

import java.util.List;
import java.util.Map;

public interface ImageToPageManufacturer {

    void putImagesToPage(Map<Integer, List<ImageSize>> groupByThreeImageMap);

    void putThreeCheckOnPage(Map<Integer, List<ImageSize>> groupByThreeImageMap);
}
