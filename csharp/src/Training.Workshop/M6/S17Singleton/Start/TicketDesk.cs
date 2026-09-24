using Training.Workshop.Shared;

namespace Training.Workshop.M6.S17Singleton.Start;

/// <summary>Start: każda wycena tworzy i parsuje nowy cennik.</summary>
public sealed class TicketDesk
{
    public Money Quote(string format, bool online)
    {
        var price = new PriceList().BasePrice(format);
        return online ? price.Plus(Money.Of("2.00")) : price;
    }
}
