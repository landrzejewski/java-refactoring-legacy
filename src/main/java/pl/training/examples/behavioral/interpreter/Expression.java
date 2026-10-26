package pl.training.examples.behavioral.interpreter;

import java.util.Map;

public interface Expression {

    double evaluate(Map<String,Double> context);

}
