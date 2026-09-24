using Training.Workshop.Shared;

namespace Training.Workshop.M6.S17Singleton.Step2;

/// <summary>Krok 2: klient używa PriceList.Instance - wciąż ukryta, globalna zależność.</summary>
public sealed class TicketDesk
{
    public Money Quote(string format, bool online)
    {
        var price = PriceList.Instance.BasePrice(format);
        return online ? price.Plus(Money.Of("2.00")) : price;
    }
}
