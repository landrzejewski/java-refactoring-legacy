namespace Training.Module4.Behavioral.Interpreter;

public interface IExpression
{
    double Evaluate(IReadOnlyDictionary<string, double> context);
}
