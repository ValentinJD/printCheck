package ru.print.check.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.print.check.model.ImageSize;
import ru.print.check.model.Pixel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.print.check.config.ValuesForConfig.*;

public class ImageToPageManufacturerImpl implements ImageToPageManufacturer {
    static final Logger logger = LoggerFactory.getLogger(ImageToPageManufacturerImpl.class);

    @Override
    public synchronized void putThreeCheckOnPage(List<ImageSize> threeChecks) {
        putListImages(threeChecks);
    }

    private void putImageInPage(Pixel[][] check, int numberCheck, BufferedImage result) {

        // Делаем двойной цикл, чтобы пройти по файлу чеку
        for (int x = 1; x < check.length; x++) {
            for (int y = 1; y < check[0].length; y++) {

                // Берем цвет пикселя чека
                Color color = check[x][y].color;

                int incrementCheck = (numberCheck - 1) * WIDTH_PAGE / 3;

                int indent = (int) (WIDTH_PAGE - (padding * WIDTH_PAGE)) / 4;
                int indentX = x + incrementCheck + indent;
                // И устанавливаем этот цвет в текущий пиксель
                result.setRGB(indentX, y, color.getRGB());
            }
        }
    }

    private void putChecksOnPage(List<Pixel[][]> threeChecks) {
        // Открываем изображение
        logger.info("Вставка трех чеков в лист");

        try {
            // Создаем новое пустое изображение, такого же размера
            BufferedImage result = getExamplePage();

            int numberCheck = 1;
            String pathname = getNameForNextPage(numberPageForPrint.get());

            File output = new File(pathname);
            for (int i = 0; i < threeChecks.size(); i++) {

                putImageInPage(threeChecks.get(i), numberCheck, result);

                if (i == threeChecks.size() - 1) {
                    ImageIO.write(result, "jpg", output);
                    numberPageForPrint.incrementAndGet();
                    output = new File(getNameForNextPage(numberPageForPrint.get()));
                    result = new BufferedImage(WIDTH_PAGE, HEIGHT_PAGE, 5);
                    numberCheck = 1;
                }

                numberCheck++;
            }
        } catch (Exception e) {
            logger.info("Не удалось сохранить чеки на странице");
            e.printStackTrace();
        }
    }

    private String getNameForNextPage(int numberPageA4ForPrint) {
        return DIR_FOR_PAGE_FOR_PRINT + "print-" + numberPageA4ForPrint + ".jpg";
    }

    private BufferedImage blackInWhite(BufferedImage source) {
        // Create the image
        Graphics2D graphics = source.createGraphics();

        // Fill the background with white color
        Color rgb = new Color(255, 255, 255);
        graphics.setColor(rgb);
        graphics.fillRect(0, 0, source.getWidth(), source.getHeight());
        return source;
    }

    private BufferedImage getExamplePage() {
        BufferedImage image;
        image = new BufferedImage(WIDTH_PAGE, HEIGHT_PAGE, 5);
        blackInWhite(image);
        return image;
    }

    private void putListImages(List<ImageSize> threeProcessedCheques) {

        List<Pixel[][]> threeChecks = new ArrayList<>();

        for (ImageSize cheque : threeProcessedCheques) {
            Pixel[][] check = getArrayPixels(cheque.path);
            threeChecks.add(check);
        }

        putChecksOnPage(threeChecks);
    }

    private Pixel[][] getArrayPixels(String filename) {
        // Открываем изображение
        logger.info("Копируем изображение в массив пикселей {}", filename);
        File check = new File(filename);
        Pixel[][] array = null;

        try {
            BufferedImage image = ImageIO.read(check);

            array = new Pixel[image.getWidth()][image.getHeight()];

            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    // Получаем цвет текущего пикселя
                    Color pixel = new Color(image.getRGB(x, y));
                    // Копируем пиксель в массив
                    array[x][y] = new Pixel(x, y, pixel);
                }
            }
        } catch (IOException e) {
            logger.info("Не удалось скопировать изображение в массив пикселей {}", filename);
            e.printStackTrace();
        }

        return array;
    }
}
