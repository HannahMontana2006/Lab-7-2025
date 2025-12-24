package functions;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Writer;
import java.io.Reader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TabulatedFunctions {

    private TabulatedFunctions() {
        throw new UnsupportedOperationException("Класс вспомогательных методов, создание объекта запрещено");
    }

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Число точек должно быть >= 2");
        }

        double step = (rightX - leftX) / (pointsCount - 1);
        double[] values = new double[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + step * i;
            values[i] = function.getFunctionValue(x);
        }

        return createTabulatedFunction(leftX, rightX, values);
    }

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            int count = function.getPointsCount();
            dos.writeInt(count);
            for (int i = 0; i < count; i++) {
                dos.writeDouble(function.getPointX(i));
                dos.writeDouble(function.getPointY(i));
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи в поток", e);
        }
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) {
        try (DataInputStream dis = new DataInputStream(in)) {
            int count = dis.readInt();
            double[] xValues = new double[count];
            double[] yValues = new double[count];
            for (int i = 0; i < count; i++) {
                xValues[i] = dis.readDouble();
                yValues[i] = dis.readDouble();
            }
            return createTabulatedFunction(xValues[0], xValues[count - 1], yValues);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении из потока", e);
        }
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        try (PrintWriter pw = new PrintWriter(out)) {
            int count = function.getPointsCount();
            pw.println(count);
            for (int i = 0; i < count; i++) {
                pw.println(function.getPointX(i) + " " + function.getPointY(i));
            }
            pw.flush();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при записи в символьный поток", e);
        }
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) {
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        try {
            tokenizer.nextToken();
            int count = (int) tokenizer.nval;
            double[] xValues = new double[count];
            double[] yValues = new double[count];

            for (int i = 0; i < count; i++) {
                tokenizer.nextToken();
                xValues[i] = tokenizer.nval;
                tokenizer.nextToken();
                yValues[i] = tokenizer.nval;
            }

            return createTabulatedFunction(xValues[0], xValues[count - 1], yValues);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении из символьного потока", e);
        }
    }

    private static TabulatedFunctionFactory factory = new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }

    public static TabulatedFunction createTabulatedFunction(
            double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }

    public static TabulatedFunction createTabulatedFunction(
            double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }

    public static TabulatedFunction createTabulatedFunction(
            FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }

    public static <T extends TabulatedFunction> T createTabulatedFunction(Class<T> clazz, double leftX, double rightX, int pointsCount) {
        try {
            Constructor<T> constructor = clazz.getConstructor(double.class, double.class, int.class);
            return constructor.newInstance(leftX, rightX, pointsCount);
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T extends TabulatedFunction> T createTabulatedFunction(Class<T> clazz, double leftX, double rightX, double[] values) {
        try {
            Constructor<T> constructor = clazz.getConstructor(double.class, double.class, double[].class);
            return constructor.newInstance(leftX, rightX, values);
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T extends TabulatedFunction> T createTabulatedFunction(Class<T> clazz, FunctionPoint[] points) {
        try {
            Constructor<T> constructor = clazz.getConstructor(FunctionPoint[].class);
            return constructor.newInstance((Object) points);
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public static <T extends TabulatedFunction> T tabulate(Class<T> clazz,
                                                           Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Число точек должно быть >= 2");
        }

        double step = (rightX - leftX) / (pointsCount - 1);
        double[] values = new double[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + step * i;
            values[i] = function.getFunctionValue(x);
        }

        return createTabulatedFunction(clazz, leftX, rightX, values);
    }
}