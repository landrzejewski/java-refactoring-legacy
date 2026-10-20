namespace Training.Module4.Behavioral.Interpreter;

public class Plus : IBiOperator
{
    public double Apply(double a, double b) => a + b;
}
