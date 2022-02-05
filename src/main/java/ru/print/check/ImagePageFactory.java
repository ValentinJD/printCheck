package ru.print.check;

import ru.print.check.converter.ConverterPdf;
import ru.print.check.converter.ConverterPdfToImage;
import ru.print.check.image.*;
import ru.print.check.model.ImageSize;

import java.util.List;
import java.util.Map;

public class ImagePageFactory {
    private ConverterPdf converterPdf;
    private ColourImageEditor colourImageEditor;
    private ImageSizeFitter imageSizeFitter;
    private ImageToPageManufacturer imageToPageManufacturer;

    public ImagePageFactory(ConverterPdf converterPdf, ColourImageEditor colourImageEditor,
                            ImageSizeFitter imageSizeFitter, ImageToPageManufacturer imageToPageManufacturer) {
        this.converterPdf = converterPdf;
        this.colourImageEditor = colourImageEditor;
        this.imageSizeFitter = imageSizeFitter;
        this.imageToPageManufacturer = imageToPageManufacturer;
    }

    public ImagePageFactory() {
        this(new ConverterPdfToImage(), new ColourImageEditorImpl(), new ImageSizeFitterImpl(),
                new ImageToPageManufacturerImpl());
    }

    public void createPagesForPrint() {
        converterPdf.convertPdfToJPG();
        colourImageEditor.editColourImage();
        Map<Integer, List<ImageSize>> groupByThreeImageMap = imageSizeFitter.getGroupByThreeImageSizeMap();
        imageToPageManufacturer.putThreeCheckOnPage(groupByThreeImageMap);
    }
}
