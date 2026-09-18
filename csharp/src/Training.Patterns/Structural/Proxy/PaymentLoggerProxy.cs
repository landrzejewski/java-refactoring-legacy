namespace Training.Patterns.Structural.Proxy;

public class PaymentLoggerProxy : IPaymentsService
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(PaymentLoggerProxy));

    private readonly IPaymentsService paymentsService;

    public void Pay(IReadOnlyDictionary<string, string> properties)
    {
        paymentsService.Pay(properties);
        log.Info("Payment completed");
    }

    public PaymentLoggerProxy(IPaymentsService paymentsService)
    {
        this.paymentsService = paymentsService;
    }
}
