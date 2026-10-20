namespace Training.Module4.Structural.Facade;

public class SecondService
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(SecondService));

    public void Run()
    {
        log.Info("Step 2");
    }
}
