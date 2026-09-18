namespace Training.Patterns.Behavioral.Visitor;

public class Department : Node
{
    public Department(string name) : base(name)
    {
    }

    public override void Accept(IVisitor visitor)
    {
        visitor.Visit(this);
        base.Accept(visitor);
    }
}
