namespace Training.Patterns.Behavioral.Visitor;

public class Application
{
    public static void Run()
    {
        var mainDepartment = new Department("Ma");
        var marketingDepartment = new Department("Marketing Department");
        var itDepartment = new Department("IT Department");
        mainDepartment.AddChild(marketingDepartment);
        mainDepartment.AddChild(itDepartment);
        itDepartment.AddChild(new Employee("Kowalski"));
        marketingDepartment.AddChild(new Employee("Nowak"));
        itDepartment.AddChild(new Employee("Karlsson"));
        //----------------------------------------------------------
        mainDepartment.Accept(new NameValidator());
    }
}
