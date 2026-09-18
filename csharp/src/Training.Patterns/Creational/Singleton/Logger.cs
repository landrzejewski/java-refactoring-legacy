namespace Training.Patterns.Creational.Singleton;

public class Logger
{
    private static readonly Lock InstanceLock = new();
    private static Logger? LOGGER;

    private Logger()
    {
    }

    // Java: synchronized static method -> lock on a dedicated object
    public static Logger GetInstance()
    {
        lock (InstanceLock)
        {
            LOGGER ??= new Logger();
            return LOGGER;
        }
    }

    public void Log(string message)
    {
        Console.WriteLine("Info: " + message);
    }
}
