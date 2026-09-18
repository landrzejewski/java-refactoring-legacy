namespace Training.Patterns.Behavioral.Visitor;

public class NameValidator : IVisitor
{
    public void Visit(Department department)
    {
        if (department.name.Length < 3)
        {
            Console.WriteLine("Department name too short: " + department.name);
        }
    }
}
