namespace Training.Patterns.Behavioral.Visitor;

public class Reporter : IVisitor
{
    public void Visit(Department department)
    {
        Console.WriteLine("Department: " + department.name);
    }

    public void Visit(Employee employee)
    {
        Console.WriteLine(" - Employee " + employee.name);
    }
}
