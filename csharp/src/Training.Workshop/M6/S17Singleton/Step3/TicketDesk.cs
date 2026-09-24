using Training.Workshop.Shared;

namespace Training.Workshop.M6.S17Singleton.Step3;

/// <summary>
/// Krok 3: jawne wstrzyknięcie. Konstruktor bezargumentowy zachowuje dotychczasowe zachowanie
/// (Instance), a test lub inny cennik (np. promocyjny) nie wymaga globalnego stanu.
/// </summary>
public sealed class TicketDesk
{
    private readonly ITariff _tariff;

    public TicketDesk()
        : this(PriceList.Instance)
    {
    }

    public TicketDesk(ITariff tariff)
    {
        ArgumentNullException.ThrowIfNull(tariff);
        _tariff = tariff;
    }

    public Money Quote(string format, bool online)
    {
        var price = _tariff.BasePrice(format);
        return online ? price.Plus(Money.Of("2.00")) : price;
    }
}
