package ru.print.check.converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class ConverterPdfToImage implements ConverterPdf {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    public void convertPdfToJPG(String sourceFileName, String destFileName) {
        toJPG(sourceFileName, destFileName);
    }

    private void toJPG(String sourceFileName, String destFileName) {
        try {
            PDDocument document = PDDocument.loadNonSeq(new File(sourceFileName), null);

            List<PDPage> allPages = document.getDocumentCatalog().getAllPages();

            int pages = 0;

            for (PDPage pdPage : allPages) {
                ++pages;

                BufferedImage bin = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 150);

                String filename = destFileName + "-" + pages + ".jpg";

                ImageIOUtil.writeImage(bin, filename, 150);

                logger.info("Преобразование файла пдф в JPG: {}", filename);
            }

            document.close();

        } catch (IOException e) {
            logger.info(" Не удалось преобразовать файл пдф в JPG: {}", sourceFileName);
            e.printStackTrace();
        }
    }

}
