namespace Training.Patterns.Behavioral.Interpreter;

public class Literal : IExpression
{
    private readonly double value;

    public double Evaluate(IReadOnlyDictionary<string, double> context) => value;

    public Literal(double value)
    {
        this.value = value;
    }
}
