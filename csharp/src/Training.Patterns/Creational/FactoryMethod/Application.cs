namespace Training.Patterns.Creational.FactoryMethod;

public class Application
{
    public static void Run()
    {
        var idGeneratorFactory = new TestIdGeneratorFactory();
        var service = new Service(idGeneratorFactory);
        //------------------------------------------------------------------
        service.Run();
    }
}
