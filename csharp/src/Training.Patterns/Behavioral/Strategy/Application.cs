namespace Training.Patterns.Behavioral.Strategy;

public class Application
{
    public static void Run()
    {
        new Service(new UuidGenerator()).Run();
        new Service(new IncrementalIdGenerator()).Run();
    }
}
