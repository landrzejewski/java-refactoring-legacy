namespace Training.Patterns.Behavioral.Interpreter;

public interface IExpression
{
    double Evaluate(IReadOnlyDictionary<string, double> context);
}
