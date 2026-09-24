using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step1;

/// <summary>
/// Krok 1: bez zmian - nadklasa nadal nie zna ceny ani etykiety.
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
