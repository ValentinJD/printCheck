package ru.print.check.image;

import ru.print.check.model.ImageSize;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import static ru.print.check.config.ValuesForConfig.DIR_FOR_IMAGE_FILES;
import static ru.print.check.config.ValuesForConfig.HEIGHT_PAGE;
import static ru.print.check.util.FileUtil.getFilesInDir;

public class ImageSizeFitterImpl implements ImageSizeFitter {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public Map<Integer, List<ImageSize>> getGroupByThreeImageSizeMap() {
        List<ImageSize> imageSizeList = fitImageSizeToPage();

        Map<Integer, List<ImageSize>> groupingMap = new HashMap<>();

        List<ImageSize> imageSizeThree = new ArrayList<>();

        int idList = 0;
        int numberImageSize = 1;
        for (int i = 0; i < imageSizeList.size(); i++) {

            if (numberImageSize <= 3) {
                imageSizeThree.add(imageSizeList.get(i));

                if (numberImageSize == 3 || i == imageSizeList.size() - 1) {

                    groupingMap.put(idList, imageSizeThree);
                    idList++;
                    imageSizeThree = new ArrayList<>();
                    numberImageSize = 0;
                }

                numberImageSize++;
            }
        }
        return groupingMap;
    }

    private void fitImageOnHeight(String imagePathToRead, int height) throws IOException {
        float kScalable = 1;
        // Масштабируем по высоте если нужно
        if (height > HEIGHT_PAGE) {
            kScalable = (float) HEIGHT_PAGE / (float) height;
        }
        resizeImageByRatio(imagePathToRead, kScalable);
    }

    private void resizeImageByRatio(String imagePath, float kScalable)
            throws IOException {

        logger.info("Масштабируем изображение " + imagePath + " с коэффициентом" + kScalable);

        File fileToRead = new File(imagePath);
        BufferedImage bufferedImageInput = ImageIO.read(fileToRead);

        int resizeWidth = (int) (bufferedImageInput.getWidth() * kScalable);
        int resizeHeight = (int) (bufferedImageInput.getHeight() * kScalable);

        BufferedImage bufferedImageOutput = new BufferedImage(resizeWidth,
                resizeHeight, bufferedImageInput.getType());

        Graphics2D g2d = bufferedImageOutput.createGraphics();
        g2d.drawImage(bufferedImageInput, 0, 0, resizeWidth, resizeHeight, null);
        g2d.dispose();

        String formatName = imagePath.substring(imagePath.lastIndexOf(".") + 1);

        ImageIO.write(bufferedImageOutput, formatName, new File(imagePath));
    }

    private void getImageSizeList(List<File> images, List<ImageSize> imageSizes) {
        try {
            for (File image : images) {
                logger.info("Масштабируем файл: " + image.getPath());
                // Проверяем высоту
                String imagePathToRead = image.getPath();
                File fileToRead = new File(imagePathToRead);
                BufferedImage bufferedImage = ImageIO.read(fileToRead);

                int height = bufferedImage.getHeight();
                int width = bufferedImage.getWidth();

                ImageSize imageSize = getImageSize(image, height, width);
                imageSizes.add(imageSize);

                fitImageOnHeight(imagePathToRead, height);
            }
        } catch (IOException e) {
            System.out.println("Не удалось уменьшить по высоте страницы файл: ");
            e.printStackTrace();
        }
    }

    private ImageSize getImageSize(File grey, int height, int width) {
        ImageSize imageSize = new ImageSize();
        imageSize.height = height;
        imageSize.width = width;
        imageSize.path = grey.getPath();
        return imageSize;
    }

    private List<ImageSize> reSizeImagesOnHeight(List<File> images) {
        List<ImageSize> imageSizes = new ArrayList<>();

        getImageSizeList(images, imageSizes);

        return imageSizes;
    }

    private List<ImageSize> fitImageSizeToPage() {
        List<File> greyImages = getFilesInDir(DIR_FOR_IMAGE_FILES);
        return reSizeImagesOnHeight(greyImages);
    }
}
