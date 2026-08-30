package pl.training.patterns.behavioral.interpreter;

import java.util.Map;

public class Operation implements Expression {
    private final Expression left;
    private final Expression right;
    private final BiOperator operator;

    @Override
    public double evaluate(Map<String, Double> context) {
        return operator.apply(left.evaluate(context), right.evaluate(context));
    }

    public Operation(final Expression left, final Expression right, final BiOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
}
