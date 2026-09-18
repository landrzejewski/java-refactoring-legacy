using System.Globalization;

namespace Training.Module1;

public static class Module1Examples
{
    public static void Main(string[] args)
    {
        RunLegacyOrderService();
        RunRiskClassifier();
        RunDiscountPolicy();
        RunSalesCalculations();
    }

    private static void RunLegacyOrderService()
    {
        IOrderRepository repository = new ConsoleOrderRepository();
        IMailGateway mailGateway = new ConsoleMailGateway();

        LegacyOrderService service = new(repository, mailGateway);
        Order order = new(
            Guid.Parse("9aa026a4-fc39-4af8-a008-d9b831b0ba59"),
            "customer@example.com",
            [new OrderLine("BOOK-1", 2, 100.00m)]);

        Receipt receipt = service.PlaceOrder(order, "VIP", false, "PL");
        Console.WriteLine("Receipt: " + receipt);
    }

    private static void RunRiskClassifier()
    {
        OrderSummary order = new(
            1500.00m,
            true,
            [new Item(true), new Item(false)]);

        Console.WriteLine("Risk level: " + RiskClassifier.RiskLevel(order));
    }

    private static void RunDiscountPolicy()
    {
        int discount = DiscountPolicy.DiscountPercent(100, true);
        Console.WriteLine("Discount: " + discount + "%");
    }

    private static void RunSalesCalculations()
    {
        decimal price = 100.00m;
        decimal invoice = SalesCalculations.InvoiceLineTotal(price, 1, true);
        decimal quote = SalesCalculations.QuoteLineTotal(price, 1, true);

        Console.WriteLine("Invoice line total: " + invoice.ToString(CultureInfo.InvariantCulture));
        Console.WriteLine("Quote line total: " + quote.ToString(CultureInfo.InvariantCulture));
    }

    private sealed class ConsoleOrderRepository : IOrderRepository
    {
        public void Save(Guid orderId, decimal total) =>
            Console.WriteLine(string.Format(
                CultureInfo.InvariantCulture,
                "Saved order {0} with total {1}",
                orderId,
                total));
    }

    private sealed class ConsoleMailGateway : IMailGateway
    {
        public void Send(string recipient, string body) =>
            Console.WriteLine($"Sent to {recipient}: {body}");
    }
}
