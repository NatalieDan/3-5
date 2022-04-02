import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class MainClass {
    public static final int CARS_COUNT = 4;
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(CARS_COUNT + 1);
        Semaphore semaphore = new Semaphore(CARS_COUNT/2);
        CountDownLatch cdl = new CountDownLatch(CARS_COUNT);
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        for (int i = 0; i < cars.length; i++) {
            int index = i;
            new Thread(() -> {
                try {
                    System.out.println(cars[index].getName() + " готовится");
                    Thread.sleep(500 + (int)(Math.random() * 800));
                    System.out.println(cars[index].getName() + " готов");
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    for (int j = 0; j < race.getStages().size(); j++) {
                        race.getStages().get(j).go(cars[index],semaphore);
                        if (j == race.getStages().size()-1){
                            cdl.countDown();
                            System.out.println(cars[index].getName() + " ФИНИШ");
                        }
                    }
                }
            }).start();
        }
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}

