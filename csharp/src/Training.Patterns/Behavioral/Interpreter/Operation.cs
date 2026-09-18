namespace Training.Patterns.Behavioral.Interpreter;

public class Operation : IExpression
{
    private readonly IExpression left;
    private readonly IExpression right;
    private readonly IBiOperator @operator;

    public double Evaluate(IReadOnlyDictionary<string, double> context) =>
        @operator.Apply(left.Evaluate(context), right.Evaluate(context));

    public Operation(IExpression left, IExpression right, IBiOperator @operator)
    {
        this.left = left;
        this.right = right;
        this.@operator = @operator;
    }
}
