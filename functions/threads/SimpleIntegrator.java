package functions.threads;

import functions.Function;
import functions.Functions;

public class SimpleIntegrator implements Runnable {

    private final Task task;

    public SimpleIntegrator(Task task) {
        this.task = task;
    }

    public void run() {
        for (int i = 0; i < task.getCount(); i++) {
            double left, right, step;
            Function func;

            synchronized (task) {
                func = task.getFunction();
                left = task.getLeftX();
                right = task.getRightX();
                step = task.getStep();
            }

            try {
                double result = Functions.integrate(func, left, right, step);
                System.out.printf("Result %.4f %.4f %.4f %.8f%n", left, right, step, result);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка интегрирования: " + e.getMessage());
            }

            try { Thread.sleep(1); } catch (InterruptedException ignored) {}
        }
    }
}
