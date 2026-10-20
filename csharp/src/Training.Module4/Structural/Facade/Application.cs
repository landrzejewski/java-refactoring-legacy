namespace Training.Module4.Structural.Facade;

public class Application
{
    public static void Run()
    {
        var library = new Facade(new FirstService(), new SecondService());
        //----------------------------------------------------------------
        library.Run();
    }
}
