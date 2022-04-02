package ru.print.check.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;


import static ru.print.check.config.ValuesForConfig.BRIGHTNESS;
import static ru.print.check.config.ValuesForConfig.CONTRAST;

public class ColourImageEditorImpl implements ColourImageEditor {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    public void editColourImage(String path, String destFile) {
        createGreyImage(path);
    }

    private void createGreyImage(String filename) {
        try {

            // Открываем изображение
            logger.info("{} Преобразуем в оттенки серого файл {}", Thread.currentThread().getName(), filename);
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

                    //  Создаем новый цвет
                    Color newColor = new Color(grey, grey, grey);

                    // И устанавливаем этот цвет в текущий пиксель результирующего изображения
                    result.setRGB(x, y, newColor.getRGB());
                }
            }

            // Сохраняем результат в новый файл
            File output = new File(filename);

            BufferedImage processedImage = getContrastAndBrightnessImage(result);

            ImageIO.write(processedImage, "jpg", output);

        } catch (IOException e) {
            logger.info("{} Не удалось преобразовать в оттенки серого файл {}", Thread.currentThread().getName(), filename);
        }
    }

    private BufferedImage getContrastAndBrightnessImage(BufferedImage image) {
        RescaleOp rescaleOp = new RescaleOp(BRIGHTNESS, CONTRAST, null);
        return rescaleOp.filter(image, image);
    }
}
