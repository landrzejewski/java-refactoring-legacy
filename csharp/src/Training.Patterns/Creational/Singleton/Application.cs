namespace Training.Patterns.Creational.Singleton;

public class Application
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Application));

    public static void Run()
    {
        var logger = Logger.GetInstance();
        log.Info("Is same: " + JavaText.Of(logger.Equals(Logger.GetInstance())));
        logger.Log("Success");
    }
}
