using Training.Workshop.M6.S04EncapsulateFactory.Start.Ticketing;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Start;

/// <summary>
/// Start: klient zna klasy konkretne biletów i regułę VIP (rząd 10+), tworzy je przez new
/// w dwóch miejscach. Nowy rodzaj biletu = zmiany u każdego klienta.
/// </summary>
public sealed class BoxOffice
{
    public ITicket Sell(string title, Money basePrice, int row)
    {
        if (row >= 10)
        {
            return new VipTicket(title, basePrice, row);
        }
        return new StandardTicket(title, basePrice, row);
    }

    public List<ITicket> SellAll(string title, Money basePrice, IReadOnlyList<int> rows)
    {
        var tickets = new List<ITicket>();
        foreach (var row in rows)
        {
            tickets.Add(row >= 10
                ? new VipTicket(title, basePrice, row)
                : new StandardTicket(title, basePrice, row));
        }
        return tickets;
    }
}
