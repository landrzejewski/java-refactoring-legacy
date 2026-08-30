package pl.training.patterns.behavioral.interpreter;

import java.util.Map;

public class Literal implements Expression {
    private final double value;

    @Override
    public double evaluate(Map<String, Double> context) {
        return value;
    }

    public Literal(final double value) {
        this.value = value;
    }
}
