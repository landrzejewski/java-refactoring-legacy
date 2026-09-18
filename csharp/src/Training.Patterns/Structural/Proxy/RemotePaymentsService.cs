namespace Training.Patterns.Structural.Proxy;

public class RemotePaymentsService : IPaymentsService
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(RemotePaymentsService));

    public void Pay(IReadOnlyDictionary<string, string> properties)
    {
        log.Info("Payment started");
    }
}
