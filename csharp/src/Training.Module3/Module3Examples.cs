using System.Globalization;
using Training.Module3.BreakDependencies.After;
using Training.Module3.Incremental;
using static Training.Module3.OrderPlacementService;

namespace Training.Module3;

public static class Module3Examples
{
    public static void Main(string[] args)
    {
        RunBehaviorPreservingRefactoring();
        RunTestDoubleExample();
        RunExplicitSeamExample();
        RunBreakDependenciesExample();
        RunParallelVerificationExample();
    }

    private static void RunBehaviorPreservingRefactoring()
    {
        IReadOnlyList<InvoiceLine> lines =
        [
            new InvoiceLine("BOOK", 2, 19.99m),
            new InvoiceLine("PEN", 1, 5.00m),
        ];
        string before = new LegacyInvoiceFormatter().Format("Acme", lines);
        string after = new InvoiceFormatter().Format("Acme", lines);

        Console.WriteLine("Formatter outputs equal: " + (before == after).ToString().ToLowerInvariant());
    }

    private static void RunTestDoubleExample()
    {
        OrderPlacementService service = new(
            new FixedPriceCatalog(12.50m),
            new FixedAuthorizationGateway("AUTH-DEMO"),
            new FixedIdOrderRepository(1L),
            new ConsoleEventPublisher());

        PlacedOrder order = service.Place("BOOK", 2, "TOKEN-DEMO");
        Console.WriteLine("Placed order: " + order);
    }

    private static void RunExplicitSeamExample()
    {
        TimeProvider clock = new FixedTimeProvider(
            DateTimeOffset.Parse("2026-08-30T10:00:00Z", CultureInfo.InvariantCulture));
        ReminderService service = new(clock, new ConsoleReminderGateway());

        service.SendRenewalReminder(new Subscription(
            "developer@example.com",
            new DateOnly(2026, 9, 6)));
    }

    private static void RunBreakDependenciesExample()
    {
        var windowService = new DeploymentWindowService(
            new StandardMaintenanceWindows());
        // Java wypisuje stałe enum wielkimi literami (ALLOWED); w C# są PascalCase.
        Console.WriteLine("Break Dependencies: "
            + windowService.Schedule("payments", 2).ToString().ToUpperInvariant());
    }

    private static void RunParallelVerificationExample()
    {
        var events = new List<VerificationEvent>();
        var engine = new MigratingPricingEngine(
            new LegacyPricingEngineAdapter(),
            new CandidatePricingEngine(),
            IVerificationReporter.Of(events.Add),
            MigrationMode.Verify);

        var quote = engine.Quote(new PriceRequest(
            19.99m,
            3,
            0.10m));

        Console.WriteLine("Parallel verification: "
            + quote.NetAmount.ToString("0.00", CultureInfo.InvariantCulture)
            + "/" + events[0].GetType().Name);
    }

    private sealed class FixedPriceCatalog(decimal price) : IProductCatalog
    {
        public decimal PriceFor(string sku) => price;
    }

    private sealed class FixedAuthorizationGateway(string authorizationId) : IPaymentGateway
    {
        public string Charge(string paymentToken, decimal amount) => authorizationId;
    }

    private sealed class FixedIdOrderRepository(long id) : IOrderRepository
    {
        public long Save(OrderDraft order) => id;
    }

    private sealed class ConsoleEventPublisher : IEventPublisher
    {
        public void Publish(OrderPlaced @event) =>
            Console.WriteLine("Published event: " + @event);
    }

    private sealed class ConsoleReminderGateway : ReminderService.IReminderGateway
    {
        public void Send(string email, DateOnly renewalDate) =>
            Console.WriteLine(string.Format(
                CultureInfo.InvariantCulture,
                "Reminder: {0} renews on {1:yyyy-MM-dd}",
                email,
                renewalDate));
    }

    // Equivalent of Java's Clock.fixed(instant, ZoneOffset.UTC).
    private sealed class FixedTimeProvider(DateTimeOffset now) : TimeProvider
    {
        public override DateTimeOffset GetUtcNow() => now;

        public override TimeZoneInfo LocalTimeZone => TimeZoneInfo.Utc;
    }
}
