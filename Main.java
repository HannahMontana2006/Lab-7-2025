import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;
import functions.threads.*;
import java.util.Random;
public class Main {

    public static void main(String[] args) {

        TabulatedFunction f = new ArrayTabulatedFunction(0, 4, new double[]{0, 1, 4, 9, 16});
        // TabulatedFunction f = new LinkedListTabulatedFunction(0, 4, new double[]{0, 1, 4, 9, 16});

        System.out.println("Исходная табулированная функция:");
        for (int i = 0; i < f.getPointsCount(); i++) {
            System.out.println("(" + f.getPoint(i).getX() + ", " + f.getPoint(i).getY() + ")");
        }

        System.out.println("\nПроверка значений функции:");
        double[] testX = {-1, 0, 1.5, 2, 2.5, 3.7, 5};
        for (double x : testX) {
            System.out.println("f(" + x + ") = " + f.getFunctionValue(x));
        }

        System.out.println("\nПроверка методов добавления, изменения и удаления:");
        try {
            f.addPoint(new FunctionPoint(5, 25));

            FunctionPoint p = f.getPoint(1);
            f.setPointY(1, p.getY() + 10);

            f.deletePoint(0);

            System.out.println("Текущее состояние точек:");
            for (int i = 0; i < f.getPointsCount(); i++) {
                System.out.println("(" + f.getPoint(i).getX() + ", " + f.getPoint(i).getY() + ")");
            }

        } catch (Exception e) {
            System.out.println("Исключение: " + e);
        }

        System.out.println("\nПроверка исключений:");
        try { f.getPoint(-1); }
        catch (Exception e) { System.out.println("Ожидаемое: " + e); }

        try { f.addPoint(new FunctionPoint(2, 100)); }
        catch (Exception e) { System.out.println("Ожидаемое: " + e); }

        try {
            while (f.getPointsCount() > 2) f.deletePoint(0);
            f.deletePoint(0);
        } catch (Exception e) {
            System.out.println("Ожидаемое: " + e);
        }

        System.out.println("\nФинальное состояние точек:");
        for (int i = 0; i < f.getPointsCount(); i++) {
            System.out.println("(" + f.getPoint(i).getX() + ", " + f.getPoint(i).getY() + ")");
        }

        System.out.println("\nПроверка Sin и Cos");
        Function sin = new Sin();
        Function cos = new Cos();
        double step = 0.1;

        for (double x = 0; x <= Math.PI; x += step) {
            System.out.printf("x=%.2f sin=%.4f cos=%.4f%n", x,
                    sin.getFunctionValue(x), cos.getFunctionValue(x));
        }

        System.out.println("\nТабулированные Sin и Cos");
        TabulatedFunction sinTab = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction cosTab = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);

        for (double x = 0; x <= Math.PI; x += step) {
            System.out.printf("x=%.2f sinTab=%.4f cosTab=%.4f%n",
                    x, sinTab.getFunctionValue(x), cosTab.getFunctionValue(x));
        }

        System.out.println("\nСумма квадратов табулированных функций");
        Function sinCosSquareSum = Functions.sum(
                Functions.power(sinTab, 2),
                Functions.power(cosTab, 2)
        );

        for (double x = 0; x <= Math.PI; x += step) {
            System.out.printf("x=%.2f sumSquares=%.4f%n",
                    x, sinCosSquareSum.getFunctionValue(x));
        }

        System.out.println("\nЭкспонента:");
        Function exp = new Exp();
        TabulatedFunction expTab = TabulatedFunctions.tabulate(exp, 0, 10, 11);
        String expFile = "exp.txt";

        try (Writer writer = new FileWriter(expFile)) {
            TabulatedFunctions.writeTabulatedFunction(expTab, writer);
        } catch (IOException e) { e.printStackTrace(); }

        TabulatedFunction expTabFromFile = null;
        try (Reader reader = new FileReader(expFile)) {
            expTabFromFile = TabulatedFunctions.readTabulatedFunction(reader);
        } catch (IOException e) { e.printStackTrace(); }

        System.out.println("x | exp(x) | expTab(x)");
        for (int x = 0; x <= 10; x++) {
            System.out.printf("%d | %.4f | %.4f%n", x,
                    exp.getFunctionValue(x), expTabFromFile.getFunctionValue(x));
        }

        System.out.println("\nЛогарифм:");
        Function log = new Log(Math.E);
        TabulatedFunction logTab = TabulatedFunctions.tabulate(log, 0.1, 10, 11);
        String logFile = "log.dat";

        try (OutputStream out = new FileOutputStream(logFile)) {
            TabulatedFunctions.outputTabulatedFunction(logTab, out);
        } catch (IOException e) { e.printStackTrace(); }

        TabulatedFunction logTabFromFile = null;
        try (InputStream in = new FileInputStream(logFile)) {
            logTabFromFile = TabulatedFunctions.inputTabulatedFunction(in);
        } catch (IOException e) { e.printStackTrace(); }

        System.out.println("x | ln(x) | logTab(x)");
        for (double x = 0.1; x <= 10; x += 1) {
            System.out.printf("%.1f | %.4f | %.4f%n", x,
                    log.getFunctionValue(x), logTabFromFile.getFunctionValue(x));
        }


        System.out.println("\nТабулирование ln(exp(x)) на отрезке [0;10] с 11 точками");

        Function ln = new Log(Math.E);
        Function expFunc = new Exp();
        Function lnOfExp = new Composition(ln, expFunc);

        TabulatedFunction lnExpTab = TabulatedFunctions.tabulate(lnOfExp, 0, 10, 11);

        String lnExpFile = "ln_exp.bin";
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(lnExpFile))) {
            out.writeObject(lnExpTab);
            System.out.println("ln(exp(x)) успешно сериализована в файл: " + lnExpFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TabulatedFunction lnExpTabFromFile = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(lnExpFile))) {
            lnExpTabFromFile = (TabulatedFunction) in.readObject();
            System.out.println("ln(exp(x)) успешно десериализована из файла: " + lnExpFile);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("\nx | ln(exp(x)) табл. | ln(exp(x)) из файла");
        for (int x = 0; x <= 10; x++) {
            System.out.printf("%d | %.4f | %.4f%n", x,
                    lnExpTab.getFunctionValue(x),
                    lnExpTabFromFile.getFunctionValue(x));
        }

        System.out.println("\nПРОВЕРКА toString()");

        TabulatedFunction arr1 = new ArrayTabulatedFunction(0, 4, new double[]{0, 1, 4, 9, 16});
        TabulatedFunction list1 = new LinkedListTabulatedFunction(0, 4, new double[]{0, 1, 4, 9, 16});

        System.out.println("ArrayTabulatedFunction:");
        System.out.println(arr1.toString());

        System.out.println("\nLinkedListTabulatedFunction:");
        System.out.println(list1.toString());


        System.out.println("\nПРОВЕРКА equals()");

        TabulatedFunction arr2 = new ArrayTabulatedFunction(0, 4, new double[]{0, 1, 4, 9, 16});
        TabulatedFunction list2 = new LinkedListTabulatedFunction(0, 4, new double[]{0, 1, 4, 9, 16});

        System.out.println("arr1.equals(arr2): " + arr1.equals(arr2));
        System.out.println("arr1.equals(list1): " + arr1.equals(list1));
        System.out.println("list1.equals(list2): " + list1.equals(list2));

        arr2.setPointY(2, arr2.getPoint(2).getY() + 0.001);

        System.out.println("arr1.equals(arr2) после изменения: " + arr1.equals(arr2));


        System.out.println("\nПРОВЕРКА hashCode()");

        System.out.println("hashCode arr1: " + arr1.hashCode());
        System.out.println("hashCode arr2: " + arr2.hashCode());
        System.out.println("hashCode list1: " + list1.hashCode());
        System.out.println("hashCode list2: " + list2.hashCode());


        System.out.println("\nПРОВЕРКА clone()");

        try {
            // Клонируем Array и LinkedList
            ArrayTabulatedFunction arrClone = (ArrayTabulatedFunction) arr1.clone();
            LinkedListTabulatedFunction listClone = (LinkedListTabulatedFunction) list1.clone();

            System.out.println("\nКлон Array:");
            System.out.println(arrClone);

            System.out.println("\nКлон LinkedList:");
            System.out.println(listClone);

            // Изменяем оригиналы
            arr1.setPointY(1, 999);
            list1.setPointY(1, 777);

            System.out.println("\nПосле изменения оригиналов:");

            System.out.println("\nОригинал Array:");
            System.out.println(arr1);

            System.out.println("\nКлон Array (НЕ ДОЛЖЕН ПОМЕНЯТЬСЯ):");
            System.out.println(arrClone);

            System.out.println("\nОригинал LinkedList:");
            System.out.println(list1);

            System.out.println("\nКлон LinkedList (НЕ ДОЛЖЕН ПОМЕНЯТЬСЯ):");
            System.out.println(listClone);

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        System.out.println("\nКОНЕЦ ПРОВЕРОК");
        System.out.println("\nПРОВЕРКА ИНТЕГРИРОВАНИЯ");

        double integral = Functions.integrate(exp, 0, 1, 0.1);
        System.out.printf("Интеграл exp(x) на [0,1] с шагом 0.1 ≈ %.8f%n", integral);

        double theoretical = Math.exp(1) - Math.exp(0); // e^1 - e^0 = e - 1
        double integrateStep = 1.0;
        double diff;
        do {
            integrateStep /= 2.0;
            integral = Functions.integrate(exp, 0, 1, integrateStep);
            diff = Math.abs(integral - theoretical);
        } while (diff >= 1e-7);

        System.out.printf("Для точности 7-го знака после запятой нужен шаг ≈ %.10f, интеграл ≈ %.10f%n",
                integrateStep, integral);
        System.out.println("Запуск последовательной проверки (nonThread)");
        nonThread();
        System.out.println("Проверка завершена.");
        System.out.println("\nЗапуск поточной версии");
        simpleThreads();
        complicatedThreads();

        for (FunctionPoint p : f) {
            System.out.println(p);
        }

        Function cosFunc = new Cos();
        TabulatedFunction tf;

        double left = Math.max(0, cosFunc.getLeftDomainBorder());
        double right = Math.min(Math.PI, cosFunc.getRightDomainBorder());

        tf = TabulatedFunctions.tabulate(ArrayTabulatedFunction.class, cosFunc, left, right, 11);
        System.out.println(tf.getClass());

        tf = TabulatedFunctions.tabulate(LinkedListTabulatedFunction.class, cosFunc, left, right, 11);
        System.out.println(tf.getClass());

        tf = TabulatedFunctions.tabulate(ArrayTabulatedFunction.class, cosFunc, left, right, 11);
        System.out.println(tf.getClass());
        System.out.println("\nРабота программы завершена.");

    }
    public static void complicatedThreads() {
        Task task = new Task();
        task.setCount(100);

        SemaphoreSlot semaphore = new SemaphoreSlot();

        Generator gen = new Generator(task, semaphore);
        Integrator integ = new Integrator(task, semaphore);

        gen.setPriority(Thread.MAX_PRIORITY);
        integ.setPriority(Thread.MIN_PRIORITY);

        gen.start();
        integ.start();

        try {
            Thread.sleep(50);
            gen.interrupt();
            integ.interrupt();

            gen.join();
            integ.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Поточная проверка с семафором завершена.");
    }
    public static void simpleThreads() {
        Task task = new Task();
        task.setCount(100);

        Thread generatorThread = new Thread(new SimpleGenerator(task));
        Thread integratorThread = new Thread(new SimpleIntegrator(task));

        generatorThread.setPriority(Thread.MAX_PRIORITY);
        integratorThread.setPriority(Thread.MIN_PRIORITY);

        generatorThread.start();
        integratorThread.start();

        try {
            generatorThread.join();
            integratorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Поточная проверка завершена.");
    }

    public static void nonThread() {
        Random rnd = new Random();
        Task task = new Task();
        task.setCount(100);

        for (int i = 0; i < task.getCount(); i++) {
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

            try {
                double result = Functions.integrate(
                        task.getFunction(), task.getLeftX(), task.getRightX(), task.getStep()
                );
                System.out.printf("Result %.4f %.4f %.4f %.8f%n", left, right, step, result);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

}
