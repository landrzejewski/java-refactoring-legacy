namespace Training.Patterns.Behavioral.ChainOfResponsibility;

public class Application
{
    public static void Run()
    {
        var chain = new Validator(new Logger(new Processor()));
        chain.HandleRequest("Test");
    }
}
