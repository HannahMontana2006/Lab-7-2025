package functions;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 1L;
    private static final double EPSILON = 1e-10;

    private static class FunctionNode implements Serializable {
        private static final long serialVersionUID = 1L;
        FunctionPoint data;
        FunctionNode  next;
        FunctionNode prev;

        FunctionNode(FunctionPoint data) {
            this.data = data;
        }
    }

    private  FunctionNode head;
    private int pointsCount;
    private FunctionNode lastAccessedNode;
    private int lastAccessedIndex;

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница >= правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Число точек меньше 2");
        }

        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        this.pointsCount = 0;
        this.lastAccessedNode = head;
        this.lastAccessedIndex = -1;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            addPointInternal(new FunctionPoint(leftX + i * step, 0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница >= правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Число точек меньше 2");
        }

        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        this.pointsCount = 0;
        this.lastAccessedNode = head;
        this.lastAccessedIndex = -1;

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            addPointInternal(new FunctionPoint(leftX + step * i, values[i]));
        }
    }
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("Число точек меньше 2");
        }

        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i - 1].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию X");
            }
        }

        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;

        this.pointsCount = 0;
        this.lastAccessedNode = head;
        this.lastAccessedIndex = -1;

        for (FunctionPoint p : points) {
            addPointInternal(new FunctionPoint(p));
        }
    }
    private LinkedListTabulatedFunction() {
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        pointsCount = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }


    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс вне диапазона: " + index);
        }

        int fromHead = index;
        int fromTail = pointsCount - index - 1;
        int fromLast = lastAccessedIndex >= 0 ? Math.abs(index - lastAccessedIndex) : Integer.MAX_VALUE;

        FunctionNode current;
        int currentIndex;

        if (fromHead <= fromTail && fromHead <= fromLast) {
            current = head.next;
            currentIndex = 0;
            while (currentIndex < index) {
                current = current.next;
                currentIndex++;
            }
        } else if (fromTail < fromHead && fromTail <= fromLast) {
            current = head.prev;
            currentIndex = pointsCount - 1;
            while (currentIndex > index) {
                current = current.prev;
                currentIndex--;
            }
        } else {
            current = lastAccessedNode;
            currentIndex = lastAccessedIndex;
            while (currentIndex < index) {
                current = current.next;
                currentIndex++;
            }
            while (currentIndex > index) {
                current = current.prev;
                currentIndex--;
            }
        }

        lastAccessedNode = current;
        lastAccessedIndex = index;
        return current;
    }

    private void addPointInternal(FunctionPoint p) {
        FunctionNode node = new FunctionNode(new FunctionPoint(p));
        FunctionNode tail = head.prev;

        tail.next = node;
        node.prev = tail;
        node.next = head;
        head.prev = node;

        pointsCount++;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).data);
    }

    public void setPoint(int index, FunctionPoint p) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException("Индекс вне диапазона");

        double x = p.getX();

        if (index > 0 && x <= getNodeByIndex(index - 1).data.getX() + EPSILON)
            throw new InappropriateFunctionPointException("X меньше или равен предыдущему");
        if (index < pointsCount - 1 && x >= getNodeByIndex(index + 1).data.getX() - EPSILON)
            throw new InappropriateFunctionPointException("X больше или равен следующему");

        getNodeByIndex(index).data = new FunctionPoint(p);
    }

    public double getPointX(int index) {
        return getNodeByIndex(index).data.getX();
    }

    public double getPointY(int index) {
        return getNodeByIndex(index).data.getY();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        setPoint(index, new FunctionPoint(x, getPointY(index)));
    }

    public void setPointY(int index, double y) {
        getNodeByIndex(index).data.setY(y);
    }

    public void deletePoint(int index) {
        if (pointsCount <= 3)
            throw new IllegalStateException("Нельзя удалить точку — останется меньше 3 точек");

        FunctionNode node = getNodeByIndex(index);
        node.prev.next = node.next;
        node.next.prev = node.prev;
        pointsCount--;
    }

    public void addPoint(FunctionPoint p) throws InappropriateFunctionPointException {
        double x = p.getX();

        FunctionNode current = head.next;
        int index = 0;
        while (current != head && current.data.getX() < x - EPSILON) {
            current = current.next;
            index++;
        }

        if (current != head && Math.abs(current.data.getX() - x) < EPSILON)
            throw new InappropriateFunctionPointException("Точка с таким X уже существует");

        FunctionNode newNode = new FunctionNode(new FunctionPoint(p));
        FunctionNode prevNode = current.prev;

        prevNode.next = newNode;
        newNode.prev = prevNode;
        newNode.next = current;
        current.prev = newNode;
        pointsCount++;
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() - EPSILON || x > getRightDomainBorder() + EPSILON)
            return Double.NaN;

        FunctionNode current = head.next;
        while (current.next != head) {
            double x1 = current.data.getX();
            double y1 = current.data.getY();
            double x2 = current.next.data.getX();
            double y2 = current.next.data.getY();

            if (Math.abs(x - x1) < EPSILON) return y1;
            if (Math.abs(x - x2) < EPSILON) return y2;
            if (x > x1 && x < x2)
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);

            current = current.next;
        }

        return Double.NaN;
    }

    public double getLeftDomainBorder() {
        return pointsCount == 0 ? Double.NaN : head.next.data.getX();
    }

    public double getRightDomainBorder() {
        return pointsCount == 0 ? Double.NaN : head.prev.data.getX();
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        FunctionNode current = head.next;
        for (int i = 0; i < pointsCount; i++) {
            sb.append("(").append(current.data.getX()).append("; ").append(current.data.getY()).append(")");

            if (i < pointsCount - 1) sb.append(", ");
            current = current.next;
        }

        sb.append("}");
        return sb.toString();
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction other = (LinkedListTabulatedFunction) o;

            if (this.pointsCount != other.pointsCount) return false;

            FunctionNode a = this.head.next;
            FunctionNode b = other.head.next;

            while (a != this.head) {

                if (!a.data.equals(b.data)) return false;

                a = a.next;
                b = b.next;
            }

            return true;
        }
        if (o instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction other = (ArrayTabulatedFunction) o;

            if (this.pointsCount != other.getPointsCount()) return false;

            FunctionNode a = this.head.next;

            for (int i = 0; i < pointsCount; i++) {
                if (!a.data.equals(other.getPoint(i)))
                    return false;
                a = a.next;
            }

            return true;
        }

        return false;
    }

    public int hashCode() {
        int hash = pointsCount;

        FunctionNode current = head.next;

        for (int i = 0; i < pointsCount; i++) {
            long x = Double.doubleToLongBits(current.data.getX());
            long y = Double.doubleToLongBits(current.data.getY());

            hash ^= (int)(x ^ (x >>> 32));
            hash ^= (int)(y ^ (y >>> 32));

            current = current.next;
        }

        return hash;
    }
    public Object clone() {
        LinkedListTabulatedFunction clone = new LinkedListTabulatedFunction(); // пустой конструктор

        FunctionNode current = this.head.next;
        while (current != this.head) { // идём по оригинальному списку
            clone.addPointInternal(new FunctionPoint(current.data.getX(), current.data.getY()));
            current = current.next;
        }

        return clone;
    }
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {

            private FunctionNode current = head.next;

            public boolean hasNext() {
                return current != head;
            }

            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                FunctionPoint p = current.data;
                current = current.next;

                return new FunctionPoint(p.getX(), p.getY());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {

        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
        }

        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        }

        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new LinkedListTabulatedFunction(points);
        }
    }



}
