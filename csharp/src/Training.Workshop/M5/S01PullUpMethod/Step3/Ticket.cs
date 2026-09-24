using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Pull Members Up dla <c>Label()</c> - trzy identyczne ciała stały się jedną metodą.
/// Niewirtualna (odpowiednik <c>final</c>), bo etykieta jest kontraktem wspólnym dla wszystkich biletów; wariantem jest tylko cena.
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

    public string Label()
    {
        return Title + ": " + Price();
    }
}
