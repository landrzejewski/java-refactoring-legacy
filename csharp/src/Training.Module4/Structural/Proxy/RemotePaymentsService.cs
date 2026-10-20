namespace Training.Module4.Structural.Proxy;

public class RemotePaymentsService : IPaymentsService
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(RemotePaymentsService));

    public void Pay(IReadOnlyDictionary<string, string> properties)
    {
        log.Info("Payment started");
    }
}
