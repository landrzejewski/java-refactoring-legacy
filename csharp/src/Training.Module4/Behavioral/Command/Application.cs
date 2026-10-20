namespace Training.Module4.Behavioral.Command;

public class Application
{
    public static void Run()
    {
        var invoker = new Invoker();
        invoker.Invoke(new PrintTime());
        invoker.Invoke(new ConnectToServer());
    }
}
