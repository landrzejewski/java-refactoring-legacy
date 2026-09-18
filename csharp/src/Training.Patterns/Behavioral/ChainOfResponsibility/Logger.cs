namespace Training.Patterns.Behavioral.ChainOfResponsibility;

public class Logger : Handler
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Logger));

    public Logger(Handler nextHandler)
    {
        this.nextHandler = nextHandler;
    }

    public override void HandleRequest(string request)
    {
        log.Info(request);
        nextHandler!.HandleRequest(request);
    }
}
