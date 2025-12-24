package functions;

import functions.meta.*;

public final class Functions {

    private Functions() {
        throw new UnsupportedOperationException("Нельзя создавать экземпляры класса Functions");
    }

    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }
    public static double integrate(Function f, double left, double right, double step) {
        if (left < f.getLeftDomainBorder() || right > f.getRightDomainBorder()) {
            throw new IllegalArgumentException("Интервал интегрирования выходит за границы функции");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("Шаг дискретизации должен быть положительным");
        }

        double integral = 0.0;
        double x = left;

        while (x < right) {
            double nextX = Math.min(x + step, right);
            double y1 = f.getFunctionValue(x);
            double y2 = f.getFunctionValue(nextX);

            integral += (y1 + y2) * (nextX - x) / 2.0;
            x = nextX;
        }

        return integral;
    }


}
