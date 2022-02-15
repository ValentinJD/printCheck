package ru.print.check;

import ru.print.check.executors.ColourImageEditorExecutor;
import ru.print.check.executors.ImageToPageManufacturerExecutor;
import ru.print.check.executors.PdfToImageExecutor;

import java.util.Date;

public class ImagePageFactory {

    private PdfToImageExecutor pdfToImageExecutor = new PdfToImageExecutor();
    private ColourImageEditorExecutor colourImageEditorExecutor = new ColourImageEditorExecutor();
    private ImageToPageManufacturerExecutor imageExecutor;

    public void createPagesForPrint() throws InterruptedException {
        final Long start = new Date().getTime();
        pdfToImageExecutor.execute();
        final Long end = new Date().getTime();
        result = end - start;

        final Long start2 = new Date().getTime();
        colourImageEditorExecutor.execute();
        final Long end2 = new Date().getTime();
        result2 = end2 - start2;

        final Long start3 = new Date().getTime();
        imageExecutor = new ImageToPageManufacturerExecutor();
        imageExecutor.execute();
        final Long end3 = new Date().getTime();
        result3 = end3 - start3;
    }

    private long result;
    private long result2;
    private long result3;

    public long getTime() {
        return result;
    }

    public long getTime2() {
        return result2;
    }

    public long getTime3() {
        return result3;
    }
}
