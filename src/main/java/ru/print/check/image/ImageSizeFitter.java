package ru.print.check.image;

import ru.print.check.model.ImageSize;

import java.util.List;
import java.util.Map;

public interface ImageSizeFitter {
    List<ImageSize> fitImageSizeToPage();

    Map<Integer, List<ImageSize>> getGroupByThreeImageSizeMap();
}
