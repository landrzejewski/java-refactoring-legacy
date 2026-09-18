namespace Training.Patterns.Behavioral.Visitor;

public class SuperDepartment : Department
{
    public SuperDepartment(string name) : base(name)
    {
    }

    public override void Accept(IVisitor visitor)
    {
        visitor.Visit(this);
        children.ForEach(node => node.Accept(visitor));
    }
}
