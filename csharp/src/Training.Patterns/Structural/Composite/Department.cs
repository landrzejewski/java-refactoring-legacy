namespace Training.Patterns.Structural.Composite;

public class Department : Node
{
    public Department(string name) : base(name)
    {
    }

    public override void PrintInfo()
    {
        Console.WriteLine("Department: " + name);
        children.ForEach(node => node.PrintInfo());
    }
}
