package functions.threads;

import functions.basic.Log;
import functions.Function;
import java.util.Random;

public class Generator extends Thread {

    private final Task task;
    private final SemaphoreSlot semaphore;
    private final Random rnd = new Random();

    public Generator(Task task, SemaphoreSlot semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    public void run() {
        for (int i = 0; i < task.getCount(); i++) {
            try {
                semaphore.acquireWrite();
                double base = 1.0 + 9.0 * rnd.nextDouble();
                Function logFunc = new Log(base);
                task.setFunction(logFunc);

                double left = 100.0 * rnd.nextDouble();
                double right = 100.0 + 100.0 * rnd.nextDouble();
                double step = rnd.nextDouble();
                if (step == 0) step = 0.01;

                task.setLeftX(left);
                task.setRightX(right);
                task.setStep(step);

                System.out.printf("Source %.4f %.4f %.4f%n", left, right, step);

                semaphore.releaseWrite();

                Thread.sleep(1);

            } catch (InterruptedException e) {
                System.out.println("Generator прерван");
                break;
            }
        }
    }
}
