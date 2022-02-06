package ru.print.check;

public class PrintCheckApp {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Имя основного потока" + "\n" +
                Thread.currentThread().getName()  + "\n" + "старт программы");
        ImagePageFactory imagePageFactory = new ImagePageFactory();
        imagePageFactory.createPagesForPrint();
        System.out.println("Имя основного потока" + "\n" +
                Thread.currentThread().getName() +  "\n" + "окончание программы");

/*        while (true) {
            System.out.println("Мейн работает");
            Thread.sleep(5000);
            for (int i = 0; i < 1000000; i++) {
                int o = i + 950;
                System.out.println("Вычисляем");
            }
        }*/
    }
}
