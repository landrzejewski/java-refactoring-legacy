using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Start;

/// <summary>
/// Start: wspólna nadklasa biletów istnieje, ale nie wie nic o cenie ani etykiecie.
/// Każda podklasa ma własne <c>Price()</c> i własne <c>Label()</c> - trzy teksty, jedno zachowanie.
/// </summary>
public abstract class Ticket
{
    protected Ticket(string title, Money basePrice)
    {
        ArgumentNullException.ThrowIfNull(title);
        ArgumentNullException.ThrowIfNull(basePrice);
        Title = title;
        BasePrice = basePrice;
    }

    public string Title { get; }

    public Money BasePrice { get; }
}
