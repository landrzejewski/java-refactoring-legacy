namespace Training.Patterns.Structural.Proxy;

public class Application
{
    public static void Run()
    {
        var paymentsService = new PaymentLoggerProxy(new PaymentSecurityProxy(new RemotePaymentsService()));
        //----------------------------------------------------------------
        paymentsService.Pay(new Dictionary<string, string>());
    }
}
