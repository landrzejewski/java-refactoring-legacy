namespace Training.Patterns.Behavioral.Interpreter;

public class Minus : IBiOperator
{
    public double Apply(double a, double b) => a - b;
}
