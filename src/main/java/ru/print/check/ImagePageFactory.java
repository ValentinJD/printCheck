package ru.print.check;

import ru.print.check.executors.PdfToImageExecutor;
import ru.print.check.image.*;
import ru.print.check.model.ImageSize;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ImagePageFactory {

    private PdfToImageExecutor pdfToImageExecutor = new PdfToImageExecutor();
    private ColourImageEditor colourImageEditor;
    private ImageSizeFitter imageSizeFitter;
    private ImageToPageManufacturer imageToPageManufacturer;

    public ImagePageFactory(ColourImageEditor colourImageEditor,
                            ImageSizeFitter imageSizeFitter, ImageToPageManufacturer imageToPageManufacturer) {
        this.colourImageEditor = colourImageEditor;
        this.imageSizeFitter = imageSizeFitter;
        this.imageToPageManufacturer = imageToPageManufacturer;
    }

    public ImagePageFactory() {
        this(new ColourImageEditorImpl(), new ImageSizeFitterImpl(),
                new ImageToPageManufacturerImpl());
    }

    public void createPagesForPrint() throws ExecutionException, InterruptedException {
        pdfToImageExecutor.execute();

        colourImageEditor.editColourImage();
        Map<Integer, List<ImageSize>> groupByThreeImageMap = imageSizeFitter.getGroupByThreeImageSizeMap();
        imageToPageManufacturer.putThreeCheckOnPage(groupByThreeImageMap);
    }
}
