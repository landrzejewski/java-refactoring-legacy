namespace Training.Module4.Structural.Composite;

public class Employee : Node
{
    public Employee(string name) : base(name)
    {
    }

    public override void AddChild(Node node) => throw new NotSupportedException();

    public override void PrintInfo()
    {
        Console.WriteLine(" - Employee " + name);
    }
}
