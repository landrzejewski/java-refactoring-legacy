namespace Training.Module3.Domain;

public sealed record DeliveryQuote
{
    public DeliveryQuote(
        string customerEmail,
        ShippingMethod method,
        Parcel parcel,
        decimal price)
    {
        ArgumentNullException.ThrowIfNull(customerEmail);
        ArgumentNullException.ThrowIfNull(parcel);

        if (string.IsNullOrWhiteSpace(customerEmail))
        {
            throw new ArgumentException("Customer email must not be blank", nameof(customerEmail));
        }
        if (price < 0m)
        {
            throw new ArgumentException("Price must not be negative", nameof(price));
        }

        CustomerEmail = customerEmail;
        Method = method;
        Parcel = parcel;
        Price = ScaleTwoWithoutRounding(price);
    }

    public string CustomerEmail { get; }

    public ShippingMethod Method { get; }

    public Parcel Parcel { get; }

    public decimal Price { get; }

    // Equivalent of BigDecimal.setScale(2, RoundingMode.UNNECESSARY):
    // widens the scale to two digits and refuses to silently drop precision.
    private static decimal ScaleTwoWithoutRounding(decimal price)
    {
        decimal scaled = decimal.Round(price * 1.00m, 2);
        if (scaled != price)
        {
            throw new ArithmeticException("Rounding necessary");
        }
        return scaled;
    }
}
