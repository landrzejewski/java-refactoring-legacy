namespace Training.Patterns.Behavioral.ChainOfResponsibility;

public class Processor : Handler
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Processor));

    public override void HandleRequest(string request)
    {
        log.Info("Processing: " + request);
    }
}
