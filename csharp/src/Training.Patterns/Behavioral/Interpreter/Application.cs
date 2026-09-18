namespace Training.Patterns.Behavioral.Interpreter;

public class Application
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Application));
    private static readonly Dictionary<string, IBiOperator> OPERATORS = [];

    private static IBiOperator Op(string name)
    {
        if (!OPERATORS.TryGetValue(name, out var @operator))
        {
            @operator = OperatorFactory(name);
            OPERATORS[name] = @operator;
        }
        return @operator;
    }

    private static IBiOperator OperatorFactory(string name) => name switch
    {
        "+" => new Plus(),
        "-" => new Minus(),
        "*" => new Multiply(),
        _ => throw new ArgumentException("Unknown operator: " + name),
    };

    public static void Run()
    {
        // 2a * 3 + 1 gdzie a = 4
        var expression = new Operation(new Operation(new Operation(new Literal(2), new Variable("a"), Op("*")), new Literal(3), Op("*")), new Literal(1), Op("+"));
        log.Info("Result: " + JavaText.Of(expression.Evaluate(new Dictionary<string, double> { ["a"] = 4.0 })));
    }
}
