package pl.training.patterns.behavioral.interpreter;

import java.util.HashMap;
import java.util.Map;

public class Application {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Application.class.getName());
    private static final Map<String, BiOperator> OPERATORS = new HashMap<>();

    private static BiOperator op(String name) {
        return OPERATORS.computeIfAbsent(name, Application::operatorFactory);
    }

    private static BiOperator operatorFactory(String name) {
        return switch (name) {
            case "+" -> new Plus();
            case "-" -> new Minus();
            case "*" -> new Multiply();
            default -> throw new IllegalArgumentException("Unknown operator: " + name);
        };
    }

    public static void main(String[] args) {
        // 2a * 3 + 1 gdzie a = 4
        var expression = new Operation(new Operation(new Operation(new Literal(2), new Variable("a"), op("*")), new Literal(3), op("*")), new Literal(1), op("+"));
        log.info("Result: " + expression.evaluate(Map.of("a", 4.0)));
    }
}
