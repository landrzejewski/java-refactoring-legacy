using Training.Workshop.Shared;

namespace Training.Workshop.M6.S17Singleton.Step1;

/// <summary>Krok 1: klient pyta o jedyną instancję zamiast robić new.</summary>
public sealed class TicketDesk
{
    public Money Quote(string format, bool online)
    {
        var price = PriceList.GetInstance().BasePrice(format);
        return online ? price.Plus(Money.Of("2.00")) : price;
    }
}
