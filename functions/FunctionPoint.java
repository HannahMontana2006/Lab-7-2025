package functions;
import java.io.Serializable;

public class FunctionPoint implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final double EPSILON = 1e-9;
    private double x;
    private double y;
    public FunctionPoint(double x, double y){
        this.x=x;
        this.y=y;
    }
    public FunctionPoint(FunctionPoint point){
        this.x=point.x;
        this.y=point.y;
    }
    public FunctionPoint(){
        this.x=0;
        this.y=0;
    }
    public double getX(){
        return x;
    }
    public void setX(double x){
        this.x =x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y){
        this.y=y;
    }

    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionPoint point = (FunctionPoint) o;

        return Math.abs(point.x - x) < EPSILON &&
                Math.abs(point.y - y) < EPSILON;
    }

    public int hashCode() {

        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);

        int xLow = (int) (xBits & 0xffffffffL);
        int xHigh = (int) (xBits >> 32);

        int yLow = (int) (yBits & 0xffffffffL);
        int yHigh = (int) (yBits >> 32);

        return xLow ^ xHigh ^ yLow ^ yHigh;
    }

    public Object clone() {
        return new FunctionPoint(this);
    }

}
