using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step2;

/// <summary>
/// Krok 2: Pull Members Up z opcją "Make abstract" dla <c>Price()</c>.
/// Ciała zostają w podklasach (są różne), ale typ bazowy obiecuje cenę - to punkt rozszerzenia dla Label().
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

    public abstract Money Price();
}
