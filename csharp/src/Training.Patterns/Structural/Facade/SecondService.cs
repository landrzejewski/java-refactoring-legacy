namespace Training.Patterns.Structural.Facade;

public class SecondService
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(SecondService));

    public void Run()
    {
        log.Info("Step 2");
    }
}
