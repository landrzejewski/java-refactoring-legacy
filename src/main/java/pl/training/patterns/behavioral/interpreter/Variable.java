package pl.training.patterns.behavioral.interpreter;

import java.util.Map;

public class Variable implements Expression {
    private final String name;

    @Override
    public double evaluate(Map<String, Double> context) {
        return context.get(name);
    }

    public Variable(final String name) {
        this.name = name;
    }
}
