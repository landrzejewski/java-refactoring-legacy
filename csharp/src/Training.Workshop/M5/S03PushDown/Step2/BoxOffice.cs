using Training.Workshop.Shared;

namespace Training.Workshop.M5.S03PushDown.Step2;

/// <summary>Krok 2: bez zmian - klient już pracuje na podtypie.</summary>
public sealed class BoxOffice
{
    public Money Sell(string kind, Money basePrice, bool vip)
    {
        if ("STUDENT" == kind)
        {
            return new StudentTicket(basePrice).Price();
        }
        var ticket = new StandardTicket(basePrice);
        if (vip)
        {
            ticket.UpgradeToVip();
        }
        return ticket.Price();
    }
}
