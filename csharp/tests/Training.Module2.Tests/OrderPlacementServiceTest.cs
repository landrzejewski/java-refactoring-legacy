using static Training.Module2.OrderPlacementService;

namespace Training.Module2.Tests;

public sealed class OrderPlacementServiceTest
{
    [Fact]
    public void PlacesOrderUsingStubFakeAndSpy()
    {
        IProductCatalog catalogStub = new ProductCatalogStub(12.50m);
        IPaymentGateway paymentStub = new PaymentGatewayStub("AUTH-7");
        InMemoryOrderRepository repositoryFake = new();
        RecordingEventPublisher publisherSpy = new();
        OrderPlacementService service = new(
            catalogStub,
            paymentStub,
            repositoryFake,
            publisherSpy);

        PlacedOrder result = service.Place("BOOK", 2, "TOKEN-1");

        Assert.Equal(25.00m, result.Total);
        Assert.Equal("AUTH-7", result.AuthorizationId);
        Assert.Equal(
            new OrderDraft("BOOK", 2, 25.00m, "AUTH-7"),
            repositoryFake.Find(result.OrderId));
        Assert.Equal(
            [new OrderPlaced(result.OrderId, 25.00m)],
            publisherSpy.PublishedEvents());
    }

    [Fact]
    public void VerifiesPaymentProtocolUsingMock()
    {
        IProductCatalog catalogStub = new ProductCatalogStub(40.00m);
        ExpectingPaymentGateway paymentMock = new(
            "TOKEN-2",
            120.00m,
            "AUTH-9");
        InMemoryOrderRepository repositoryFake = new();
        IEventPublisher publisherStub = new IgnoringEventPublisher();
        OrderPlacementService service = new(
            catalogStub,
            paymentMock,
            repositoryFake,
            publisherStub);

        service.Place("COURSE", 3, "TOKEN-2");

        paymentMock.Verify();
    }

    private sealed class ProductCatalogStub(decimal price) : IProductCatalog
    {
        public decimal PriceFor(string sku) => price;
    }

    private sealed class PaymentGatewayStub(string authorizationId) : IPaymentGateway
    {
        public string Charge(string paymentToken, decimal amount) => authorizationId;
    }

    private sealed class IgnoringEventPublisher : IEventPublisher
    {
        public void Publish(OrderPlaced @event)
        {
        }
    }

    private sealed class InMemoryOrderRepository : IOrderRepository
    {
        private readonly Dictionary<long, OrderDraft> orders = [];
        private long nextId = 1;

        public long Save(OrderDraft order)
        {
            long id = nextId++;
            orders[id] = order;
            return id;
        }

        public OrderDraft? Find(long orderId) =>
            orders.GetValueOrDefault(orderId);
    }

    private sealed class RecordingEventPublisher : IEventPublisher
    {
        private readonly List<OrderPlaced> events = [];

        public void Publish(OrderPlaced @event) => events.Add(@event);

        public IReadOnlyList<OrderPlaced> PublishedEvents() => [.. events];
    }

    private sealed class ExpectingPaymentGateway(
        string expectedToken,
        decimal expectedAmount,
        string authorizationId) : IPaymentGateway
    {
        private int calls;

        public string Charge(string paymentToken, decimal amount)
        {
            Assert.Equal(expectedToken, paymentToken);
            Assert.Equal(expectedAmount, amount);
            calls++;
            return authorizationId;
        }

        public void Verify()
        {
            Assert.Equal(1, calls);
        }
    }
}
