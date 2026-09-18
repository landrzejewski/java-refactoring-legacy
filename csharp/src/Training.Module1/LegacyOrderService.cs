using System.Globalization;

namespace Training.Module1;

public sealed class LegacyOrderService
{
    private readonly IOrderRepository repository;
    private readonly IMailGateway mailGateway;

    public LegacyOrderService(
        IOrderRepository repository,
        IMailGateway mailGateway)
    {
        this.repository = repository;
        this.mailGateway = mailGateway;
    }

    public Receipt PlaceOrder(
        Order? order,
        string customerType,
        bool express,
        string destinationCountry)
    {
        if (order == null || order.Lines == null || order.Lines.Count == 0)
        {
            throw new ArgumentException("Order must contain lines");
        }

        decimal subtotal = 0m;

        foreach (OrderLine line in order.Lines)
        {
            decimal lineValue = line.UnitPrice * line.Quantity;

            if ("VIP".Equals(customerType))
            {
                lineValue = lineValue * 0.90m;
            }

            if (line.Quantity >= 10)
            {
                lineValue = lineValue * 0.95m;
            }

            subtotal = subtotal + lineValue;
        }

        decimal shipping;
        if (express)
        {
            shipping = 39.99m;
        }
        else if (subtotal >= 200.00m)
        {
            shipping = 0m;
        }
        else
        {
            shipping = 14.99m;
        }

        decimal tax;
        if ("PL".Equals(destinationCountry))
        {
            tax = subtotal * 0.23m;
        }
        else if ("DE".Equals(destinationCountry))
        {
            tax = subtotal * 0.19m;
        }
        else
        {
            tax = 0m;
        }

        decimal total = Math.Round(
            subtotal + shipping + tax,
            2,
            MidpointRounding.AwayFromZero);

        repository.Save(order.Id, total);
        mailGateway.Send(
            order.CustomerEmail,
            "Order total: " + total.ToString(CultureInfo.InvariantCulture));

        return new Receipt(order.Id, total);
    }
}

public sealed record Order(
    Guid Id,
    string CustomerEmail,
    IReadOnlyList<OrderLine>? Lines);

public sealed record OrderLine(
    string Sku,
    int Quantity,
    decimal UnitPrice);

public sealed record Receipt(Guid OrderId, decimal Total)
{
    public override string ToString() => string.Format(
        CultureInfo.InvariantCulture,
        "Receipt[orderId={0}, total={1}]",
        OrderId,
        Total);
}

public interface IOrderRepository
{
    void Save(Guid orderId, decimal total);
}

public interface IMailGateway
{
    void Send(string recipient, string body);
}
