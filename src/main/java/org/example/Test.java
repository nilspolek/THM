package org.example;
import com.sun.jdi.connect.Connector;

import java.util.function.*;

public class Test {
    public static void main(String[] args) {
        String w = "Hallo";
        IntPredicate a  = (IntPredicate) s-> s%2==0;
        System.out.println(a.test(10));
        var i = (ToIntFunction<Integer>) x-> x*x;
        System.out.println(i.applyAsInt(10));
    }
}
