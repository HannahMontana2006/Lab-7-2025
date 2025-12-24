package functions.threads;

import functions.Function;
import functions.basic.Log;
import java.util.Random;

public class SimpleGenerator implements Runnable {

    private final Task task;
    private final Random rnd = new Random();

    public SimpleGenerator(Task task) {
        this.task = task;
    }

    public void run() {
        for (int i = 0; i < task.getCount(); i++) {
            synchronized (task) {
                double base = 1.0 + 9.0 * rnd.nextDouble();
                Function logFunc = new Log(base);
                task.setFunction(logFunc);

                double left = 100.0 * rnd.nextDouble();
                task.setLeftX(left);

                double right = 100.0 + 100.0 * rnd.nextDouble();
                task.setRightX(right);

                double step = rnd.nextDouble();
                if (step == 0) step = 0.01;
                task.setStep(step);

                System.out.printf("Source %.4f %.4f %.4f%n", left, right, step);
            }

            try { Thread.sleep(1); } catch (InterruptedException ignored) {}
        }
    }
}
