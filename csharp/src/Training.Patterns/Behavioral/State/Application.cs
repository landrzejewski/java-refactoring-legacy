namespace Training.Patterns.Behavioral.State;

public class Application
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(Application));

    public static void Run()
    {
        var order = new Order();
        log.Info("State: " + order.State);
        order.Pay();
        log.Info("State: " + order.State);
        order.Ship();
        log.Info("State: " + order.State);
        try
        {
            order.Cancel();
        }
        catch (InvalidOperationException exception)
        {
            log.Info(exception.Message);
        }
    }
}
