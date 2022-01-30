package ru.print.check.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ConverterPdfToImage implements ConverterPdf {

    Logger LOGGER = Logger.getLogger(getClass().getName());

    @Override
    public void toJPG(String sourceFileName, String destFileName) {

        try {
            PDDocument document = PDDocument.loadNonSeq(new File(sourceFileName), null);

            List<PDPage> allPages = document.getDocumentCatalog().getAllPages();

            int pages = 0;

            for (PDPage pdPage : allPages) {
                ++pages;

                BufferedImage bin = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 150);

                String filename = destFileName + "-" + pages + ".jpg";

                ImageIOUtil.writeImage(bin, filename, 150);

                LOGGER.info("Преобразование файла пдф в JPG: " + filename);
            }

            document.close();

        } catch (IOException e) {
            LOGGER.info(" Не удалось преобразовать файл пдф в JPG: " + sourceFileName);
            e.printStackTrace();
        }
    }
}
