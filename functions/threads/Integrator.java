package functions.threads;

import functions.Function;
import functions.Functions;

public class Integrator extends Thread {

    private final Task task;
    private final SemaphoreSlot semaphore;

    public Integrator(Task task, SemaphoreSlot semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    public void run() {
        for (int i = 0; i < task.getCount(); i++) {
            try {
                semaphore.acquireRead();

                Function func = task.getFunction();
                double left = task.getLeftX();
                double right = task.getRightX();
                double step = task.getStep();

                double result = Functions.integrate(func, left, right, step);
                System.out.printf("Result %.4f %.4f %.4f %.8f%n", left, right, step, result);

                semaphore.releaseRead();

                Thread.sleep(1);

            } catch (InterruptedException e) {
                System.out.println("Integrator прерван");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка интегрирования: " + e.getMessage());
            }
        }
    }
}
