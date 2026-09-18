namespace Training.Patterns.Structural.Proxy;

public class PaymentSecurityProxy : IPaymentsService
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(PaymentSecurityProxy));

    private readonly IPaymentsService paymentsService;

    public void Pay(IReadOnlyDictionary<string, string> properties)
    {
        log.Info("Checking security");
        paymentsService.Pay(properties);
    }

    public PaymentSecurityProxy(IPaymentsService paymentsService)
    {
        this.paymentsService = paymentsService;
    }
}
