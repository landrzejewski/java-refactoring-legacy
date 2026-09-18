namespace Training.Patterns.Behavioral.Interpreter;

public class Variable : IExpression
{
    private readonly string name;

    public double Evaluate(IReadOnlyDictionary<string, double> context) => context[name];

    public Variable(string name)
    {
        this.name = name;
    }
}
