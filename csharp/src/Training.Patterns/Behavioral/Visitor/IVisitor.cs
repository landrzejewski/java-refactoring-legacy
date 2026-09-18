namespace Training.Patterns.Behavioral.Visitor;

public interface IVisitor
{
    void Visit(Department department)
    {
    }

    void Visit(SuperDepartment department)
    {
    }

    void Visit(Employee employee)
    {
    }
}
