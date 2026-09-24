using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Start;

/// <summary>Start: klient pracuje na typie bazowym i sprawdza <c>is</c>, zanim wywoła UpgradeToVip().</summary>
public sealed class BoxOffice
{
    public Money Sell(string kind, Money basePrice, bool vip)
    {
        Ticket ticket = "STUDENT" == kind
            ? new StudentTicket(basePrice)
            : new StandardTicket(basePrice);
        if (vip && ticket is StandardTicket)
        {
            ticket.UpgradeToVip();
        }
        return ticket.Price();
    }
}
