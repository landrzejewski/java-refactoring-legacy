using System.Globalization;

namespace Training.Module2;

public sealed class OrderPlacementService
{
    private readonly IProductCatalog catalog;
    private readonly IPaymentGateway paymentGateway;
    private readonly IOrderRepository repository;
    private readonly IEventPublisher eventPublisher;

    public OrderPlacementService(
        IProductCatalog catalog,
        IPaymentGateway paymentGateway,
        IOrderRepository repository,
        IEventPublisher eventPublisher)
    {
        ArgumentNullException.ThrowIfNull(catalog);
        ArgumentNullException.ThrowIfNull(paymentGateway);
        ArgumentNullException.ThrowIfNull(repository);
        ArgumentNullException.ThrowIfNull(eventPublisher);
        this.catalog = catalog;
        this.paymentGateway = paymentGateway;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public PlacedOrder Place(
        string sku,
        int quantity,
        string paymentToken)
    {
        if (quantity <= 0)
        {
            throw new ArgumentException("Quantity must be positive", nameof(quantity));
        }

        decimal total = Math.Round(
            catalog.PriceFor(sku) * quantity,
            2,
            MidpointRounding.AwayFromZero);
        string authorizationId = paymentGateway.Charge(paymentToken, total);
        long orderId = repository.Save(
            new OrderDraft(sku, quantity, total, authorizationId));

        eventPublisher.Publish(new OrderPlaced(orderId, total));
        return new PlacedOrder(orderId, total, authorizationId);
    }

    public interface IProductCatalog
    {
        decimal PriceFor(string sku);
    }

    public interface IPaymentGateway
    {
        string Charge(string paymentToken, decimal amount);
    }

    public interface IOrderRepository
    {
        long Save(OrderDraft order);
    }

    public interface IEventPublisher
    {
        void Publish(OrderPlaced @event);
    }

    public sealed record OrderDraft(
        string Sku,
        int Quantity,
        decimal Total,
        string AuthorizationId);

    public sealed record OrderPlaced(long OrderId, decimal Total)
    {
        public override string ToString() => string.Format(
            CultureInfo.InvariantCulture,
            "OrderPlaced[orderId={0}, total={1:0.00}]",
            OrderId,
            Total);
    }

    public sealed record PlacedOrder(
        long OrderId,
        decimal Total,
        string AuthorizationId)
    {
        public override string ToString() => string.Format(
            CultureInfo.InvariantCulture,
            "PlacedOrder[orderId={0}, total={1:0.00}, authorizationId={2}]",
            OrderId,
            Total,
            AuthorizationId);
    }
}
