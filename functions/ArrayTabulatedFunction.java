package functions;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable {

    private static final double EPSILON = 1e-10;

    private FunctionPoint[] point;
    private int pointsCount;

    public ArrayTabulatedFunction() {
    }


    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("левая граница больше или равна правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("число точек меньше 2");
        }

        this.pointsCount = pointsCount;
        point = new FunctionPoint[pointsCount];

        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + step * i;
            point[i] = new FunctionPoint(x, 0);
        }
    }


    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("левая граница больше или равна правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("число точек меньше 2");
        }

        this.pointsCount = values.length;
        point = new FunctionPoint[pointsCount];

        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + step * i;
            point[i] = new FunctionPoint(x, values[i]);
        }
    }


    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2)
            throw new IllegalArgumentException("Количество точек должно быть ≥ 2");

        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i - 1].getX())
                throw new IllegalArgumentException("Точки должны быть упорядочены по X");
        }

        this.point = new FunctionPoint[points.length];
        for (int i = 0; i < points.length; i++) {
            this.point[i] = new FunctionPoint(points[i]);
        }

        this.pointsCount = points.length;
    }


    public double getLeftDomainBorder() {
        return point[0].getX();
    }


    public double getRightDomainBorder() {
        return point[pointsCount - 1].getX();
    }


    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder())
            return Double.NaN;

        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = point[i].getX();
            double y1 = point[i].getY();
            double x2 = point[i + 1].getX();
            double y2 = point[i + 1].getY();

            if (Math.abs(x - x1) < EPSILON) return y1;
            if (Math.abs(x - x2) < EPSILON) return y2;

            if (x > x1 && x < x2) {
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }

        return Double.NaN;
    }


    public int getPointsCount() {
        return pointsCount;
    }


    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException("неверный индекс");

        return new FunctionPoint(point[index]);
    }


    public double getPointX(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException("неверный индекс");

        return point[index].getX();
    }


    public double getPointY(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException("неверный индекс");

        return point[index].getY();
    }


    public void setPoint(int index, FunctionPoint p) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException("неверный индекс");

        double x = p.getX();

        if ((index > 0 && x <= point[index - 1].getX() + EPSILON)
                || (index < pointsCount - 1 && x >= point[index + 1].getX() - EPSILON)) {
            throw new InappropriateFunctionPointException("новая точка нарушает порядок X");
        }

        point[index] = new FunctionPoint(p);
    }


    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException("неверный индекс");

        if ((index > 0 && x <= point[index - 1].getX() + EPSILON)
                || (index < pointsCount - 1 && x >= point[index + 1].getX() - EPSILON)) {
            throw new InappropriateFunctionPointException("новый x нарушает порядок точек");
        }

        point[index].setX(x);
    }


    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException("неверный индекс");

        point[index].setY(y);
    }


    public void deletePoint(int index) {
        if (pointsCount <= 2) {
            throw new IllegalStateException("Нельзя удалить точку — останется меньше 2 точек");
        }

        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Неверный индекс");
        }

        FunctionPoint[] newPoint = new FunctionPoint[pointsCount - 1];
        System.arraycopy(point, 0, newPoint, 0, index);
        System.arraycopy(point, index + 1, newPoint, index, pointsCount - index - 1);

        point = newPoint;
        pointsCount--;
    }


    public void addPoint(FunctionPoint p) throws InappropriateFunctionPointException {
        double x = p.getX();

        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(point[i].getX() - x) < EPSILON)
                throw new InappropriateFunctionPointException("точка с таким x уже существует");
        }

        FunctionPoint[] newPoints = new FunctionPoint[pointsCount + 1];
        int i = 0;

        while (i < pointsCount && point[i].getX() < x) {
            newPoints[i] = point[i];
            i++;
        }

        newPoints[i] = new FunctionPoint(p);

        for (int j = i; j < pointsCount; j++) {
            newPoints[j + 1] = point[j];
        }

        point = newPoints;
        pointsCount++;
    }



    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);

        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(point[i].getX());
            out.writeDouble(point[i].getY());
        }
    }


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int size = in.readInt();

        if (size < 2) {
            throw new IOException("Недостаточно точек для создания функции");
        }

        FunctionPoint[] points = new FunctionPoint[size];

        for (int i = 0; i < size; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        initFromPoints(points);
    }


    private void initFromPoints(FunctionPoint[] points) {
        this.pointsCount = points.length;
        this.point = new FunctionPoint[points.length];

        for (int i = 0; i < points.length; i++) {
            this.point[i] = new FunctionPoint(points[i]);
        }
    }
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        for (int i = 0; i < pointsCount; i++) {
            sb.append(point[i].toString());
            if (i < pointsCount - 1)
                sb.append(", ");
        }

        sb.append("}");
        return sb.toString();
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction other = (ArrayTabulatedFunction) o;

            if (this.pointsCount != other.pointsCount) return false;

            for (int i = 0; i < pointsCount; i++) {
                if (!this.getPoint(i).equals(other.getPoint(i))) {
                    return false;
                }
            }
            return true;
        }

        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction other = (LinkedListTabulatedFunction) o;

            if (this.pointsCount != other.getPointsCount()) return false;

            for (int i = 0; i < pointsCount; i++) {
                if (!this.getPoint(i).equals(other.getPoint(i)))
                    return false;
            }
            return true;
        }

        return false;
    }
    public int hashCode() {

        int hash = pointsCount;

        for (int i = 0; i < pointsCount; i++) {
            hash ^= point[i].hashCode();
        }

        return hash;
    }
    public Object clone() {

        FunctionPoint[] newPoints = new FunctionPoint[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            newPoints[i] = (FunctionPoint) point[i].clone();
        }

        return new ArrayTabulatedFunction(newPoints);
    }
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private int index = 0;

            public boolean hasNext() {
                return index < pointsCount;
            }

            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                FunctionPoint p = point[index++];
                return new FunctionPoint(p.getX(), p.getY());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    public static class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {

        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
        }

        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new ArrayTabulatedFunction(leftX, rightX, values);
        }

        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new ArrayTabulatedFunction(points);
        }
    }



}
