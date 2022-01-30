package ru.print.check;

import ru.print.check.converter.ConverterPdf;
import ru.print.check.converter.ConverterPdfToImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class Main {
    static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static ConverterPdf converterPdf = new ConverterPdfToImage();

    public static final int A4_HEIGHT = 3508;//1754
    public static final int A4_WIDTH = 2480;//1240

    public static void main(String[] args) throws IOException {

        List<File> filesInDir = getFilesInDir("pdfs/");
        PdfToJPG(filesInDir);

        List<File> images = getFilesInDir("images/");
        createGreyImages(images);

        List<File> greyImages = getFilesInDir("greyImage/");
        List<ImageSize> imageSizes = reSizeImagesOnHeight(greyImages);

        List<File> scalableImages = getFilesInDir("scalable" + File.separator);
        reSizeImagesOnThreeWidth(scalableImages, imageSizes);

        String fileNameA4 = "examples" + File.separator + "a40.jpg";
        putImagesToA4(scalableImages, fileNameA4);
    }

    private static void putImagesToA4(List<File> scalableImages, String pathToOut) {

        List<Pixel[][]> listChecks = new ArrayList<>();

        for (File imageFileName : scalableImages) {
            Pixel[][] check = copyCheckToArrayPixels(imageFileName.getPath());
            listChecks.add(check);
        }

        putImageToA4(listChecks);
    }

    static List<ImageSize> reSizeImagesOnHeight(List<File> greyImages) {
        List<ImageSize> imageSizes = new ArrayList<>();

        try {
            int countGreyFile = 1;
            for (File grey : greyImages) {
                String pathToScalableImage = "scalable" + File.separator + countGreyFile++ + ".jpg";
                LOGGER.info("Масштабируем файл: " + grey.getPath());
                // Проверяем высоту
                String imagePathToRead = grey.getPath();
                File fileToRead = new File(imagePathToRead);
                BufferedImage bufferedImageInput = ImageIO.read(fileToRead);

                int height = bufferedImageInput.getHeight();
                int width = bufferedImageInput.getWidth();

                ImageSize imageSize = new ImageSize();
                imageSize.height = height;
                imageSize.width = width;
                imageSize.path = pathToScalableImage;
                imageSizes.add(imageSize);

                float k1 = 1;
                // Масштабируем по высоте если нужно
                if (height > A4_HEIGHT) {
                    k1 = (float) A4_HEIGHT / (float) (height);
                }
                resizeFile(imagePathToRead, pathToScalableImage, k1);
            }
        } catch (IOException e) {
            System.out.println("Не удалось масштабировать файл: ");
            e.printStackTrace();
        }

        return imageSizes;

    }

    private static void reSizeImagesOnThreeWidth(List<File> scalableImagesList, List<ImageSize> imageSizeList) {
        // Логика по изменению размера файлов по три штуки на лист
        // Берем по три
        List<ImageSize> imageSizeThree = new ArrayList<>();

        for (int i = 0; i < scalableImagesList.size(); i++) {

            if (i > 2) {
                break;
            }
            imageSizeThree.add(imageSizeList.get(i));
        }
        // Определяем общую ширину трех чеков
        LOGGER.info("Определяем общую ширину трех чеков");
        int sumWidthThreeImage = 0;

        for (ImageSize imageSize : imageSizeThree) {

            sumWidthThreeImage += imageSize.width;
        }
        LOGGER.info("Ширина трех файлов: " + sumWidthThreeImage);

        // Определяем коэффициент уменьшения по ширине
        LOGGER.info("Определяем коэффициент уменьшения по ширине ");
        float k2 = 1;
        if (sumWidthThreeImage > A4_WIDTH) {
            k2 = (float) A4_WIDTH / sumWidthThreeImage;

            // Уменьшаем если ширина превышает
            int countGreyFileResize = 1;
            String pathToScalableImage = "scalable" + File.separator + countGreyFileResize++ + ".jpg";

            for (ImageSize imageSize : imageSizeThree) {
                try {
                    LOGGER.info("Уменьшаем по ширине " + imageSize.path);
                    resizeFile(imageSize.path, pathToScalableImage, k2);
                } catch (IOException e) {
                    LOGGER.info("Не удалось изменить масштаб по ширине " + imageSize.path);
                    e.printStackTrace();
                }
            }
        }
    }

    static void PdfToJPG(List<File> filesInDir) {
        Integer countPage = 0;
        for (File filesName : filesInDir) {
            converterPdf.toJPG(filesName.getPath(), "images" + File.separator + "" + countPage);
            countPage++;

        }
    }

    static void createGreyImages(List<File> images) {
        int countImageFile = 0;
        for (File imageFileName : images) {
            createGreyImage(imageFileName.getPath(), countImageFile);
            countImageFile++;
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

    static void createGreyImage(String filename, int numberFile) {
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

                    // Если вы понаблюдаете, то заметите что у любого оттенка серого цвета, все каналы имеют
                    // одно и то же значение. Так, как у нас изображение тоже будет состоять из оттенков серого
                    // то, все каналы будут иметь одно и то же значение.
                    int newRed = grey;
                    int newGreen = grey;
                    int newBlue = grey;

                    //  Cоздаем новый цвет
                    Color newColor = new Color(newRed, newGreen, newBlue);

                    // И устанавливаем этот цвет в текущий пиксель результирующего изображения
                    result.setRGB(x, y, newColor.getRGB());
                }
            }

            // Сохраняем результат в новый файл
            File output = new File("greyImage" + File.separator + numberFile + ".jpg");
            ImageIO.write(result, "jpg", output);

        } catch (IOException e) {
            LOGGER.info("Не удалось преобразовать в оттенки серого файл " + filename);
            // При открытии и сохранении файлов, может произойти неожиданный случай.
            // И на этот случай у нас try catch

        }
    }

    static void putImageInPage(Pixel[][] check, int numberCheck, BufferedImage result) {

        // Делаем двойной цикл, чтобы пройти по файлу чеку
        for (int x = 1; x < check.length; x++) {
            for (int y = 1; y < check[0].length; y++) {

                // Берем цвет пикселя чека
                Color color = check[x][y].color;
                // И устанавливаем этот цвет в текущий пиксель

                int incrementCheck = (numberCheck - 1) * A4_WIDTH / 3;

                result.setRGB(x + incrementCheck, y, color.getRGB());
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

    static void putImageToA4(List<Pixel[][]> listChecks) {
        // Открываем изображение
        LOGGER.info("Вставка чека в образец А4 ");

        int numberPageA4ForPrint = 1;

        try {
            // Создаем новое пустое изображение, такого же размера
            BufferedImage result = new BufferedImage(A4_WIDTH, A4_HEIGHT, 5);

            blackInWhite(result);

            int numberCheck = 1;
            String pathname = getNameForNextA4Page(numberPageA4ForPrint);
            LOGGER.info("Сохраняем результат в новый файл А4 " + pathname);
            File output = new File(pathname);
            for (int i = 0; i < listChecks.size(); i++) {

                int numberCheckInPage = 3;

                if (numberCheck > numberCheckInPage) {
                    // Сохраняем результат в новый файл

                    ImageIO.write(result, "jpg", output);
                    numberPageA4ForPrint++;
                    output = new File(getNameForNextA4Page(numberPageA4ForPrint));
                    result = new BufferedImage(A4_WIDTH, A4_HEIGHT, 5);
                    numberCheck = 1;
                    blackInWhite(result);
                }

                putImageInPage(listChecks.get(i), numberCheck, result);

                if (i == listChecks.size() - 1) {
                    ImageIO.write(result, "jpg", output);
                    numberPageA4ForPrint++;
                    output = new File(getNameForNextA4Page(numberPageA4ForPrint));
                    result = new BufferedImage(A4_WIDTH, A4_HEIGHT, 5);
                    numberCheck = 1;
                }

                numberCheck++;
            }


        } catch (IOException e) {
            LOGGER.info("Не удалось сохранить результат в новый файл А4");
            e.printStackTrace();
        }
    }

    private static String getNameForNextA4Page(int numberPageA4ForPrint) {
        String pathname = "imageForPrint" + File.separator + "print-" + numberPageA4ForPrint++ + ".jpg";
        return pathname;
    }

    static Pixel[][] copyCheckToArrayPixels(String filename) {
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

    public static void resizeFile(String imagePathToRead, String imagePathToWrite, float kScalable)
            throws IOException {

        LOGGER.info("Масштабируем изображение " + imagePathToRead + "с коэффициентом" + kScalable);

        File fileToRead = new File(imagePathToRead);
        BufferedImage bufferedImageInput = ImageIO.read(fileToRead);

        int resizeWidth = (int) (bufferedImageInput.getWidth() * kScalable);
        int resizeHeight = (int) (bufferedImageInput.getHeight() * kScalable);

        BufferedImage bufferedImageOutput = new BufferedImage(resizeWidth,
                resizeHeight, bufferedImageInput.getType());

        Graphics2D g2d = bufferedImageOutput.createGraphics();
        g2d.drawImage(bufferedImageInput, 0, 0, resizeWidth, resizeHeight, null);
        g2d.dispose();

        String formatName = imagePathToWrite.substring(imagePathToWrite
                .lastIndexOf(".") + 1);

        ImageIO.write(bufferedImageOutput, formatName, new File(imagePathToWrite));
    }
}
