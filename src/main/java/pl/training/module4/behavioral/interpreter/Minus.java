package pl.training.module4.behavioral.interpreter;

public class Minus implements BiOperator {

    @Override
    public double apply(double a, double b) {
        return a - b;
    }

}
