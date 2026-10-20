package pl.training.module4.behavioral.interpreter;

import java.util.Map;

public interface Expression {

    double evaluate(Map<String,Double> context);

}
