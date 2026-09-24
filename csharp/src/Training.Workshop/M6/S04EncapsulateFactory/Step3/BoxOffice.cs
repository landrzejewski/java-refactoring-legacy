using Training.Workshop.M6.S04EncapsulateFactory.Step3.Ticketing;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step3;

/// <summary>Krok 3: klient bez zmian - zmieniła się tylko widoczność klas biletów.</summary>
public sealed class BoxOffice
{
    public ITicket Sell(string title, Money basePrice, int row)
    {
        return Tickets.ForSeat(title, basePrice, row);
    }

    public List<ITicket> SellAll(string title, Money basePrice, IReadOnlyList<int> rows)
    {
        var tickets = new List<ITicket>();
        foreach (var row in rows)
        {
            tickets.Add(Sell(title, basePrice, row));
        }
        return tickets;
    }
}
