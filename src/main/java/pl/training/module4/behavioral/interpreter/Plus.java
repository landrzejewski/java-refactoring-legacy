package pl.training.module4.behavioral.interpreter;

public class Plus implements BiOperator {

    @Override
    public double apply(double a, double b) {
        return a + b;
    }

}
