namespace Training.Module4.Behavioral.Strategy;

public class Application
{
    public static void Run()
    {
        new Service(new UuidGenerator()).Run();
        new Service(new IncrementalIdGenerator()).Run();
    }
}
