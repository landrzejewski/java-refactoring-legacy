namespace Training.Module4.Behavioral.Visitor;

public class Employee : Node
{
    public Employee(string name) : base(name)
    {
    }

    public override void AddChild(Node node) => throw new NotSupportedException();

    public override void Accept(IVisitor visitor)
    {
        visitor.Visit(this);
    }
}
