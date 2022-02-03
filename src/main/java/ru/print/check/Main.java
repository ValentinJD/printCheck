package ru.print.check;

import ru.print.check.converter.ConverterPdf;
import ru.print.check.converter.ConverterPdfToImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static ConverterPdf converterPdf = new ConverterPdfToImage();

    public static final int HEIGHT_PAGE = 3508;//3508;//1754
    public static final int WIDTH_PAGE = 2480;//2480;//1240
    public static int numberPageForPrint = 1;
    public static float padding = 0.9f;
    public static float brightness = 4f;
    public static float contrast = -700f;

    public static void main(String[] args) {

        List<File> filesInDir = getFilesInDir("pdfs/");
        PdfToJPG(filesInDir);

        List<File> images = getFilesInDir("images/");
        processingImagesToShadesOfGrey(images);

        List<File> greyImages = getFilesInDir("images/");
        List<ImageSize> imageSizes = reSizeImagesOnHeight(greyImages);

        Map<Integer, List<ImageSize>> groupByThreeImageMap = getGroupByThreeImageSizeMap(imageSizes);

        reSizeImagesOnThreeByWidthPage(groupByThreeImageMap, padding);

        putThreeCheckOnPage(groupByThreeImageMap);
    }

    private static void putThreeCheckOnPage(Map<Integer, List<ImageSize>> groupsOnThreeCheck) {
        for (List<ImageSize> threeChecks : groupsOnThreeCheck.values()) {
            putListImages(threeChecks);
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

    static List<ImageSize> reSizeImagesOnHeight(List<File> images) {
        List<ImageSize> imageSizes = new ArrayList<>();

        getImageSizeList(images, imageSizes);

        return imageSizes;
    }

    private static void getImageSizeList(List<File> images, List<ImageSize> imageSizes) {
        try {
            for (File image : images) {
                LOGGER.info("Масштабируем файл: " + image.getPath());
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

    private static void fitImageOnHeight(String imagePathToRead, int height) throws IOException {
        float kScalable = 1;
        // Масштабируем по высоте если нужно
        if (height > HEIGHT_PAGE) {
            kScalable = (float) HEIGHT_PAGE / (float) height;
        }
        resizeImageByRatio(imagePathToRead, kScalable);
    }

    private static ImageSize getImageSize(File grey, int height, int width) {
        ImageSize imageSize = new ImageSize();
        imageSize.height = height;
        imageSize.width = width;
        imageSize.path = grey.getPath();
        return imageSize;
    }

    private static Map<Integer, List<ImageSize>> getGroupByThreeImageSizeMap(List<ImageSize> imageSizeList) {

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

    private static void reSizeImagesOnThreeByWidthPage(Map<Integer, List<ImageSize>> groupByThreeImageMap, float padding) {

        // Определяем общую ширину трех чеков
        LOGGER.info("Определяем общую ширину трех чеков");

        for (List<ImageSize> imageSizeThree : groupByThreeImageMap.values()) {
            int sumWidthThreeImage = imageSizeThree.stream()
                    .mapToInt(img -> img.width).sum();

            LOGGER.info("Ширина трех файлов: " + sumWidthThreeImage);

            LOGGER.info("Определяем коэффициент уменьшения по ширине ");
            // Уменьшаем если ширина превышает
            if (sumWidthThreeImage > WIDTH_PAGE) {
                float kScalable = ( (float) WIDTH_PAGE / sumWidthThreeImage ) * padding;

                for (ImageSize imageSize : imageSizeThree) {
                    try {
                        LOGGER.info("Уменьшаем по ширине " + imageSize.path);
                        resizeImageByRatio(imageSize.path, kScalable);
                    } catch (IOException e) {
                        LOGGER.info("Не удалось изменить масштаб по ширине " + imageSize.path);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static void PdfToJPG(List<File> listPdfFiles) {
        int countPage = 0;
        for (File fileName : listPdfFiles) {
            converterPdf.toJPG(fileName.getPath(), "images" + File.separator + "" + countPage);
            countPage++;
        }
    }

    static void processingImagesToShadesOfGrey(List<File> images) {
        for (File imageFileName : images) {
            createGreyImage(imageFileName.getPath());
        }
    }

    static List<File> getFilesInDir(String path) {
        File dir = new File(path); //path указывает на директорию

        List<File> files = new ArrayList<>();

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            System.out.println(file.getName());

            if (file.isFile()) {
                files.add(file);
            }
        }

        return files;
    }

    static void createGreyImage(String filename) {
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


    static void putImageInPage(Pixel[][] check, int numberCheck, BufferedImage result) {

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

    static void blackInWhite(BufferedImage source) {

        // Делаем двойной цикл, чтобы обработать каждый пиксель
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                // И устанавливаем белый цвет результирующего изображения
                source.setRGB(x, y, -1);
            }
        }
    }

    static void putChecksOnPage(List<Pixel[][]> threeChecks) {
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

    static Pixel[][] getArrayPixels(String filename) {
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

    public static void resizeImageByRatio(String imagePath, float kScalable)
            throws IOException {

        LOGGER.info("Масштабируем изображение " + imagePath + " с коэффициентом" + kScalable);

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
}
