using Training.Workshop.M6.S04EncapsulateFactory.Step1.Ticketing;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step1;

/// <summary>
/// Krok 1: każde new zastąpione wywołaniem metody tworzącej. Klient nie używa już
/// klas konkretnych, ale nadal zna regułę VIP.
/// </summary>
public sealed class BoxOffice
{
    public ITicket Sell(string title, Money basePrice, int row)
    {
        if (row >= 10)
        {
            return Tickets.Vip(title, basePrice, row);
        }
        return Tickets.Standard(title, basePrice, row);
    }

    public List<ITicket> SellAll(string title, Money basePrice, IReadOnlyList<int> rows)
    {
        var tickets = new List<ITicket>();
        foreach (var row in rows)
        {
            tickets.Add(row >= 10 ? Tickets.Vip(title, basePrice, row) : Tickets.Standard(title, basePrice, row));
        }
        return tickets;
    }
}
