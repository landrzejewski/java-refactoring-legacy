using Training.Workshop.M6.S04EncapsulateFactory.Step2.Ticketing;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step2;

/// <summary>Krok 2: klient prosi o bilet dla miejsca - reguła VIP jest w jednym miejscu.</summary>
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
