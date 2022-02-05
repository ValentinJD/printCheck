package ru.print.check.image;

import ru.print.check.model.ImageSize;
import ru.print.check.model.Pixel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import static ru.print.check.config.ValuesForConfig.*;

public class ImageToPageManufacturerImpl implements ImageToPageManufacturer{

    private static void putImageInPage(Pixel[][] check, int numberCheck, BufferedImage result) {

        // Делаем двойной цикл, чтобы пройти по файлу чеку
        for (int x = 1; x < check.length; x++) {
            for (int y = 1; y < check[0].length; y++) {

                // Берем цвет пикселя чека
                Color color = check[x][y].color;

                int incrementCheck = (numberCheck - 1) * WIDTH_PAGE / 3;

                int indent = (int) (WIDTH_PAGE - (padding * WIDTH_PAGE)) /4;
                int indentX = x + incrementCheck + indent;
                // И устанавливаем этот цвет в текущий пиксель
                result.setRGB(indentX, y, color.getRGB());
            }
        }
    }

    private static void putChecksOnPage(List<Pixel[][]> threeChecks) {
        // Открываем изображение
        LOGGER.info("Вставка трех чеков в лист");

        try {
            // Создаем новое пустое изображение, такого же размера
            BufferedImage result = new BufferedImage(WIDTH_PAGE, HEIGHT_PAGE, 5);

            blackInWhite(result);

            int numberCheck = 1;
            String pathname = getNameForNextPage(numberPageForPrint);

            File output = new File(pathname);
            for (int i = 0; i < threeChecks.size(); i++) {

                putImageInPage(threeChecks.get(i), numberCheck, result);

                if (i == threeChecks.size() - 1) {
                    ImageIO.write(result, "jpg", output);
                    numberPageForPrint++;
                    output = new File(getNameForNextPage(numberPageForPrint));
                    result = new BufferedImage(WIDTH_PAGE, HEIGHT_PAGE, 5);
                    numberCheck = 1;
                }

                numberCheck++;
            }
        } catch (IOException e) {
            LOGGER.info("Не удалось сохранить чеки на странице");
            e.printStackTrace();
        }
    }

    private static String getNameForNextPage(int numberPageA4ForPrint) {
        return "imageForPrint" + File.separator + "print-" + numberPageA4ForPrint + ".jpg";
    }

    private static void blackInWhite(BufferedImage source) {

        // Делаем двойной цикл, чтобы обработать каждый пиксель
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                // И устанавливаем белый цвет результирующего изображения
                source.setRGB(x, y, -1);
            }
        }
    }

    private static void putListImages(List<ImageSize> threeProcessedCheques) {

        List<Pixel[][]> threeChecks = new ArrayList<>();

        for (ImageSize cheque : threeProcessedCheques) {
            Pixel[][] check = getArrayPixels(cheque.path);
            threeChecks.add(check);
        }

        putChecksOnPage(threeChecks);
    }

    private static Pixel[][] getArrayPixels(String filename) {
        // Открываем изображение
        LOGGER.info("Копируем изображение в массив пикселей " + filename);
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
            LOGGER.info("Не удалось скопировать изображение в массив пикселей " + filename);
            e.printStackTrace();
        }

        return array;
    }

    public void putThreeCheckOnPage(Map<Integer, List<ImageSize>> groupsOnThreeCheck) {
        for (List<ImageSize> threeChecks : groupsOnThreeCheck.values()) {
            putListImages(threeChecks);
        }
    }
}
