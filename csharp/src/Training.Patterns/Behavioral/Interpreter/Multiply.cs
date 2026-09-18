namespace Training.Patterns.Behavioral.Interpreter;

public class Multiply : IBiOperator
{
    public double Apply(double a, double b) => a * b;
}
