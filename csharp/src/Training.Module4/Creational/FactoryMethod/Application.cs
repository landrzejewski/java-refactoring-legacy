namespace Training.Module4.Creational.FactoryMethod;

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
