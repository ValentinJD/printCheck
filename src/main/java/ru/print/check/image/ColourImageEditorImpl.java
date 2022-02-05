package ru.print.check.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import static ru.print.check.config.ValuesForConfig.brightness;
import static ru.print.check.config.ValuesForConfig.contrast;
import static ru.print.check.util.FileUtil.getFilesInDir;

public class ColourImageEditorImpl implements ColourImageEditor{
    @Override
    public void editColourImage() {
        List<File> images = getFilesInDir("images/");
        processingImagesToShadesOfGrey(images);
    }

    private static void createGreyImage(String filename) {
        try {

            // Открываем изображение
            LOGGER.info("Преобразуем в оттенки серого файл " + filename);
            File file = new File(filename);
            BufferedImage source = ImageIO.read(file);

            // Создаем новое пустое изображение, такого же размера
            BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());

            // Делаем двойной цикл, чтобы обработать каждый пиксель
            for (int x = 0; x < source.getWidth(); x++) {
                for (int y = 0; y < source.getHeight(); y++) {

                    // Получаем цвет текущего пикселя
                    Color color = new Color(source.getRGB(x, y));

                    // Получаем каналы этого цвета
                    int blue = color.getBlue();
                    int red = color.getRed();
                    int green = color.getGreen();

                    // Применяем стандартный алгоритм для получения черно-белого изображения
                    int grey = (int) (red * 0.299 + green * 0.587 + blue * 0.114);

                    int newRed = grey;
                    int newGreen = grey;
                    int newBlue = grey;

                    //  Создаем новый цвет
                    Color newColor = new Color(newRed, newGreen, newBlue);

                    // И устанавливаем этот цвет в текущий пиксель результирующего изображения
                    result.setRGB(x, y, newColor.getRGB());
                }
            }

            // Сохраняем результат в новый файл
            File output = new File(filename);

            BufferedImage processedImage = getContrastAndBrightnessImage(result);

            ImageIO.write(processedImage, "jpg", output);

        } catch (IOException e) {
            LOGGER.info("Не удалось преобразовать в оттенки серого файл " + filename);
        }
    }

    private static BufferedImage getContrastAndBrightnessImage(BufferedImage image) {
        RescaleOp rescaleOp = new RescaleOp(brightness, contrast, null);
        return rescaleOp.filter(image, image);
    }

    private static void processingImagesToShadesOfGrey(List<File> images) {
        for (File imageFileName : images) {
            createGreyImage(imageFileName.getPath());
        }
    }
}
