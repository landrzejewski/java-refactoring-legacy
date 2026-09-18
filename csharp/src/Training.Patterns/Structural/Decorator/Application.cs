namespace Training.Patterns.Structural.Decorator;

public class Application
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Application));

    public static void Run()
    {
        IReader reader = new LowerCaseReaderDecorator(new UnderscoreReaderDecorator(new SystemInReader()));
        //------------------------------------------------
        log.Info(reader.GetText());
    }
}
