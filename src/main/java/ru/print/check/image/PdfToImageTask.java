package ru.print.check.image;

import ru.print.check.converter.ConverterPdfToImage;
import ru.print.check.util.FileUtil;

import java.io.File;

public class PdfToImageTask implements Runnable {
    ConverterPdfToImage converterPdfToImage;

    private File file;

    public PdfToImageTask(ConverterPdfToImage converterPdfToImage, File file) {
        this.converterPdfToImage = converterPdfToImage;
        this.file = file;
    }

    @Override
    public void run() {
        converterPdfToImage.toJPG(file.getPath(), FileUtil.getDestFile());
    }
}
