import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;
import functions.threads.*;

public class Main {

    public static void main(String[] args) {


        TabulatedFunction arr = new ArrayTabulatedFunction(0, 2, new double[]{0, 1, 4});
        for (FunctionPoint p : arr) System.out.println(p);

        TabulatedFunction list = new LinkedListTabulatedFunction(0, 2, new double[]{0, 1, 4});
        for (FunctionPoint p : list) System.out.println(p);


        Function cos = new Cos();
        TabulatedFunction tf = TabulatedFunctions.tabulate(cos, 0, Math.PI, 5);
        System.out.println(tf.getClass());

        TabulatedFunctions.setTabulatedFunctionFactory(new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(cos, 0, Math.PI, 5);
        System.out.println(tf.getClass());


        TabulatedFunction rf = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println(rf.getClass());
    }


}
