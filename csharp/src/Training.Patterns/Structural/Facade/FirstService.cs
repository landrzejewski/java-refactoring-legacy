namespace Training.Patterns.Structural.Facade;

public class FirstService
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(FirstService));

    public void Run()
    {
        log.Info("Step 1");
    }
}
